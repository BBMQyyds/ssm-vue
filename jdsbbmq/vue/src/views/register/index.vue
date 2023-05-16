<template>
  <el-container>
    <el-main>
      <el-card class="box-card login-card">
        <span class="login-title">JavaWeb在线申请和审批系统</span>
        <span class="login-tip">20217033 梁朝阳</span>
        <el-form
            ref="user"
            :model="user"
            :rules="rules"
            label-width="auto">
          <el-form-item label="账号" prop="account">
            <el-input
                v-model="user.account"
                placeholder="学生以s开头，教师以t开头"></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input
                v-model="user.name"
                placeholder="请输入姓名"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="pwd">
            <el-input
                v-model="user.pwd"
                placeholder="请输入密码"
                show-password
                type="password"></el-input>
          </el-form-item>
          <el-form-item label="验证" prop="pwd2">
            <el-input
                v-model="user.pwd2"
                placeholder="请验证密码"
                show-password
                type="password"></el-input>
          </el-form-item>
          <el-form-item label="学院" prop="college">
            <el-input
                v-model="user.college"
                placeholder="请输入学院"></el-input>
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-radio-group v-model="user.role">
              <el-radio-button label="学生">学生</el-radio-button>
              <el-radio-button label="教师">教师</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="register">注册</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>

import request from "@/api";
import router from "@/router";
import CryptoJS from "crypto-js";

export default {
  name: "register",
  inject: ['reload'],
  data() {
    return {
      user: {
        account: "",
        name: "",
        pwd: "",
        pwd2: "",
        college: "",
        role: "学生"
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
        pwd: [
          {required: true, message: '密码不能为空', trigger: 'blur'},
          {min: 6, max: 32, message: '长度在 6 到 32 个字符', trigger: 'blur'}
        ],
        pwd2: [
          {required: true, message: '密码不能为空', trigger: 'blur'},
          {min: 6, max: 32, message: '长度在 6 到 32 个字符', trigger: 'blur'}
        ],
        college: [
          {required: true, message: '学院不能为空', trigger: 'blur'},
          {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}
        ],
      }
    };
  },
  methods: {
    register() {
      this.$refs.user.validate((valid) => {
        if (valid && (this.user.account.startsWith('t') && this.user.role === '教师' ||
            this.user.account.startsWith('s') && this.user.role === '学生')) {
          if (this.user.pwd !== this.user.pwd2) {
            this.$message.error("两次密码不一致，请重新输入");
            this.reload();
          } else {
            request.post('/register', JSON.stringify({
              account: this.user.account,
              name: this.user.name,
              pwd:  CryptoJS.MD5(this.user.pwd).toString(),
              college: this.user.college,
              role: this.user.role
            })).then(res => {
              if (res.data !== "") {
                if (res.data === 1) {
                  this.$message({
                    message: '注册成功！',
                    type: 'success'
                  });
                  router.push({name: 'login'});
                } else {
                  this.$message({
                    message: '注册失败，该账号已存在',
                    type: 'error'
                  });
                  this.reload();
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
          }
        } else {
          this.$message.error("信息有误，请重新输入");
          this.reload();
        }
      });
    }
  }
};
</script>

<style scoped>

.el-input {
  border: none;
  height: 35px;
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
  height: 630px;
  padding: 40px;
  border: 2px solid #4158D0;
  border-radius: 24px;
}

.login-title {
  font-size: 32px;
  color: #333333;
  line-height: 50px;
  letter-spacing: 1px;
  font-weight: 800;
  display: block;
  text-align: center;
}

.login-tip {
  font-size: 30px;
  font-weight: 400;
  color: #999999;
  line-height: 60px;
  letter-spacing: 1px;
  display: block;
  text-align: center;
  margin-bottom: 20px;
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