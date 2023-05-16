<template>
  <el-container>
    <el-main>
      <el-card class="box-card login-card">
        <span class="login-title" v-if="this.$route.query.type==='add'">添加用户</span>
        <span class="login-title" v-if="this.$route.query.type==='update'">修改用户</span>
        <el-form
            ref="user"
            :model="user"
            :rules="rules"
            label-width="auto">
          <el-form-item label="账号" prop="account">
            <el-input
                v-model="user.account"
                v-if="this.$route.query.type==='add'"
                placeholder="学生以s开头，教师以t开头"></el-input>
            <el-input
                v-model="user.account"
                v-if="this.$route.query.type==='update'"
                disabled
                placeholder="学生以s开头，教师以t开头"></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input
                v-model="user.name"
                placeholder="请输入姓名"></el-input>
          </el-form-item>
          <el-form-item label="学院" prop="college">
            <el-input
                v-model="user.college"
                placeholder="请输入学院"></el-input>
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-radio-group v-model="user.role" :disabled="this.$route.query.type==='update'">
              <el-radio-button label="学生">学生</el-radio-button>
              <el-radio-button label="教师">教师</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addUser" v-if="this.$route.query.type==='add'">添加</el-button>
            <el-button type="primary" @click="updateUser" v-if="this.$route.query.type==='update'">修改</el-button>
            <el-button type="primary" @click="back">返回</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>

import request from "@/api";
import router from "@/router";
import {eventBus} from "@ai-zen/event-bus";

export default {
  name: "user",
  inject: ['reload'],
  data() {
    return {
      user: {
        account: this.$route.query.userAccount,
        name: this.$route.query.name,
        college: this.$route.query.college,
        role: this.$route.query.role,
      },
      preUser: {
        account: this.$route.query.userAccount,
        name: this.$route.query.name,
        college: this.$route.query.college,
        role: this.$route.query.role,
      },
      rules: {
        account: [
          {required: true, message: '昵称不能为空', trigger: 'blur'},
          {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}
        ],
        name: [
          {required: true, message: '姓名不能为空', trigger: 'blur'},
          {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}
        ],
        college: [
          {required: true, message: '学院不能为空', trigger: 'blur'},
          {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}
        ],
      }
    };
  },
  methods: {
    back() {
      router.push({
        path: '/userManage',
        query: {
          account: this.$route.query.account,
        },
      });
      eventBus.emit('updateSelected', '/userManage');
    },
    addUser() {
      this.$refs.user.validate((valid) => {
        if (valid && (this.user.account.startsWith('t') && this.user.role === '教师' ||
            this.user.account.startsWith('s') && this.user.role === '学生')) {
          request.post('/addUserA', JSON.stringify({
            account: this.user.account,
            name: this.user.name,
            college: this.user.college,
            role: this.user.role
          })).then(res => {
            if (res.data !== "") {
              if (res.data === 1) {
                this.$message({
                  message: '添加成功！',
                  type: 'success'
                });
                this.back();
              } else {
                this.$message({
                  message: '添加失败，该账号已存在',
                  type: 'error'
                });
                this.back();
              }
            } else {
              this.$message({
                message: '信息有误，请重新输入',
                type: 'error'
              });
              this.reload();
            }
          }).catch(err => {
            console.log(err)
          })
        } else {
          this.$message.error("信息有误，请重新输入");
          this.reload();
        }
      });
    },
    updateUser() {
      if (this.user.account === this.preUser.account && this.user.name === this.preUser.name && this.user.college === this.preUser.college && this.user.role === this.preUser.role) {
        this.$message.error("信息未修改，无效操作");
        this.reload();
      } else {
        this.$refs.user.validate((valid) => {
          if (valid && (this.user.account.startsWith('t') && this.user.role === '教师' ||
              this.user.account.startsWith('s') && this.user.role === '学生')) {
            request.post('/updateUserA', JSON.stringify({
              name: this.user.name,
              college: this.user.college,
              role: this.user.role,
              id: this.$route.query.id
            })).then(res => {
              if (res.data !== "") {
                if (res.data === 1) {
                  this.$message({
                    message: '修改成功！',
                    type: 'success'
                  });
                  this.back();
                } else {
                  this.$message({
                    message: '修改失败',
                    type: 'error'
                  });
                  this.back();
                }
              } else {
                this.$message({
                  message: '信息有误，请重新输入',
                  type: 'error'
                });
                this.reload();
              }
            }).catch(err => {
              console.log(err)
            })
          } else {
            this.$message.error("信息有误，请重新输入");
            this.reload();
          }
        });
      }
    },
  }
};
</script>

<style scoped>

:deep(input) {
  border: none;
}

el-main {
  position: center;
}

.login-card {
  background-color: #8BC6EC;
  background-image: linear-gradient(135deg, #8BC6EC 0%, #9599E2 100%);
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  margin: auto;
  width: 480px;
  height: 480px;
  padding: 10px;
  border: 2px solid #4158D0;
  border-radius: 24px;
}

.login-title {
  font-size: 24px;
  color: #333333;
  font-weight: 800;
  display: block;
  text-align: center;
  margin-bottom: 30px;
}

.item {
  margin-bottom: 40px;
}

#radios {
  height: 10px;
  margin-left: 125px;
}

.el-form-item {
  margin-bottom: 40px;
}

.el-form-item__label {
  font-size: 20px;
  padding-top: 8px;
  font-weight: 400;
  color: #333333;
  letter-spacing: 1px;
  display: block;
  text-align: center;
}

.el-button {
  font-size: 16px;

  display: block;
  text-align: center;
  border-radius: 5px;
  border: 1px solid #4158D0;
  margin: auto;
}

:deep(input) {
  width: 340px;
  border: none;
}

</style>