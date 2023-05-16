<template>
  <div>
    <el-row class="row">
      <el-col :span="12">
        <el-input v-model="searchKeyword" placeholder="搜索课程名/申请人"></el-input>
      </el-col>
      <el-col :span="12" class="text-right">
        <el-button type="primary" @click="exportTable">导出</el-button>
      </el-col>
    </el-row>
    <el-table
        ref="table"
        :cell-style="{'text-align':'center'}"
        :data="sortedApplicationList"
        :header-cell-style="{backgroundColor:'#D9AFD9','text-align':'center'}"
        cell-class-name="cell-class-name"
        class="table"
        style="margin-top: 20px;">
      <el-table-column label="申请人" prop="stu_name" sortable></el-table-column>
      <el-table-column label="课程名" prop="title" sortable></el-table-column>
      <el-table-column label="分类" prop="type" sortable></el-table-column>
      <el-table-column label="主讲教师" prop="speaker_name" sortable></el-table-column>
      <el-table-column label="主管教师" prop="supervisor_name" sortable></el-table-column>
      <el-table-column label="申请原因" prop="apply_reason" sortable></el-table-column>
      <el-table-column label="进度" prop="situation" sortable></el-table-column>
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

    <el-table
        id="out-table" hidden :data="this.applicationList">
      <el-table-column label="申请人" prop="stu_name" sortable></el-table-column>
      <el-table-column label="课程名" prop="title" sortable></el-table-column>
      <el-table-column label="分类" prop="type" sortable></el-table-column>
      <el-table-column label="主讲教师" prop="speaker_name" sortable></el-table-column>
      <el-table-column label="主管教师" prop="supervisor_name" sortable></el-table-column>
      <el-table-column label="申请原因" prop="apply_reason" sortable></el-table-column>
      <el-table-column label="进度" prop="situation" sortable></el-table-column>
      <el-table-column label="申请时间" prop="apply_time" sortable></el-table-column>
    </el-table>

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
import FileSaver from "file-saver";
import XLSX from "xlsx";

export default {
  name: 'queryApplication',
  created() {
    this.getApplicationList();
    eventBus.emit('updateSelected', '/queryApplication');
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
      //获取全部申请信息
      request.post('/applyListAllA', JSON.stringify(this.$route.query.account)).then(res => {
        if (res.data !== "") {
          this.applicationList = res.data;
          for (let i = 0; i < this.applicationList.length; i++) {
            if (this.applicationList[i].finish === -1) {
              this.applicationList[i].situation = '申请驳回';
            } else if (this.applicationList[i].finish === 1) {
              this.applicationList[i].situation = '申请成功';
            } else {
              if (this.applicationList[i].progress === 0) {
                this.applicationList[i].situation = '申请已提交';
              } else if (this.applicationList[i].progress === 1) {
                this.applicationList[i].situation = '主讲教师审批';
              } else if (this.applicationList[i].progress === 2) {
                this.applicationList[i].situation = '主管教师审批';
              }
            }
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
    exportTable() {
      /* 从表生成工作簿对象 */
      const wb = XLSX.utils.table_to_book(document.querySelector("#out-table"));
      /* 获取二进制字符串作为输出 */
      const wbout = XLSX.write(wb, {
        bookType: "xlsx",
        bookSST: true,
        type: "array"
      });
      try {
        FileSaver.saveAs(
            //Blob 对象表示一个不可变、原始数据的类文件对象。
            //Blob 表示的不一定是JavaScript原生格式的数据。
            //File 接口基于Blob，继承了 blob 的功能并将其扩展使其支持用户系统上的文件。
            //返回一个新创建的 Blob 对象，其内容由参数中给定的数组串联组成。
            new Blob([wbout], {type: "application/octet-stream"}),
            //设置导出文件名称
            "applications.xlsx"
        );
      } catch (e) {
        if (typeof console !== "undefined") console.log(e, wbout);
      }
      return wbout;
    },
  },
  computed: {
    sortedApplicationList() {
      let list = this.applicationList.slice().sort();
      if (this.searchKeyword) {
        list = list.filter((application) =>
            (application.title.toLowerCase().includes(this.searchKeyword.toLowerCase()))
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

.el-button {
  padding-top: 8px;
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
