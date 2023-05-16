<template>
  <div class="main">
    <el-row class="row">
      <el-col :span="12">
        <el-input v-model="searchKeyword" placeholder="搜索用户"></el-input>
      </el-col>
      <el-col :span="12" class="text-right">
        <el-button type="primary" @click="addUser">添加用户</el-button>
        <el-button type="primary" @click="exportTable">导出表格</el-button>
      </el-col>
    </el-row>
    <el-table
        ref="table"
        :cell-style="{'text-align':'center'}"
        :data="sortedUserList"
        :header-cell-style="{backgroundColor:'#D9AFD9','text-align':'center'}"
        cell-class-name="cell-class-name"
        class="table"
        style="margin-top: 20px;">
      <el-table-column label="账号" prop="account" sortable></el-table-column>
      <el-table-column label="姓名" prop="name" sortable></el-table-column>
      <el-table-column label="权限" prop="role" sortable></el-table-column>
      <el-table-column label="学院" prop="college" sortable></el-table-column>
      <el-table-column label="创建时间" prop="create_time" sortable></el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button type="warning" size="default" @click="updateUser(scope.row)">修改</el-button>
          <el-button type="danger" size="default" @click="deleteUser(scope.row)">删除</el-button>
        </template>
      </el-table-column>
      <el-table-column v-if="false" label="ID" prop="id"></el-table-column>
    </el-table>
    <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        layout="prev, pager, next"
        :hide-on-single-page="false"
        :total="this.userList.length"
        @current-change="handleCurrentChange"
    ></el-pagination>

    <el-table
        id="out-table" hidden :data="this.userList">
      <el-table-column label="账号" prop="account" sortable></el-table-column>
      <el-table-column label="姓名" prop="name" sortable></el-table-column>
      <el-table-column label="权限" prop="role" sortable></el-table-column>
      <el-table-column label="学院" prop="college" sortable></el-table-column>
      <el-table-column label="创建时间" prop="create_time" sortable></el-table-column>
    </el-table>

  </div>
</template>

<script>
import request from "@/api";
import {eventBus} from "@ai-zen/event-bus";
import router from "@/router";
import FileSaver from "file-saver";
import XLSX from "xlsx";

export default {
  name: 'userManage',
  created() {
    // 获取用户列表
    this.getUserList();
    eventBus.emit('updateSelected', '/userManage');
  },
  data() {
    return {
      searchKeyword: '',
      sortType: '',
      currentPage: 1,
      pageSize: 5,
      userList: [],
    };
  },
  methods: {
    handleCurrentChange(val) {
      this.currentPage = val;
    },
    getUserList() {
      // 获取用户信息
      request.post('/userListA', JSON.stringify(this.$route.query.account)).then(res => {
        if (res.data !== "") {
          this.userList = res.data;
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
    addUser() {
      router.push({
        path: '/userManage/user',
        query: {
          type: 'add',
          account: this.$route.query.account,
        },
      });
      eventBus.emit('updateSelected', '/userManage');
    },
    updateUser(row) {
      router.push({
        path: '/userManage/user',
        query: {
          type: 'update',
          account: this.$route.query.account,
          userAccount: row.account,
          name: row.name,
          role: row.role,
          college: row.college,
          id: row.id,
        },
      });
    },
    deleteUser(row) {
      //弹出确认框
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post('/deleteUserA', JSON.stringify(row.id)).then(res => {
          if (res.data === 1) {
            this.$message({
              message: '删除成功',
              type: 'success'
            });
            // 刷新页面
            this.getUserList();
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
            "users.xlsx"
        );
      } catch (e) {
        if (typeof console !== "undefined") console.log(e, wbout);
      }
      return wbout;
    },
  },
  computed: {
    sortedUserList() {
      let list = this.userList.slice().sort();
      if (this.searchKeyword) {
        list = list.filter((user) =>
            (user.account.toLowerCase().includes(this.searchKeyword.toLowerCase()))
            || user.name.toLowerCase().includes(this.searchKeyword.toLowerCase())
            || user.role.toLowerCase().includes(this.searchKeyword.toLowerCase())
            || user.college.toLowerCase().includes(this.searchKeyword.toLowerCase()));
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
  width: 1200px;
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
