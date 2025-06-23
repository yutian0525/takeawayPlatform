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
import { getSessionStorage,setSessionStorage } from '@/common';
import { ElMessageBox, ElMessage } from 'element-plus';
import { get } from "@/api/index.js";

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

const toShowUserReviews = () => {
  // 跳转到用户评价页面
  router.push('/myComments');
};

const toShowUserOrders = () => {
  // 跳转到用户订单页面
};

const toShowUserBusinesses = () =>{
  //跳转到关注店铺界面
};

const toShowUserAddresses = () =>{
  //跳转到用户地址界面
};

const toChangeAccount = () => {
  //跳转到修改信息的界面
  verifyPassword(() => {
    router.push('/accountChange');
  });
};

const toSwitchAccount = () => {
  router.push('/login');
};

const toCancelAccount = () => {
  verifyPassword(() => {
  //注销账号
  const url = `/account/cancel/${account.accountId}`;
  get(url).then(res => {
      if (res.data.code === 20000) {
        ElMessage.success('账号已注销');
      } 
      else {
        ElMessage.error(res.data.message || '账号注销失败');
      }
    });
  sessionStorage.removeItem('account');
  location.reload()
  });
};

const toLogoutAccount = () => {
  //退出当前账号
  sessionStorage.removeItem('account');
  location.reload()
};


const verifyPassword = (callback) => {
  ElMessageBox.prompt(
    '请输入原密码进行验证',
    '密码验证',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      showCancelButton: true,
      showInput: true,
      inputValidator: (val) => val.trim().length > 0,
      inputErrorMessage: '密码不能为空'
    }
  )
  .then(({ value }) => {
    const url = `/account/checkPassword/${account.accountId}/${value}`;
    get(url).then(res => {
      if (res.data.code === 20000) {
        // account.password = value;
        // setSessionStorage('account',JSON.stringify(account));
        setSessionStorage('account',res.data.resultdata);
        console.log(res.data.data);
        callback();
      } 
      else {
        ElMessage.error("密码不正确");
      }
    });
  })
  .catch(() => {
    ElMessage.info('取消验证');
  });
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
  margin-top: 10%;
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
  border: 0.3vw solid #e2c6c3;
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
.button-login {
  width: 40vmax;
  box-sizing: border-box;
  padding: 4vw;
}

.button-login button {
  width: 100%;
  height: 10vw;
  font-size: 3.8vw;
  background-color: #3cba92;
  border: none;
  border-radius: 1.5vw;
  color: #efefef;
  outline: none;
  letter-spacing: 2vw;
}

.button-register {
  width: 100%;
  box-sizing: border-box;
  padding: 4vw;
}

.button-register button {
  width: 100%;
  height: 10vw;
  font-size: 3.8vw;
  background-color: #fdfcfb;
  border: 0.3vw solid #e2d1c3;
  border-radius: 1.5vw;
  color: #5f72bd;
  outline: none;
  letter-spacing: 2vw;
}
</style>