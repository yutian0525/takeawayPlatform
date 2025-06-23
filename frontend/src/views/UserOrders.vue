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
            <div class="tab" :class="{ active: currentTab === 'all' }" @click="selectTab('all')">全部订单</div>
            <div class="tab" :class="{ active: currentTab === 'paid' }" @click="selectTab('paid')">已支付</div>
            <div class="tab" :class="{ active: currentTab === 'pending' }" @click="selectTab('pending')">待支付</div>
        </div>

        <!-- 订单列表 -->
        <div class="order-list">
            <div class="order-item" v-for="order in filteredOrders" :key="order.orderId">
                <div class="business-info">
                    <i class="fa fa-shopping-cart"></i>
                    <span class="business-name">{{ order.businessName }}</span>
                    <!-- 使用 order.state 来判断订单状态 -->
                    <span class="order-status">{{ order.state === 1 ? '已支付' : '待支付' }}</span>
                </div>
                <div class="order-details">
                    <div class="amount">交易金额: {{ order.amount }}元</div>
                    <div class="date">交易日期: {{ order.created }}</div>
                </div>
                <div class="order-actions">
                    <!-- 新增删除订单按钮 -->
                    <button class="delete-btn" @click="deleteOrder(order.orderId)">删除订单</button>
                    <button class="review-btn" @click="goToReview(order.orderId)">去评价 ></button>
                </div>
            </div>
        </div>

        <Footer></Footer>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '@/components/Footer.vue'
import { get, del } from '@/api' // 导入 del 方法
import { ElMessage, ElMessageBox } from 'element-plus' // 导入 ElMessageBox 用于确认弹窗

const router = useRouter()
const searchText = ref('')
const orders = ref([])
const currentTab = ref('all') // 当前选中的标签，默认为'all'

// 计算属性：根据搜索文本和当前标签过滤订单
const filteredOrders = computed(() => {
    let filtered = orders.value;

    // 1. 根据搜索文本过滤
    if (searchText.value) {
        filtered = filtered.filter(order =>
            order.businessName.includes(searchText.value)
        );
    }

    // 2. 根据订单状态标签过滤
    if (currentTab.value === 'paid') {
        return filtered.filter(order => order.state === 1);
    } else if (currentTab.value === 'pending') {
        return filtered.filter(order => order.state === 0);
    }
    return filtered; // 'all' tab 或没有特定过滤条件时返回全部
});

// 切换标签的方法
const selectTab = (tab) => {
    currentTab.value = tab;
};

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

// 删除订单
const deleteOrder = async (orderId) => {
    try {
        // 弹出确认框
        await ElMessageBox.confirm('确定要删除此订单吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });

        // 调用后端删除接口
        const response = await del(`/orders/${orderId}`); // 假设后端删除接口为 DELETE /orders/{orderId}
        if (response.data.code === 20000) {
            ElMessage.success('订单删除成功！');
            loadOrders(); // 重新加载订单列表以更新显示
        } else {
            ElMessage.error(response.data.message || '订单删除失败');
        }
    } catch (error) {
        // 如果用户点击了取消，error会是'cancel'，不进行错误提示
        if (error !== 'cancel') {
            console.error('删除订单失败:', error);
            ElMessage.error('删除订单异常，请重试');
        }
    }
};

// 跳转到评价页面
const goToReview = (orderId) => {
    router.push(`/review/${orderId}`) // 保持现有逻辑，跳转到 /review/订单ID 页面
}

onMounted(() => {
    loadOrders()
})
</script>

<style scoped>
.wrapper {
    width: 100%;
    height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 12vw; 
}

header {
    width: 100%;
    height: 12vw;
    background: linear-gradient(to right, #fff1eb, #ace0f9);
    color: #596164;
    font-size: 5vw;
    position: fixed; /* 固定在顶部 */
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
    margin-top: 14vw; /* 为顶部固定导航栏留出空间 */
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
    gap: 2vw; /* 按钮之间的间距 */
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

.delete-btn {
    background-color: #f56c6c; /* 删除按钮的颜色 */
    color: #fff;
    border: none;
    padding: 1.5vw 3vw;
    border-radius: 1vw;
    font-size: 3.2vw;
    cursor: pointer;
}
</style>