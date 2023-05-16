<template>
  <el-container>
    <el-main>
      <el-card class="box-card login-card">
        <span class="login-title">JavaWeb在线申请和审批系统</span>
        <span class="login-tip">20217033 梁朝阳</span>
        <span class="login-tip2">初始密码：123456</span>
        <el-form
            ref="user"
            :model="user"
            :rules="rules"
            label-width="auto">
          <el-form-item class="item" label="账号" prop="account">
            <el-input
                v-model="user.account"
                placeholder="请输入账号">
            </el-input>
          </el-form-item>

          <el-form-item class="item" label="密码" prop="pwd">
            <el-input
                v-model="user.pwd"
                placeholder="请输入密码"
                show-password
                type="password">
            </el-input>
          </el-form-item>
          <el-form-item id="radios" class="item">
            <el-checkbox v-model="memory">记住密码</el-checkbox>
            <el-checkbox v-model="auto">自动登录</el-checkbox>
          </el-form-item>
          <el-form-item class="item">
            <el-button id="left" type="primary" @click="login">登录</el-button>
            <el-button id="right" type="primary" @click="register">注册</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>

import request from "@/api";
import router from "@/router";
import CryptoJS from 'crypto-js'
import {eventBus} from "@ai-zen/event-bus";

export default {
  name: "login",
  inject: ['reload'],
  created() {
    if (localStorage.getItem("account") !== null && localStorage.getItem("pwd") !== null) {
      this.user.account = localStorage.getItem("account");
      this.user.pwd = localStorage.getItem("pwd");
      this.memory = true;
    }
    if (localStorage.getItem("auto") !== null) {
      this.auto = true;
    }
  },
  mounted() {
    if (localStorage.getItem("auto") !== null) {
      this.login();
      this.auto = true;
    }
  },
  data() {
    return {
      name: '',
      user: {
        account: "",
        pwd: ""
      },
      memory: false,
      auto: false,
      rules: {
        account: [
          {required: true, message: '昵称不能为空', trigger: 'blur'},
          {min: 2, max: 20, message: '长度在 2 到 23 个字符', trigger: 'blur'}
        ],
        pwd: [
          {required: true, message: '密码不能为空', trigger: 'blur'},
          {min: 6, max: 32, message: '长度在 6 到 32 个字符', trigger: 'blur'}
        ],
      }
    };
  },
  methods: {
    login() {
      //进行表单验证
      this.$refs.user.validate(valid => {
        //表单验证成功
        if (valid) {
          request.post('/login', JSON.stringify({
            account: this.user.account,
            pwd: CryptoJS.MD5(this.user.pwd).toString(),
          })).then(async res => {
            if ('account' in res.data) {
              this.$message({
                message: '登录成功！',
                type: 'success',
                offset: 50,
                duration: 500
              });

              //记住密码
              if (this.memory) {
                localStorage.setItem("account", this.user.account);
                localStorage.setItem("pwd", this.user.pwd);
              } else {
                localStorage.removeItem("account");
                localStorage.removeItem("pwd");
              }
              //自动登录
              if (this.auto) {
                localStorage.setItem("auto", "true");
              } else {
                localStorage.removeItem("auto");
              }
              sessionStorage.setItem("account", this.user.account);
              sessionStorage.setItem("pwd", this.user.pwd);

              //获得用户名字
              await request.post('/getUserName', JSON.stringify(this.user.account)).then(res2 => {
                if (res2.data !== '') {
                  sessionStorage.removeItem("name");
                  sessionStorage.setItem("name", res2.data);
                } else {
                  this.$message({
                    type: 'error',
                    message: '获取用户名字失败'
                  });
                }
              }).catch(err => {
                console.log(err)
              })

              if (res.data.account.startsWith('s')) {
                await router.push({
                  path: '/apply',
                  query: {
                    account: res.data.account,
                  },
                });
                eventBus.emit('updateSelected', '/apply');
              } else if (res.data.account.startsWith('t')) {
                await router.push({
                  path: '/approve',
                  query: {
                    account: res.data.account,
                  },
                });
                eventBus.emit('updateSelected', '/approve');
              } else if (res.data.account.startsWith('a')) {
                await router.push({
                  path: '/userManage',
                  query: {
                    account: res.data.account,
                  },
                });
                eventBus.emit('updateSelected', '/userManage');
              }
            } else {
              this.$message({
                message: '昵称或密码不正确，请重新输入',
                type: 'error'
              });
              this.reload();
            }
          }).catch(err => {
            console.log(err)
          })
        } else {
          this.$message({
            message: '输入格式不正确，请重新输入',
            type: 'error'
          });
          this.reload();
        }
      });
    },
    register() {
      router.push({name: 'register'})
    },
  }
};
</script>

<style scoped>

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
  width: 490px;
  height: 450px;
  padding: 50px;
  border: 2px solid #4158D0;
  border-radius: 24px;
}

.login-title {
  font-size: 32px;
  color: #333333;
  line-height: 60px;
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
}

.login-tip2 {
  font-size: 20px;
  font-weight: 400;
  color: #999999;
  display: block;
  text-align: center;
  margin-bottom: 30px;
}

#left {
  margin-top: 20px;
  margin-right: 100px;
  margin-left: 50px;
}

#right {
  margin-top: 20px;
  margin-left: 100px;
  margin-right: 50px;
}

.item {
  margin-bottom: 40px;
}

:deep(.el-form-item__label) {
  font-size: 20px;
  font-weight: 400;
  color: #333333;
  letter-spacing: 1px;
  display: block;
  text-align: center;
}

:deep(input) {
  width: 325px;
  border: none;
}

.el-button {
  font-size: 16px;
  padding-top: 8px;
  display: block;
  text-align: center;
  border-radius: 5px;
  border: 1px solid #4158D0;
}

#radios {
  height: 10px;
  margin-left: 125px;
}

</style>