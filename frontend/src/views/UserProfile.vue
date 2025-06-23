<template>
  <div class="wrapper">
    <header>
      <p>用 户 主 页</p>
    </header>
    <div class="loggedin" v-if="isLogin">
        <div class="user-info">
        <img :src="account.accountImg" alt="头像" />
        <p>昵称：{{ account.accountName }}</p>
        <p>用户ID: {{ account.accountId }}</p>
        </div>
        <div class="Mine">
          <h3>我的:</h3>
            <button @click="toShowUserReviews()">我的评价</button>
            <button @click="toShowUserOrders()">我的订单</button>
            <button @click="toShowUserBusinesses()">店铺关注</button>
            <button @click="toShowUserAddresses()">地址管理</button>
        </div>
        <div class="Actions">
          <h3>账号:</h3>
          <button @click="toChangeAccount()">修改信息</button>
          <button @click="toSwitchAccount()">切换账号</button>
        </div>
        <div class="Account">
          <div class="cancel">
            <button @click="toCancelAccount()">注销账号</button>
          </div>
          <div class="logout">
            <button @click="toLogoutAccount()">退出账号</button>
          </div>
        </div>
    </div>
    <div class="Actions" v-else>
        <br><br><br>
        <div class="button-login">
            <button @click="toLogin()">登录</button>
        </div>
        <div class="button-register">
            <button @click="toRegister()">注册</button>
        </div>
    </div>
    <Footer></Footer>
  </div>
</template>

<script setup>
import Footer from '@/components/Footer.vue';
import { ref } from 'vue';
import router from '@/router';
import { getSessionStorage } from '@/common';
import { ElMessage } from 'element-plus';

// 用户登录信息
const account = JSON.parse(sessionStorage.getItem("account"))


// 判断是否登录
const isLogin = ref(!!account);

const toLogin = () => {
  // 跳转到登录页面
  router.push('/login');
};

const toRegister = () => {
  // 跳转到注册页面
  router.push('/register');
};

const toShowUserOrders = () => {
  // 跳转到用户订单页面
  console.log("跳转到用户订单页面");
};

const toShowUserReviews = () => {
  // 跳转到用户评价页面
  router.push('/myComments');
};

const toChangePassword = () => {
  // 跳转到修改密码页面
  console.log("跳转到修改密码页面");
};

const toSwitchAccount = () => {
  // 跳转到切换账号页面
  console.log("跳转到切换账号页面");
};

const toShowUserBusinesses = () => {
  // 跳转到店铺关注页面
  router.push('/userFavorites');
};

const toShowUserAddresses = () => {
  // 跳转到地址管理页面
  router.push('/address');
};

const toChangeAccount = () => {
  // 跳转到修改信息页面
  router.push('/accountChange');
};

const toLogoutAccount = () => {
  // 退出账号
  sessionStorage.removeItem('account');
  ElMessage.success('退出成功');
  isLogin.value = false;
};

const toCancelAccount = () => {
  // 注销账号功能
  console.log("注销账号功能待实现");
};
</script>
<style scoped>
.wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #f5f5f5;
}

/* 头部 */
.wrapper header {
  width: 100%;
  height: 14vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  color: #596164;
  font-size: 5vw;
  position: fixed;
  left: 0;
  top: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.wrapper header p {
  letter-spacing: 2vw;
}

/* 登录状态显示 */
.loggedin {
  width: 100%;
  padding: 4vw 4vw 4vw 4vw;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.loggedin .user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 0;
  margin-bottom: 4vw;
}

.loggedin .user-info img {
  width: 24vw;
  height: 24vw;
  border-radius: 9vw;
  border: 0.5vw solid #fff;
  margin-bottom: 2vw;
}

.loggedin .user-info p {
  font-size: 6vw;
  color: #696969;
}

.loggedin .Mine,
.loggedin .Actions {
  width: 100%;
  margin-bottom: 4vw;
}

.loggedin .Mine h3,
.loggedin .Actions h3 {
  font-size: 4vw;
  color: #696969;
  margin-bottom: 2vw;
}

.loggedin button {
  width: 100%;
  height: 10vw;
  font-size: 3.8vw;
  background-color: #3cba92;
  border: none;
  border-radius: 1.5vw;
  color: #efefef;
  outline: none;
  letter-spacing: 2vw;
  margin-bottom: 2vw;
}

.loggedin .Actions button:last-child {
  background-color: #fdfcfb;
  border: 0.3vw solid #e2d1c3;
  color: #5f72bd;
}

/* 账号操作部分 */
.loggedin .Account {
  width: 100%;
  margin-top: 8vw;
}

.loggedin .Account .cancel button,
.loggedin .Account .logout button {
  width: 100%;
  height: 10vw;
  font-size: 3.8vw;
  border: none;
  border-radius: 1.5vw;
  color: #efefef;
  outline: none;
  letter-spacing: 2vw;
  margin-bottom: 2vw;
}

.loggedin .Account .cancel button {
  background-color: #ff4d4f; /* 注销账号使用红色 */
}

.loggedin .Account .logout button {
  background-color: #ff9500; /* 退出账号使用橙色 */
}

/* 未登录状态显示 */
.actions {
  width: 100%;
  padding: 30vw 4vw 4vw 4vw;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.actions .button-login,
.actions .button-register {
  width: 100%;
  margin-bottom: 4vw;
}

.actions .button-login button,
.actions .button-register button {
  width: 100%;
  height: 10vw;
  font-size: 3.8vw;
  border: none;
  border-radius: 1.5vw;
  color: #efefef;
  outline: none;
  letter-spacing: 2vw;
}

.actions .button-login button {
  background-color: #3cba92;
}

.actions .button-register button {
  background-color: #fdfcfb;
  border: 0.3vw solid #e2d1c3;
  color: #5f72bd;
}

/* 底部 */
Footer {
  width: 100%;
  position: fixed;
  bottom: 0;
  left: 0;
  z-index: 1000;
}
</style>