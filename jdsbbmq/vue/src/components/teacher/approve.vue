<template>
  <div>
    <el-row class="row">
      <el-col :span="12">
        <el-input v-model="searchKeyword" placeholder="搜索课程名/申请人"></el-input>
      </el-col>
    </el-row>
    <el-table ref="table"
              :cell-style="{'text-align':'center'}"
              :data="sortedApplicationList"
              :header-cell-style="{backgroundColor:'#D9AFD9','text-align':'center'}"
              cell-class-name="cell-class-name"
              class="table"
              style="margin-top: 20px;">
      <el-table-column label="课程名" prop="course_name" sortable></el-table-column>
      <el-table-column label="申请人" prop="stu_name" sortable></el-table-column>
      <el-table-column label="申请原因" prop="reason" sortable></el-table-column>
      <el-table-column label="身份" prop="identity" sortable></el-table-column>
      <el-table-column label="申请时间" prop="apply_time" sortable></el-table-column>
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
          <el-button class="btn" type="primary" size="default"
                     @click="passApplication(scope.row)">
            通过
          </el-button>
          <el-button class="btn" type="danger" size="default"
                     @click="failApplication(scope.row)">
            驳回
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
        :total="this.applicationList.length"
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
  name: 'approve',
  created() {
    this.getApplicationList();
    eventBus.emit('updateSelected', '/approve');
  },
  data() {
    return {
      searchKeyword: '',
      sortType: '',
      currentPage: 1,
      pageSize: 5,
      applicationList: [],
      imageUrl: '',
      dialogVisible: false,
    };
  },
  methods: {
    getApplicationList() {
      // 获取申请信息
      request.post('/applyListT', JSON.stringify(this.$route.query.account)).then(res => {
        if (res.data !== "") {
          this.applicationList = res.data;
          for (let i = 0; i < this.applicationList.length; i++) {
            this.applicationList[i].identity = this.indexMethod(this.applicationList[i].progress);
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
    getProof(row) {
      request.post('/getProof', row.id, {
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
    passApplication(row) {
      //通过申请
      //弹出确认框
      this.$confirm('此操作将通过该申请, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post('/passApplicationT', JSON.stringify({
          application_id: row.id,
          teacher_account: this.$route.query.account,
          course_id: row.course_id,
          progress: row.progress,
        })).then(res => {
          if (res.data === 1) {
            this.$message({
              message: '通过成功',
              type: 'success'
            });
            this.getApplicationList();
          } else {
            this.$message({
              message: '通过失败',
              type: 'error'
            });
          }
        }).catch(err => {
          console.log(err)
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消通过'
        });
      });
    },
    failApplication(row) {
      //驳回申请，并给出理由
      //弹出确认框
      this.$prompt('请输入驳回理由', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '请输入驳回理由'
      }).then(({value}) => {
        request.post('/failApplicationT', JSON.stringify({
          application_id: row.id,
          teacher_account: this.$route.query.account,
          reason: value,
          course_id: row.course_id,
          progress: row.progress,
        })).then(res => {
          if (res.data === 1) {
            this.$message({
              message: '驳回成功',
              type: 'success'
            });
            this.getApplicationList();
          } else {
            this.$message({
              message: '驳回失败',
              type: 'error'
            });
          }
        }).catch(err => {
          console.log(err)
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消驳回'
        });
      });
    },
    //数字转化为第几
    indexMethod(index) {
      return '第' + this.toChinesNum(index + 1) + '审批人';
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
    sortedApplicationList() {
      let list = this.applicationList.slice().sort();
      if (this.searchKeyword) {
        list = list.filter((application) =>
            (application.course_name.toLowerCase().includes(this.searchKeyword.toLowerCase()))
            || application.stu_name.toLowerCase().includes(this.searchKeyword.toLowerCase()));
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
