<template>
  <div>
    <el-row class="row">
      <el-col :span="12">
        <el-input v-model="searchKeyword" placeholder="搜索课程名/申请人"></el-input>
      </el-col>
    </el-row>
    <el-table ref="table"
              :cell-style="{'text-align':'center'}"
              :data="sortedApprovalList"
              :header-cell-style="{backgroundColor:'#D9AFD9','text-align':'center'}"
              cell-class-name="cell-class-name"
              class="table"
              style="margin-top: 20px;">
      <el-table-column label="课程名" prop="course_title" sortable></el-table-column>
      <el-table-column label="申请人" prop="stu_name" sortable></el-table-column>
      <el-table-column label="驳回原因（若驳回）" prop="reason" sortable></el-table-column>
      <el-table-column label="身份" prop="identity" sortable></el-table-column>
      <el-table-column label="审批情况" prop="situation" sortable></el-table-column>
      <el-table-column label="审批时间" prop="create_time" sortable></el-table-column>
      <el-table-column label="证明">
        <template v-slot="scope">
          <el-button class="btn" type="primary" size="default"
                     v-if="'proof_path' in scope.row"
                     @click="getProof(scope.row)">
            查看证明
          </el-button>
          <el-button class="btn" type="primary" size="default"
                     v-else disabled>
            无证明
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button class="btn" type="warning" size="default"
                     @click="updateApproval(scope.row)"
                     v-if="scope.row.pass===0">
            修改
          </el-button>
          <el-button class="btn" type="danger" size="default"
                     @click="deleteApproval(scope.row)">
            撤销
          </el-button>
        </template>
      </el-table-column>
      <el-table-column v-if="false" label="ID" prop="id"></el-table-column>
    </el-table>
    <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        layout="prev, pager, next"
        :hide-on-single-page="false"
        :total="this.approvalList.length"
        @current-change="handleCurrentChange"
    ></el-pagination>

    <el-dialog class="dialog" v-model="dialogVisible" title="证明文件："
               align-center destroy-on-close>
      <img class="image" :src="imageUrl" alt="证明文件">
      <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">确认</el-button>
      </span>
      </template>
    </el-dialog>

  </div>
</template>

<script>
import request from "@/api";
import {eventBus} from "@ai-zen/event-bus";

export default {
  name: 'approved',
  created() {
    this.getApprovalList();
    eventBus.emit('updateSelected', '/approved');
  },
  data() {
    return {
      searchKeyword: '',
      sortType: '',
      currentPage: 1,
      pageSize: 5,
      approvalList: [],
      imageUrl: '',
      dialogVisible: false,
    };
  },
  methods: {
    getApprovalList() {
      // 获取审批信息
      request.post('/approvalListT', JSON.stringify(this.$route.query.account)).then(res => {
        if (res.data !== "") {
          this.approvalList = res.data;
          for (let i = 0; i < this.approvalList.length; i++) {
            this.approvalList[i].identity = this.indexMethod(this.approvalList[i].sequence + 1);
            this.approvalList[i].situation = this.approvalList[i].pass === 1 ? '通过' : '驳回';
          }
        } else {
          this.$message({
            message: 'error',
            type: 'error'
          });
        }
      }).catch(err => {
        console.log(err)
      });
    },
    handleCurrentChange(val) {
      this.currentPage = val;
    },
    getProof(row){
      request.post('/getProof', row.application_id, {
        responseType: 'arraybuffer', // 指定响应类型为字节数组
      }).then(response => {
        // 将字节数组转换为Blob对象
        const blob = new Blob([response.data], {type: response.headers['content-type']});
        // 将Blob对象转换为DataURL
        // 在界面中显示图像
        this.imageUrl = URL.createObjectURL(blob);
        this.dialogVisible = true;
      }).catch((error) => {
        console.error(error);
      });
    },
    updateApproval(row) {
      if (row.progress === row.sequence + 1) {
        this.$prompt('请修改驳回理由：', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: "warning",
          inputValue: row.reason,
          customClass: 'reset-message-box',
        }).then(({value}) => {
          request.post('/updateApprovalT', JSON.stringify({
            id: row.id,
            reason: value,
          })).then(res => {
            if (res.data === 1) {
              this.$message({
                message: '修改成功',
                type: 'success'
              });
              this.getApprovalList();
            } else {
              this.$message({
                message: '修改失败',
                type: 'error'
              });
            }
          }).catch(err => {
            console.log(err)
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消修改'
          });
        });
      } else {
        this.$message({
          message: '后续审批人已审批，无法修改',
          type: 'warning'
        });
      }
    },
    deleteApproval(row) {
      if (row.progress === row.sequence + 1) {
        //弹出确认框
        this.$confirm('此操作将撤销该申请, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          request.post('/deleteApprovalT', JSON.stringify({
            id: row.id,
            application_id: row.application_id,
          })).then(res => {
            if (res.data === 1) {
              this.$message({
                message: '撤销成功',
                type: 'success'
              });
              this.getApprovalList();
            } else {
              this.$message({
                message: '撤销失败',
                type: 'error'
              });
            }
          }).catch(err => {
            console.log(err)
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消撤销'
          });
        });
      } else {
        this.$message({
          message: '后续审批人已审批，无法撤销',
          type: 'warning'
        });
      }
    },
    //数字转化为第几
    indexMethod(index) {
      return '第' + this.toChinesNum(index) + '审批人';
    },
    toChinesNum(num) {
      let changeNum = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九']
      let unit = ['', '十', '百', '千', '万']
      num = parseInt(num)
      let getWan = (temp) => {
        let strArr = temp.toString().split('').reverse()
        let newNum = ''
        let newArr = []
        strArr.forEach((item, index) => {
          newArr.unshift(item === '0' ? changeNum[item] : changeNum[item] + unit[index])
        })
        let numArr = []
        newArr.forEach((m, n) => {
          if (m !== '零') numArr.push(n)
        })
        if (newArr.length > 1) {
          newArr.forEach((m, n) => {
            if (newArr[newArr.length - 1] === '零') {
              if (n <= numArr[numArr.length - 1]) {
                newNum += m
              }
            } else {
              newNum += m
            }
          })
        } else {
          newNum = newArr[0]
        }

        return newNum
      }
      let overWan = Math.floor(num / 10000)
      let noWan = num % 10000
      if (noWan.toString().length < 4) {
        noWan = '0' + noWan
      }
      return overWan ? getWan(overWan) + '万' + getWan(noWan) : getWan(num)
    },
  },
  computed: {
    sortedApprovalList() {
      let list = this.approvalList.slice().sort();
      if (this.searchKeyword) {
        list = list.filter((approval) =>
            (approval.course_title.toLowerCase().includes(this.searchKeyword.toLowerCase()))
            || approval.stu_name.toLowerCase().includes(this.searchKeyword.toLowerCase()));
      }
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return list.slice(start, end);
    },
  },
};
</script>

<style scoped>

:deep(input) {
  border: none;
}

.row {
  margin-top: 20px;
}

::v-deep .el-table__row {
  background-color: #97D9E1;
}

.table ::v-deep .el-table__body tr:hover > td {
  background-color: #D9AFD9 !important;
}

.btn {
  padding-top: 7px;
}

.image {
  width: 250px;
}

.dialog-footer button:first-child {
  margin-right: 20px;
}

/deep/ .dialog {
  width: 35% !important;
  background-color: #97D9E1 !important;
}

</style>
