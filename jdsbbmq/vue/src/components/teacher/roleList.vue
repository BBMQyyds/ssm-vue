<template>
  <div>
    <el-row class="row">
      <el-col :span="12">
        <el-input v-model="searchKeyword" placeholder="搜索课程名"></el-input>
      </el-col>
    </el-row>
    <el-table ref="table"
              :cell-style="{'text-align':'center'}"
              :data="sortedRoleList"
              :header-cell-style="{backgroundColor:'#D9AFD9','text-align':'center'}"
              cell-class-name="cell-class-name"
              class="table"
              style="margin-top: 20px;">
      <el-table-column label="课程名" prop="course_title" sortable></el-table-column>
      <el-table-column label="分类" prop="course_type" sortable></el-table-column>
      <el-table-column label="主讲教师" prop="speaker_name" sortable></el-table-column>
      <el-table-column label="主管教师" prop="supervisor_name" sortable></el-table-column>
      <el-table-column label="身份" prop="identity" sortable></el-table-column>
      <el-table-column label="绑定时间" prop="create_time" sortable></el-table-column>
      <el-table-column v-if="false" label="ID" prop="id"></el-table-column>
    </el-table>
    <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        layout="prev, pager, next"
        :hide-on-single-page="false"
        :total="this.roleList.length"
        @current-change="handleCurrentChange"
    ></el-pagination>
  </div>
</template>

<script>
import request from "@/api";
import {eventBus} from "@ai-zen/event-bus";

export default {
  name: 'role',
  created() {
    this.getRoleList();
    eventBus.emit('updateSelected', '/approve');
  },
  data() {
    return {
      searchKeyword: '',
      sortType: '',
      currentPage: 1,
      pageSize: 5,
      roleList: [],
    };
  },
  methods: {
    getRoleList() {
      //获取权限信息
      request.post('/roleListT', JSON.stringify(this.$route.query.account)).then(res => {
        if (res.data !== "") {
          this.roleList = res.data;
          for (let i = 0; i < this.roleList.length; i++) {
            this.roleList[i].identity = this.indexMethod(this.roleList[i].sequence);
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
    sortedRoleList() {
      let list = this.roleList.slice().sort();
      if (this.searchKeyword) {
        list = list.filter((role) =>
            (role.course_title.toLowerCase().includes(this.searchKeyword.toLowerCase())));
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
