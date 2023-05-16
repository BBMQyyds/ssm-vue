<template>
  <div class="not-found">
    <h1>404 Not Found</h1>
    <p>很抱歉，您要访问的页面不存在或没有权限访问。</p>
    <p>可能原因：</p>
    <p>1.您所输入的地址有误</p>
    <p>2.该页面已被管理员删除或下架</p>
    <p>3.您没有足够的权限访问该页面</p>
  </div>

</template>
<script>
export default {
  name: 'NotFound',
  data() {
    return {
      account: sessionStorage.getItem('account')
    }
  }
}
</script>
<style scoped>

:deep(input) {
  border: none;
}

.not-found {
  text-align: center;
  padding: 50px;
  font-size: 24px;
}

#list {
  text-align: center;
}

.return-link {
  display: inline-block;
  margin-top: 20px;
  margin-right: 30px;
  margin-left: 30px;
  padding: 10px 20px;
  border-radius: 4px;
  color: #fff;
  background-color: #6DD6D6;
  text-decoration: none;
  transition: background-color 0.3s;
}

.return-link:hover {
  background-color: #D9AFD9;
}
</style>