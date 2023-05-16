<template>
  <div>
    <el-row class="row">
      <el-col :span="12">
        <el-input v-model="searchKeyword" placeholder="搜索课程或教师"></el-input>
      </el-col>
    </el-row>
    <el-table ref="table"
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
  </div>
</template>

<script>
import request from "@/api";
import {eventBus} from "@ai-zen/event-bus";
import router from "@/router";

export default {
  name: 'apply',
  created() {
    // 获取课程信息
    request.post('/courseListS', JSON.stringify(this.$route.query.account)).then(res => {
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
    eventBus.emit('updateSelected', '/apply');
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
    handleCurrentChange(val) {
      this.currentPage = val;
    },
    handleClick(row) {
      router.push({
        path: '/application',
        query: {
          id: row.id,
          account: this.$route.query.account,
        },
      });
      eventBus.emit('updateSelected', '/application');
    },
  },
  computed: {
    sortedCourseList() {
      let list = this.courseList.slice().sort();
      if (this.searchKeyword) {
        list = list.filter((course) =>
            (course.title.toLowerCase().includes(this.searchKeyword.toLowerCase()))
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


</style>
