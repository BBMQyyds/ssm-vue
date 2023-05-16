<template>
  <div class="about">
    <h2>关于本站</h2>
    <p>这是一个使用 Vue.js 和 Element UI 搭建的在线审批网站</p>
    <p>后端服务器采用Spring+SpringMVC+Mybatis框架</p>
    <p>初次制作，欢迎大家反馈bug和提出建议</p>
    <h3>制作人</h3>
    <ul>
      <li>
        <strong>author：</strong>20127033 梁朝阳
      </li>
      <li>
        <strong>email：</strong>3369855428@qq.com
      </li>
      <li>
        <strong>Github：</strong><a href="https://github.com/bbmqyyds" target="_blank">https://github.com/bbmqyyds</a>
      </li>
    </ul>
  </div>
</template>

<script>

import {eventBus} from "@ai-zen/event-bus";

export default {
  name: "about",
  created() {
    eventBus.emit('updateSelected', '/about');
  }
}
</script>

<style scoped>

:deep(input) {
  border: none;
}

.about {
  padding: 20px;
  font-size: 20px;
}

h2 {
  font-size: 32px;
  margin-bottom: 20px;
}

h3 {
  font-size: 24px;
  margin-top: 30px;
  margin-bottom: 10px;
}

ul {
  list-style: none;
  padding-left: 0;
}

li {
  margin-bottom: 10px;
}

strong {
  font-weight: bold;
}

</style>
