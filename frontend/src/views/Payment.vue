<template>
    <div class="wrapper">
        <!-- header部分 -->
        <header>
            <p>在线支付</p>
        </header>
        <main>
            <!-- 商家信息 -->
            <div class="business-info">
                <h3>{{ order.business.businessName }}</h3>
                <p>{{ order.business.businessAddress }}</p>
            </div>

            <!-- 订单明细部分 -->
            <div class="order-details-section">
                <h4>商品列表</h4>
                <ul class="item-list">
                    <li class="item-card" v-for="item in order.orderdetails" :key="item.odId">
                        <img :src="item.goodsImg || '/path/to/default/goods_image.png'" alt="Goods Image">
                        <div class="item-info">
                            <p class="item-name">{{ item.goodsName }} x {{ item.quantity }}</p>
                            <p class="item-price">&#165;{{ (item.goodsPrice * item.quantity).toFixed(2) }}</p>
                        </div>
                    </li>
                </ul>

                <div class="delivery-fee">
                    <p>配送费</p>
                    <p>&#165;{{ order.business.deliveryPrice ? order.business.deliveryPrice.toFixed(2) : '0.00' }}</p>
                </div>

                <div class="total-amount">
                    <p>总计</p>
                    <p class="large-total-price">&#165;{{ order.amount ? order.amount.toFixed(2) : '0.00' }}</p> <!-- 增大字号 -->
                </div>
            </div>
        </main>

        <ul class="payment-type">
            <li @click="selectPayment('alipay')">
                <img src="../assets/alipay.png">
                <i class="fa" :class="{'fa-check-circle': selectedPayment === 'alipay'}"></i>
            </li>
            <li @click="selectPayment('wechatpay')">
                <img src="../assets/wechat.png">
                <i class="fa" :class="{'fa-check-circle': selectedPayment === 'wechatpay'}"></i>
            </li>
        </ul>
        <div class="payment-button">
            <button @click="confirmAndGoToDetail">确认支付</button> <!-- 修改点击事件 -->
        </div>

        <!-- 底部菜单部分 -->
        <Footer></Footer>
    </div>
</template>
  
<script setup>
import Footer from '../components/Footer.vue'
import { ref, onMounted } from "vue"
import { useRouter, useRoute } from "vue-router"
import { get, post, put } from "@/api" 
import { ElMessage } from 'element-plus'

const router = useRouter();
const route = useRoute();
const orderId = route.query.orderId;

const order = ref({ business: {}, orderdetails: [] });
const selectedPayment = ref('alipay'); 

const selectPayment = (type) => {
    selectedPayment.value = type;
};

onMounted(async () => {
    try {
        const res = await get(`/orders/getOrdersById/${orderId}`, {}, true);
        if (res.data.code === 20000) {
            order.value = res.data.resultdata;
            if (!order.value.orderdetails) {
                order.value.orderdetails = [];
            }
            if (!order.value.business) {
                order.value.business = {};
            }
            if (order.value.state === 1) {
                ElMessage.warning('您已支付过此订单！');
                router.replace('/orderList'); 
            }
        } else {
            ElMessage.error('获取订单信息失败');
            router.back(); 
        }
    } catch (error) {
        console.error('请求订单信息异常:', error);
        ElMessage.error('网络异常，请稍后重试');
        router.back(); 
    }
});

const confirmAndGoToDetail = async () => {
    try {
        const updateOrderRes = await post('/orders/updateState', {
            orderId: orderId,
            state: 1 
        }, true);

        if (updateOrderRes.data.code !== 20000) {
            ElMessage.error(updateOrderRes.data.message || '更新订单状态失败！');
            return; 
        }
        const account = JSON.parse(sessionStorage.getItem('account'));
        if (!account || !account.accountId) {
            ElMessage.error('无法获取用户信息，请重新登录。');
            router.push('/login');
            return;
        }

        const removePromises = [];
        if (order.value && order.value.orderdetails && order.value.business) {
            order.value.orderdetails.forEach(item => {
                removePromises.push(post("/cart/remove", {
                    goodsId: item.goodsId,
                    businessId: order.value.business.businessId,
                    accountId: account.accountId,
                }, true));
            });
        }
        await Promise.all(removePromises);
      
        ElMessage.success('支付成功！');
        router.push({ path: '/orderDetail', query: { orderId: orderId } });

    } catch (error) {
        console.error('支付或更新操作异常:', error);
        ElMessage.error('操作失败，请稍后重试！');
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
      background: linear-gradient(to right, #fff1eb, #ace0f9);
      color: #596164;
      letter-spacing: 2vw;
      font-size: 4.8vw;
  
      position: fixed;
      left: 0;
      top: 0;
      z-index: 1000;
  
      display: flex;
      justify-content: center;
      align-items: center;
    }
  
    /* 新增 main 样式，为内容区域留出 header 和 footer 空间 */
    main {
        padding: 12vw 0 14vw 0; /* 顶部留出header空间，底部留出footer空间 */
        overflow-y: auto;
        background-color: #f5f5f5;
        min-height: calc(100vh - 12vw - 14vw); /* 减去header和footer的高度 */
    }
  
    /* 商家信息部分 - 复制自 OrderDetail.vue */
    .business-info {
        background-color: #fff;
        border-radius: 2vw;
        margin: 3vw;
        padding: 4vw;
        box-shadow: 0 0.5vw 1vw rgba(0, 0, 0, 0.05);
    }
  
    .business-info h3 {
        font-size: 4.5vw;
        font-weight: bold;
        color: #333;
        margin-bottom: 1.5vw;
    }
  
    .business-info p {
        font-size: 3.5vw;
        color: #666;
    }
  
    /* 订单明细部分 - 复制自 OrderDetail.vue */
    .order-details-section {
        background-color: #fff;
        border-radius: 2vw;
        margin: 3vw;
        padding: 4vw;
        box-shadow: 0 0.5vw 1vw rgba(0, 0, 0, 0.05);
    }
  
    .order-details-section h4 {
        font-size: 4vw;
        font-weight: bold;
        color: #333;
        margin-bottom: 3vw;
        padding-bottom: 2vw;
        border-bottom: 1px solid #eee;
    }
  
    .item-list {
        list-style: none;
        padding: 0;
        margin: 0;
    }
  
    .item-card {
        display: flex;
        align-items: center;
        padding: 2vw 0;
        border-bottom: 1px dashed #eee;
    }
  
    .item-card:last-child {
        border-bottom: none;
    }
  
    .item-card img {
        width: 18vw;
        height: 18vw;
        object-fit: cover;
        border-radius: 1vw;
        margin-right: 3vw;
    }
  
    .item-info {
        flex-grow: 1;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
  
    .item-info .item-name {
        font-size: 4vw;
        color: #333;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        flex: 1;
    }
  
    .item-info .item-price {
        font-size: 3.8vw;
        color: #ff6600;
        font-weight: bold;
        margin-left: 2vw;
    }
  
    .delivery-fee,
    .total-amount {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 2vw 0;
        font-size: 4vw;
        color: #333;
        border-top: 1px solid #eee;
        margin-top: 2vw;
    }
  
    /* 增大总价字号 */
    .total-amount .large-total-price {
        font-size: 5vw; /* 调整为更大字号 */
        color: #ff6600;
        font-weight: bold;
    }
  
    /****************** 支付方式部分 ******************/
    .wrapper .payment-type {
        width: 100%;
        margin-top: 5vw; /* 调整间距 */
        border-top: 0.1vw solid #cecece;
        padding-top: 4vw;
        background-color: #fff; /* 添加背景色 */
        border-radius: 2vw;
        margin: 3vw; /* 调整外边距 */
        box-shadow: 0 0.5vw 1vw rgba(0, 0, 0, 0.05);
    }
  
    .wrapper .payment-type li {
        width: 100%;
        box-sizing: border-box;
        padding: 2vw 4vw;
        display: flex;
        justify-content: space-between;
        align-items: center;
        cursor: pointer; /* 添加手型光标 */
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
        position: fixed; /* 固定在底部 */
        left: 0;
        bottom: 14vw; /* 位于 Footer 之上 */
        background-color: #fff;
        border-top: 1px solid #f0f0f0;
        z-index: 999;
    }
  
    .wrapper .payment-button button {
        width: 100%;
        height: 10vw;
        border: none;
        outline: none;
        border-radius: 4px;
        background-color: #ff812c; /* 更改为橙色，更像“去支付” */
        color: #fff;
        font-size: 4.5vw; /* 增大字体 */
        font-weight: bold;
        cursor: pointer;
    }
</style>
  