<template>
  <div class="wrapper">
    <header>
      <p>用 户 主 页</p>
    </header>
    <div class="loggedin" v-if="isLogin">
        <div class="user-info">
        <img :src="account.accountImg" alt="头像" />
        <p>用户ID: {{ account.accountId }}</p>
        </div>
        <div class="orders">
        <h3>我的订单</h3>
        <button @click="toShowUserOrders()">查看所有订单</button>
        </div>
        <div class="reviews">
        <h3>用户评价</h3>
        <button @click="toShowUserReviews()">查看所有评价</button>
        </div>
        <div class="actions">
        <button @click="toChangePassword()">修改密码</button>
        <button @click="toSwitchAccount()">切换账号</button>
        </div>
    </div>
    <div class="actions" v-else>
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
  console.log("跳转到用户评价页面");
};

const toChangePassword = () => {
  // 跳转到修改密码页面
  console.log("跳转到修改密码页面");
};

const toSwitchAccount = () => {
  // 跳转到切换账号页面
  console.log("跳转到切换账号页面");
};
</script>
<style scoped>
 .wrapper header{
 width:100%; height:12vw;
 background: linear-gradient(to right,#fff1eb,#ace0f9);
 color:#596164;
 font-size: 5vw;
 position: fixed; left:0; top: 0;
 display: flex;
 justify-content: center;
 align-items: center;
z-index: 1000;
 }
 .wrapper header p{
 letter-spacing: 2vw;
 }
 /* APP标题部分样式 */
 .wrapper .button-login{
 width:100%;
 box-sizing: border-box;
 padding:4vw 4vw 0 4vw;
 }
 .wrapper .button-login button{
 width:100%;
 height:10vw;
 font-size: 3.8vw;
 background-color: #3cba92;
 border:none;
 border-radius: 1.5vw;
 color:#efefef;
 outline: none;
 letter-spacing: 2vw;
 }
 .wrapper .button-register{
 width:100%;
 box-sizing: border-box;
 padding:4vw;
 }
 .wrapper .button-register button{
 width:100%;
 height:10vw;
 font-size: 3.8vw;
 background-color: #fdfcfb;
 border:0.3vw solid #e2d1c3;
 border-radius: 1.5vw;
 color:#5f72bd;
 outline: none;
 letter-spacing: 2vw;
 }
 </style>
