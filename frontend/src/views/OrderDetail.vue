<template>
    <div class="wrapper">
        <!-- header部分 -->
        <header>
            <i class="fa fa-angle-left" @click="goBack"></i>
            <p>订单详情</p>
        </header>

        <main>
            <!-- 商家信息 -->
            <div class="business-info">
                <h3>{{ order.business.businessName }}</h3>
                <p>{{ order.business.businessAddress }}</p>
                <img :src="order.business.businessImg " alt="Business Image" class="business-img"> <!-- 新增：商家图片 -->
            </div>

            <!-- 订单明细部分 -->
            <div class="order-details-section">
                <h4>商品列表</h4>
                <ul class="item-list">
                    <li class="item-card" v-for="item in order.orderdetails" :key="item.odId">
                        <img :src="item.goodsImg || '/path/to/default/goods_image.png'" alt="Goods Image"> <!-- 更改：使用 item.goodsImg，并提供默认图片 -->
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
                    <p>&#165;{{ order.amount ? order.amount.toFixed(2) : '0.00' }}</p>
                </div>

                <div class="order-time">
                    <p>下单时间</p>
                    <p>{{ formatDateTime(order.created) }}</p>
                </div>
            </div>
        </main>

        <!-- 底部操作区 -->
        <div class="bottom-bar">
            <div v-if="order.state === 1" class="paid-status">
                <p>订单已支付</p>
                <button @click="goBack">返回</button>
            </div>
            <div v-else class="unpaid-actions">
                <button class="pay-button" @click="toPayment">去支付</button>
                <button class="back-button" @click="goBack">返回</button>
            </div>
        </div>

        <Footer></Footer>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { get } from '@/api';
import { ElMessage } from 'element-plus';
import Footer from '@/components/Footer.vue'; // 引入 Footer 组件

const router = useRouter();
const route = useRoute();
const orderId = route.query.orderId; // 从路由查询参数获取 orderId

const order = ref({
    business: {},
    orderdetails: [],
    amount: 0,
    created: '',
    state: 0 // 0: 未支付, 1: 已支付
});

// 格式化日期时间
const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return '';
    const date = new Date(dateTimeString);
    return date.toLocaleString(); // 根据本地时区格式化
};

// 获取订单详情
onMounted(async () => {
    if (!orderId) {
        ElMessage.error('订单ID缺失');
        router.back();
        return;
    }
    try {
        const res = await get(`/orders/getOrdersById/${orderId}`, {}, true);
        if (res.data.code === 20000) {
            order.value = res.data.resultdata;
            // 确保 orderdetails 是数组，即使后端返回 null 或 undefined
            if (!order.value.orderdetails) {
                order.value.orderdetails = [];
            }
            // 确保 business 存在
            if (!order.value.business) {
                order.value.business = {};
            }
        } else {
            ElMessage.error(res.data.message || '获取订单详情失败');
            router.back();
        }
    } catch (error) {
        console.error('请求订单详情异常:', error);
        ElMessage.error('网络异常，请稍后重试');
        router.back();
    }
});

// 跳转到支付页面
const toPayment = () => {
    router.push({ path: '/payment', query: { orderId: orderId } });
};

// 返回上一页
const goBack = () => {
    router.back();
};
</script>

<style scoped>
/* 顶部标题栏 */
.wrapper header {
    width: 100%;
    height: 12vw;
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

.wrapper header .fa-angle-left {
    position: absolute;
    left: 4vw;
    font-size: 6vw;
    color: #596164;
    cursor: pointer;
}

/* 主要内容区域 */
main {
    padding: 12vw 0 28vw 0;
    /* 顶部留出header空间，底部留出bottom-bar和footer空间 */
    overflow-y: auto;
    background-color: #f5f5f5;
    min-height: calc(100vh - 12vw - 14vw - 14vw);
    /* 减去header、bottom-bar和footer的高度 */
}

/* 商家信息部分新增样式 */
.business-info {
    position: relative; /* 使图片可以相对于此定位 */
    background-color: #fff;
    border-radius: 2vw;
    margin: 3vw;
    padding: 4vw;
    box-shadow: 0 0.5vw 1vw rgba(0, 0, 0, 0.05);
    padding-right: 25vw; /* 为图片留出空间 */
}

.business-info .business-img {
    position: absolute;
    right: 4vw;
    top: 50%;
    transform: translateY(-50%);
    width: 18vw; /* 根据需要调整大小 */
    height: 18vw; /* 根据需要调整大小 */
    object-fit: cover;
    border-radius: 1vw;
    border: 1px solid #eee;
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
.total-amount,
.order-time {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2vw 0;
    font-size: 4vw;
    color: #333;
    border-top: 1px solid #eee;
    margin-top: 2vw;
}

.total-amount p:last-child {
    font-size: 5vw;
    color: #ff6600;
    font-weight: bold;
}

/* 底部操作区 */
.bottom-bar {
    width: 100%;
    height: 14vw;
    background-color: #fff;
    position: fixed;
    left: 0;
    bottom: 14vw;
    /* 位于Footer之上 */
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 4vw;
    box-sizing: border-box;
    border-top: 1px solid #f0f0f0;
    z-index: 999;
}

.paid-status,
.unpaid-actions {
    display: flex;
    align-items: center;
    width: 100%;
    justify-content: space-around;
}

.paid-status p {
    font-size: 4.5vw;
    color: #4CAF50;
    font-weight: bold;
}

.paid-status button,
.unpaid-actions button {
    background-color: #007bff;
    /* 蓝色 */
    color: #fff;
    border: none;
    padding: 3vw 6vw;
    border-radius: 1vw;
    font-size: 4.5vw;
    font-weight: bold;
    cursor: pointer;
    white-space: nowrap;
}

.unpaid-actions .pay-button {
    background-color: #ff812c;
    /* 橙色 */
}

.unpaid-actions .back-button {
    background-color: #6c757d;
    /* 灰色 */
}
</style>