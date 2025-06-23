<template>
    <div class="wrapper">
        <!-- header部分 -->
        <header>
            <p>在线支付</p>
        </header>

        <!-- 订单信息部分 -->
        <h3>订单信息：</h3>
        <div class="order-info">
            <p>
                {{ order.business.businessName }}
                <i class="fa fa-caret-down" @click="detailetShow"></i>
            </p>
            <p>&#165;{{ order.orderTotal }}</p>
        </div>

        <!-- 订单明细部分 -->
        <ul class="order-detailet" v-show="isShowDetailet">
            <li v-for="item in order.list" :key="item.odId">
                <p>{{ item.goods.goodsName }} x {{ item.quantity }}</p>
                <p>&#165;{{ item.goods.goodsPrice * item.quantity }}</p>
            </li>
            <li>
                <p>配送费</p>
                <p>&#165;{{ order.business.deliveryPrice }}</p>
            </li>
        </ul>

        <!-- 支付方式部分 -->
        <ul class="payment-type">
            <li>
                <img src="../assets/alipay.png">
                <i class="fa fa-check-circle"></i>
            </li>
        </ul>
        <div class="payment-button">
            <button @click="toUserOrders">确认支付</button>
        </div>

        <!-- 底部菜单部分 -->
        <Footer></Footer>
    </div>
</template>
  
  <script setup>
import Footer from '../components/Footer.vue'
import { ref, reactive, onMounted, onUnmounted } from "vue"
import { useRouter, useRoute } from "vue-router"
import { get, post } from "@/api"
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter();
const route = useRoute();
const orderId = route.query.orderId;
const order = ref({ business: {} });
const isShowDetailet = ref(false);

const detailetShow = () => {
    isShowDetailet.value = !isShowDetailet.value;
};

onMounted(async () => {
    try {
        const res = await get(`/orders/getOrdersById/${orderId}`, {}, true);
        if (res.data.code === 20000) {
            order.value = res.data.resultdata;
        } else {
            ElMessage.error('获取订单信息失败');
        }
    } catch (error) {
        console.error('请求订单信息异常:', error);
        ElMessage.error('网络异常，请稍后重试');
    }
});

const toUserOrders = async () => {
    try {
        const res = await post(`/orders/pay/${orderId}`, {}, true);
        if (res.data.code === 20000) {
            ElMessage.success('支付成功！');
            router.push('/userOrders');
        } else {
            ElMessage.error(res.data.message || '支付失败');
        }
    } catch (error) {
        console.error('支付请求异常:', error);
        ElMessage.error('支付异常，请稍后重试');
    }
};
</script>
  
  <style scoped>
    /****************** 总容器 ******************/
    .wrapper {
      width: 100%;
      height: 100%;
      padding: 3vw;
      box-sizing: border-box;
    }
  
    /****************** header部分 ******************/
    .wrapper header {
      width: 100%;
      height: 12vw;
      background:linear-gradient(to right,#fff1eb,#ace0f9 ); color:#596164;
      letter-spacing:2vw;
      font-size: 4.8vw;
  
      position: fixed;
      left: 0;
      top: 0;
      z-index: 1000;
  
      display: flex;
      justify-content: center;
      align-items: center;
    }
  
    /****************** 订单信息部分 ******************/
    .wrapper h3 {
      margin-top: 12vw;
      box-sizing: border-box;
      padding: 4vw 4vw 0;
  
      font-size: 4vw;
      font-weight: 300;
      color: #999;
    }
  
    .wrapper .order-info {
      box-sizing: border-box;
      padding: 4vw;
      font-size: 4vw;
      color: #666;
  
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  
    .wrapper .order-info p:last-child {
      color: orangered;
    }
  
    /****************** 订单明细部分 ******************/
    .wrapper .order-detailet {
      width: 100%;
    }
  
    .wrapper .order-detailet li {
      width: 100%;
      box-sizing: border-box;
      padding: 1vw 4vw;
  
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  
    .wrapper .order-detailet li img {
      width: 16vw;
      height: 16vw;
      border-radius: 5px;
    }

    
  
    .wrapper .order-detailet li p {
      font-size: 3.8vw;
      color: #79859E;
    }
  
    /****************** 支付方式部分 ******************/
    .wrapper .payment-type {
      width: 100%;
      margin-top:15vw;
      border-top:0.1vw solid #cecece;
      padding-top:4vw;
    }
  
    .wrapper .payment-type li {
      
      width: 100%;
      box-sizing: border-box;
      padding:2vw 4vw;
  
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  
    .wrapper .payment-type li img {
      width: 30vw;
      height: 8vw;
    }
  
    .wrapper .payment-type li .fa-check-circle {
      font-size: 5vw;
      color: #3cba92;
    }
  
    .wrapper .payment-button {
      width: 100%;
      box-sizing: border-box;
      padding: 4vw;
    }

    .wrapper .payment-button .el-icon-success{
        color:#3cba92;
    }
  
    .wrapper .payment-button button {
      width: 100%;
      height: 10vw;
      border: none;
      /*去掉外轮廓线*/
      outline: none;
      border-radius: 4px;
      background-color: #3cba92;
      color: #fff;
    }
  </style>
  