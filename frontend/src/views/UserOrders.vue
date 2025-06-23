<template>
    <div class="wrapper">
        <header>
            <p>订单</p>
        </header>

        <!-- 搜索框 -->
        <div class="search-box">
            <input type="text" v-model="searchText" placeholder="请输入您想搜索的商家名称">
            <i class="el-icon-search"></i>
        </div>

        <!-- 订单状态标签 -->
        <div class="order-tabs">
            <div class="tab active">全部订单</div>
            <div class="tab">已支付</div>
            <div class="tab">待支付</div>
        </div>

        <!-- 订单列表 -->
        <div class="order-list">
            <div class="order-item" v-for="order in orders" :key="order.orderId">
                <div class="business-info">
                    <i class="fa fa-shopping-cart"></i>
                    <span class="business-name">{{ order.businessName }}</span>
                    <span class="order-status">{{ order.status === 1 ? '已支付' : '待支付' }}</span>
                </div>
                <div class="order-details">
                    <div class="amount">交易金额: {{ order.amount }}元</div>
                    <div class="date">交易日期: {{ order.orderDate }}</div>
                </div>
                <div class="order-actions">
                    <button class="review-btn" @click="goToReview(order.orderId)">去评价 ></button>
                </div>
            </div>
        </div>

        <Footer />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '@/components/Footer.vue'
import { get } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const searchText = ref('')
const orders = ref([])

// 获取订单列表
const loadOrders = async () => {
    try {
        const account = JSON.parse(sessionStorage.getItem('account'))
        if (!account) {
            ElMessage.warning('请先登录')
            router.push('/login')
            return
        }

        const response = await get(`/orders/${account.accountId}`)
        if (response.data.code === 20000) {
            orders.value = response.data.resultdata
        } else {
            ElMessage.error('获取订单列表失败')
        }
    } catch (error) {
        console.error('加载订单列表失败:', error)
        ElMessage.error('加载订单列表失败')
    }
}

// 跳转到评价页面
const goToReview = (orderId) => {
    router.push(`/review/${orderId}`)
}

onMounted(() => {
    loadOrders()
})
</script>

<style scoped>
.wrapper {
    width: 100%;
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 14vw;
}

header {
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

header p {
    letter-spacing: 2vw;
}

.search-box {
    margin-top: 14vw;
    padding: 2vw 4vw;
    position: relative;
}

.search-box input {
    width: 100%;
    height: 8vw;
    border: none;
    border-radius: 4vw;
    padding: 0 4vw;
    font-size: 3.2vw;
    background-color: #fff;
    box-shadow: 0 0.2vw 1vw rgba(0, 0, 0, 0.1);
}

.search-box i {
    position: absolute;
    right: 6vw;
    top: 50%;
    transform: translateY(-50%);
    color: #999;
    font-size: 4vw;
}

.order-tabs {
    display: flex;
    justify-content: space-around;
    padding: 2vw 0;
    background-color: #fff;
    margin-bottom: 2vw;
}

.tab {
    font-size: 3.6vw;
    color: #666;
    padding: 2vw 4vw;
    cursor: pointer;
}

.tab.active {
    color: #ff6b01;
    font-weight: bold;
    position: relative;
}

.tab.active::after {
    content: '';
    position: absolute;
    bottom: -1vw;
    left: 50%;
    transform: translateX(-50%);
    width: 6vw;
    height: 0.4vw;
    background-color: #ff6b01;
    border-radius: 0.2vw;
}

.order-list {
    padding: 0 3vw;
}

.order-item {
    background-color: #fff;
    border-radius: 2vw;
    margin-bottom: 3vw;
    padding: 3vw;
    box-shadow: 0 0.2vw 1vw rgba(0, 0, 0, 0.05);
}

.business-info {
    display: flex;
    align-items: center;
    padding-bottom: 2vw;
    border-bottom: 0.2vw solid #f5f5f5;
}

.business-info i {
    font-size: 4vw;
    color: #ff6b01;
    margin-right: 2vw;
}

.business-name {
    flex: 1;
    font-size: 3.6vw;
    color: #333;
}

.order-status {
    font-size: 3.2vw;
    color: #3cba92;
}

.order-details {
    padding: 2vw 0;
    font-size: 3.2vw;
    color: #666;
}

.amount {
    margin-bottom: 1vw;
}

.order-actions {
    display: flex;
    justify-content: flex-end;
    padding-top: 2vw;
    border-top: 0.2vw solid #f5f5f5;
}

.review-btn {
    background-color: #ff9900;
    color: #fff;
    border: none;
    padding: 1.5vw 3vw;
    border-radius: 1vw;
    font-size: 3.2vw;
    cursor: pointer;
}
</style>