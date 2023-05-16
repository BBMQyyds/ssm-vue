<template>
  <div>
    <el-row class="row">
      <el-col :span="12">
        <el-input v-model="searchKeyword" placeholder="搜索课程/教师/进度"></el-input>
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
      <el-table-column label="申请数" prop="apply_count" sortable></el-table-column>
      <el-table-column label="进度" prop="situation" sortable></el-table-column>
      <el-table-column label="申请时间" prop="apply_time" sortable></el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button class="btn" type="warning" size="default"
                     @click="updateApplication(scope.row); $event.stopPropagation();">
            修改
          </el-button>
          <el-button class="btn" type="danger" size="default"
                     @click="deleteApplication(scope.row); $event.stopPropagation();">
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
  name: 'courses',
  created() {
    this.getCourseList();
    eventBus.emit('updateSelected', '/courses');
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
    getCourseList() {
      // 获取课程信息
      request.post('/appliedCourseListS', JSON.stringify(this.$route.query.account)).then(res => {
        if (res.data !== "") {
          this.courseList = res.data;
          for (let i = 0; i < this.courseList.length; i++) {
            if (this.courseList[i].finish === -1) {
              this.courseList[i].situation = '申请驳回';
            } else if (this.courseList[i].finish === 1) {
              this.courseList[i].situation = '申请成功';
            } else {
              if (this.courseList[i].progress === 0) {
                this.courseList[i].situation = '主讲教师审批';
              } else if (this.courseList[i].progress === 1) {
                this.courseList[i].situation = '主管教师审批';
              } else if (this.courseList[i].progress === 2) {
                this.courseList[i].situation = '第三审批人审批';
              }else if (this.courseList[i].progress === 3) {
                this.courseList[i].situation = '第四审批人审批';
              }else if (this.courseList[i].progress === 4) {
                this.courseList[i].situation = '第五审批人审批';
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
    handleClick(row) {
      router.push({
        path: '/applicationDetail',
        query: {
          id: row.id,
          account: this.$route.query.account,
        },
      });
      eventBus.emit('updateSelected', '/applicationDetail');
    },
    updateApplication(row) {
      this.$prompt('请修改申请理由：', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: "warning",
        inputValue: row.apply_reason,
        customClass: 'reset-message-box',
      }).then(({value}) => {
        request.post('/updateApplicationS', JSON.stringify({
          id: row.application_id,
          reason: value,
        })).then(res => {
          if (res.data === 1) {
            this.$message({
              message: '修改成功',
              type: 'success'
            });
            this.getCourseList();
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
    },
    deleteApplication(row) {
      //弹出确认框
      this.$confirm('此操作将撤销该申请, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post('/deleteApplicationS', JSON.stringify(row.application_id)).then(res => {
          if (res.data === 1) {
            this.$message({
              message: '撤销成功',
              type: 'success'
            });
            this.getCourseList();
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
    },
  },
  computed: {
    sortedCourseList() {
      let list = this.courseList.slice().sort();
      if (this.searchKeyword) {
        list = list.filter((course) =>
            (course.title.toLowerCase().includes(this.searchKeyword.toLowerCase()))
            || course.speaker_name.toLowerCase().includes(this.searchKeyword.toLowerCase())
            || course.supervisor_name.toLowerCase().includes(this.searchKeyword.toLowerCase())
            || course.situation.toLowerCase().includes(this.searchKeyword.toLowerCase()));
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

</style>
