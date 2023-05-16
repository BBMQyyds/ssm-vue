<template>
  <div class="main">
    <el-row class="row">
      <el-col :span="12">
        <el-input v-model="searchKeyword" placeholder="搜索课程"></el-input>
      </el-col>
      <el-col :span="12" class="text-right">
        <el-button type="primary" @click="addCourse">添加课程</el-button>
        <el-button type="primary" @click="exportTable">导出表格</el-button>
      </el-col>
    </el-row>
    <el-table
        ref="table"
        :cell-style="{'text-align':'center'}"
        :data="sortedCourseList"
        :header-cell-style="{backgroundColor:'#D9AFD9','text-align':'center'}"
        cell-class-name="cell-class-name"
        class="table"
        style="margin-top: 20px;"
        @row-click="handleClick">
      <el-table-column label="课程名" prop="title" sortable></el-table-column>
      <el-table-column label="分类" prop="type" sortable></el-table-column>
      <el-table-column label="主讲教师" prop="speaker_name" sortable></el-table-column>
      <el-table-column label="主管教师" prop="supervisor_name" sortable></el-table-column>
      <el-table-column label="描述" prop="description"></el-table-column>
      <el-table-column label="申请数" prop="apply_count" sortable></el-table-column>
      <el-table-column label="创建时间" prop="create_time" sortable></el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button type="warning" size="default" @click="updateCourse(scope.row); $event.stopPropagation();">修改
          </el-button>
          <el-button type="danger" size="default" @click="deleteCourse(scope.row); $event.stopPropagation();">删除
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
        :total="this.courseList.length"
        @current-change="handleCurrentChange"
    ></el-pagination>

    <el-table
        id="out-table" hidden :data="this.courseList">
      <el-table-column label="课程名" prop="title" sortable></el-table-column>
      <el-table-column label="分类" prop="type" sortable></el-table-column>
      <el-table-column label="主讲教师" prop="speaker_name" sortable></el-table-column>
      <el-table-column label="主管教师" prop="supervisor_name" sortable></el-table-column>
      <el-table-column label="描述" prop="description"></el-table-column>
      <el-table-column label="申请数" prop="apply_count" sortable></el-table-column>
      <el-table-column label="创建时间" prop="create_time" sortable></el-table-column>
    </el-table>

  </div>
</template>

<script>
import request from "@/api";
import {eventBus} from "@ai-zen/event-bus";
import router from "@/router";
import FileSaver from 'file-saver';
import XLSX from 'xlsx';

export default {
  name: 'courseManage',
  created() {
    // 获取课程列表
    this.getCourseList();
    eventBus.emit('updateSelected', '/courseManage');
  },
  data() {
    return {
      searchKeyword: '',
      sortType: '',
      currentPage: 1,
      pageSize: 5,
      courseList: [],
    };
  },
  methods: {
    handleClick(row) {
      router.push({
        path: '/courseManage/flow',
        query: {
          id: row.id,
          account: this.$route.query.account,
        },
      });
      eventBus.emit('updateSelected', '/courseManage');
    },
    getCourseList() {
      // 获取课程信息
      request.post('/selectCourseListA', JSON.stringify(this.$route.query.account)).then(res => {
        if (res.data !== "") {
          this.courseList = res.data;
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
    addCourse() {
      router.push({
        path: '/courseManage/course',
        query: {
          type: 'add',
          account: this.$route.query.account,
        },
      });
      eventBus.emit('updateSelected', '/courseManage');
    },
    updateCourse(row) {
      router.push({
        path: '/courseManage/course',
        query: {
          type: 'update',
          account: this.$route.query.account,
          courseType: row.type,
          title: row.title,
          description: row.description,
          speaker: row.speaker_name,
          supervisor: row.supervisor_name,
          id: row.id,
        },
      });
    },
    deleteCourse(row) {
      //弹出确认框
      this.$confirm('此操作将永久删除该课程, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post('/deleteCourseA', JSON.stringify(row.id)).then(res => {
          if (res.data === 1) {
            this.$message({
              message: '删除成功',
              type: 'success'
            });
            // 获取课程信息
            this.getCourseList();
          } else {
            this.$message({
              message: '删除失败',
              type: 'error'
            });
          }
        }).catch(err => {
          console.log(err)
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
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
            "courses.xlsx"
        );
      } catch (e) {
        if (typeof console !== "undefined") console.log(e, wbout);
      }
      return wbout;
    },
  },
  computed: {
    sortedCourseList() {
      let list = this.courseList.slice().sort();
      if (this.searchKeyword) {
        list = list.filter((course) =>
            (course.title.toLowerCase().includes(this.searchKeyword.toLowerCase()))
            || course.type.toLowerCase().includes(this.searchKeyword.toLowerCase())
            || course.speaker_name.toLowerCase().includes(this.searchKeyword.toLowerCase())
            || course.supervisor_name.toLowerCase().includes(this.searchKeyword.toLowerCase()));
      }
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return list.slice(start, end);
    },
  },
};
</script>

<style scoped>

.main {
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

.el-button {
  padding-top: 8px;
}

</style>
