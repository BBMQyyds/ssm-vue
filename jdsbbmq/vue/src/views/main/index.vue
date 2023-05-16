<template>
  <div class="home">
    <el-container>
      <el-header class="head">
        <span class="title" v-if="account.startsWith('s')">JavaWeb在线申请系统</span>
        <span class="title" v-if="account.startsWith('t')">JavaWeb在线审批系统</span>
        <span class="title" v-if="account.startsWith('a')">JavaWeb在线后台系统</span>
      </el-header>
      <el-container>
        <el-aside width="160px">
          <el-menu
              v-model="activeIndex"
              :default-active="activeIndex"
              class="side">
            <router-link :to="{path:'/apply',query:{account}}" v-if="account.startsWith('s')">
              <el-menu-item index="/apply">
                <i class="el-icon-house"></i>
                <span>课程选择</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/courses',query:{account}}" v-if="account.startsWith('s')">
              <el-menu-item index="/courses">
                <i class="el-icon-collection"></i>
                <span>已申请课程</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/application',query:{account}}" v-if="account.startsWith('s')">
              <el-menu-item index="/application">
                <i class="el-icon-position"></i>
                <span>课程申请</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/applicationDetail',query:{account}}" v-if="account.startsWith('s')">
              <el-menu-item index="/applicationDetail">
                <i class="el-icon-document"></i>
                <span>申请详情</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/approve',query:{account}}" v-if="account.startsWith('t')">
              <el-menu-item index="/approve">
                <i class="el-icon-house"></i>
                <span>申请审批</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/approved',query:{account}}" v-if="account.startsWith('t')">
              <el-menu-item index="/approved">
                <i class="el-icon-collection"></i>
                <span>已审批申请</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/roleList',query:{account}}" v-if="account.startsWith('t')">
              <el-menu-item index="/roleList">
                <i class="el-icon-lock"></i>
                <span>权限列表</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/userManage',query:{account}}" v-if="account.startsWith('a')">
              <el-menu-item index="/userManage">
                <i class="el-icon-user"></i>
                <span>用户管理</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/courseManage',query:{account}}" v-if="account.startsWith('a')">
              <el-menu-item index="/courseManage">
                <i class="el-icon-tickets"></i>
                <span>课程管理</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/queryApplication',query:{account}}" v-if="account.startsWith('a')">
              <el-menu-item index="/queryApplication">
                <i class="el-icon-search"></i>
                <span>查询申请</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/queryApproval',query:{account}}" v-if="account.startsWith('a')">
              <el-menu-item index="/queryApproval">
                <i class="el-icon-search"></i>
                <span>查询审批</span>
              </el-menu-item>
            </router-link>
            <router-link :to="{path:'/about',query:{account}}">
              <el-menu-item index="/about">
                <i class="el-icon-more"></i>
                <span>关于</span>
              </el-menu-item>
            </router-link>
            <el-menu-item index="6" @click="logout">
              <i class="el-icon-switch-button"></i>
              <span>登出</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
      <el-footer class="foot">
        <span v-if="account.startsWith('s')">您好，{{ name }}同学</span>
        <span v-if="account.startsWith('t')">您好，{{ name }}老师</span>
        <span v-if="account.startsWith('a')">您好，{{ name }}管理员</span>
      </el-footer>
    </el-container>
  </div>
</template>

<script>

import 'element-ui/lib/theme-chalk/index.css';
import router from "@/router";
import {eventBus} from '@ai-zen/event-bus';

export default {
  name: "main",
  created() {
    //更改选中菜单
    eventBus.offAll("updateSelected")
    eventBus.on("updateSelected", (data) => {
      this.activeIndex = data;
    });
    //登录过期
    eventBus.offAll("login")
    eventBus.on("login", () => {
      // 登录过期
      this.$message({
        message: '登录过期，请重新登录',
        type: 'warning',
      });
      if (localStorage.getItem("auto") != null) {
        localStorage.removeItem("auto");
      }
      sessionStorage.removeItem("account");
      sessionStorage.removeItem("pwd");
      router.push({path: '/login'});
    });
    //权限不足
    eventBus.offAll("role")
    eventBus.on("role", () => {
      this.$message({
        message: '权限不足',
        type: 'error',
      });
    });
    if (this.account.startsWith('s')) {
      this.activeIndex = "/apply";
    } else if (this.account.startsWith('t')) {
      this.activeIndex = "/approve";
    } else if (this.account.startsWith('a')) {
      this.activeIndex = "/userManage";
    }
  },
  data() {
    return {
      name: sessionStorage.getItem("name"),
      activeIndex: "",
      account: sessionStorage.getItem("account"),
    }
  },
  methods: {
    logout() {
      if (localStorage.getItem("auto") != null) {
        localStorage.removeItem("auto");
      }
      sessionStorage.removeItem("account");
      sessionStorage.removeItem("pwd");
      router.push({path: '/login'});
    },
  }
};
</script>

<style scoped>

:deep(input) {
  border: none;
}

.el-main {
  background-color: #8BC6EC;
  background-image: linear-gradient(135deg, #8BC6EC 0%, #9599E2 100%);
  border: #f5f5f5 1px solid;
}

.home {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.side {
  height: 100%;
  background-color: #D9AFD9;
  background-image: linear-gradient(225deg, #D9AFD9 0%, #97D9E1 100%);
  border: #f5f5f5 1px solid;
}

.el-menu-item {
  background-color: #B5FFFC;
  background-image: linear-gradient(225deg, #B5FFFC 0%, #FFDEE9 100%);
  border: 1px solid #ccc;
}

.el-menu-item:hover {
  background-color: #FFDEE9;
  background-image: linear-gradient(225deg, #FFDEE9 0%, #B5FFFC 100%);
}

.head {
  height: 48px;
  background-color: #D9AFD9;
  background-image: linear-gradient(225deg, #D9AFD9 0%, #97D9E1 100%);
  text-align: center;
  border: #f5f5f5 1px solid;
}

.title {
  font-size: 36px;
  color: #4B0082;

  font-weight: bolder;
}

.foot {
  height: 24px;
  background-color: #D9AFD9;
  background-image: linear-gradient(225deg, #D9AFD9 0%, #97D9E1 100%);
  text-align: center;
  border: #f5f5f5 1px solid;
}

.router-link-active {
  text-decoration: none;
}

.head {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 48px;
  /* 其他样式 */
}

.el-aside {
  position: fixed;
  top: 48px;
  left: 0;
  bottom: 24px;
  width: 160px;
  /* 其他样式 */
}

.el-main {
  margin-top: 48px;
  margin-left: 160px; /* 这里的值需要和 .el-aside 的宽度一致 */
  margin-bottom: 24px;
  /* 其他样式 */
}

.foot {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 24px;
  /* 其他样式 */
}

a {
  text-decoration: none;
}

</style>
