<template>
  <div class="approval">
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
        :data="sortedApprovalList"
        :header-cell-style="{backgroundColor:'#D9AFD9','text-align':'center'}"
        cell-class-name="cell-class-name"
        class="table"
        style="margin-top: 20px;">
      <el-table-column label="审批人" prop="teacher_name" sortable></el-table-column>
      <el-table-column label="课程名" prop="course_title" sortable></el-table-column>
      <el-table-column label="申请人" prop="stu_name" sortable></el-table-column>
      <el-table-column label="驳回原因（若驳回）" prop="reason" sortable></el-table-column>
      <el-table-column label="身份" prop="identity" sortable></el-table-column>
      <el-table-column label="审批情况" prop="situation" sortable></el-table-column>
      <el-table-column label="审批时间" prop="create_time" sortable></el-table-column>
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

    <el-table
        id="out-table" hidden :data="this.approvalList">
      <el-table-column label="审批人" prop="teacher_name" sortable></el-table-column>
      <el-table-column label="课程名" prop="course_title" sortable></el-table-column>
      <el-table-column label="申请人" prop="stu_name" sortable></el-table-column>
      <el-table-column label="驳回原因（若驳回）" prop="reason" sortable></el-table-column>
      <el-table-column label="身份" prop="identity" sortable></el-table-column>
      <el-table-column label="审批情况" prop="situation" sortable></el-table-column>
      <el-table-column label="审批时间" prop="create_time" sortable></el-table-column>
    </el-table>

  </div>
</template>

<script>
import request from "@/api";
import {eventBus} from "@ai-zen/event-bus";
import FileSaver from "file-saver";
import XLSX from "xlsx";

export default {
  name: 'queryApproval',
  created() {
    this.getApprovalList();
    eventBus.emit('updateSelected', '/queryApproval');
  },
  data() {
    return {
      searchKeyword: '',
      sortType: '',
      currentPage: 1,
      pageSize: 5,
      approvalList: [],
    };
  },
  methods: {
    getApprovalList() {
      // 获取审批信息
      request.post('/approvalListAllA', JSON.stringify(this.$route.query.account)).then(res => {
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
    exportTable() {
      /* 从表生成工作簿对象 */
      console.log(XLSX);
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
            "approvals.xlsx"
        );
      } catch (e) {
        if (typeof console !== "undefined") console.log(e, wbout);
      }
      return wbout;
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
            || approval.teacher_name.toLowerCase().includes(this.searchKeyword.toLowerCase()));
      }
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return list.slice(start, end);
    },
  },
};
</script>

<style scoped>

.approval {
  width: 1400px;
  margin: auto;
}

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

</style>
