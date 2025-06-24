<template>
    <div class="wrapper">
        <header>
            <p>订单</p>
        </header>

        <div class="search-box">
            <input type="text" v-model="searchText" placeholder="搜索商家或商品名称">
            <i class="el-icon-search"></i>
        </div>

        <div class="order-tabs">
            <div class="tab" :class="{ active: currentTab === 'all' }" @click="selectTab('all')">全部订单</div>
            <div class="tab" :class="{ active: currentTab === 'paid' }" @click="selectTab('paid')">已支付</div>
            <div class="tab" :class="{ active: currentTab === 'pending' }" @click="selectTab('pending')">待支付</div>
        </div>

        <div class="order-list">
            <div class="order-item" v-for="order in filteredOrders" :key="order.orderId" @click="goToOrderDetail(order.orderId)"> <!-- 新增：点击跳转到订单详情 -->
                <div class="business-info">
                    <i class="fa fa-shopping-cart"></i>
                    <span class="business-name">{{ order.businessName }}</span>
                  <span class="order-status">
                    {{ order.state === 0 ? '待支付' : (hasComment(order.orderId) ? '已完成' : '已支付') }}
                  </span>
                </div>
                <div class="order-details">
                    <div class="amount">交易金额: {{ order.amount }}元</div>
                    <div class="date">交易日期: {{ formatDateTime(order.created) }} </div>
                </div>
                <div class="order-actions">
                    <button class="review-btn" @click.stop="goToReview(order.orderId)" v-if="order.state === 1 && !hasComment(order.orderId)">去评价 ></button> <!-- 只有已支付且未评价的订单才显示去评价按钮 -->
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
import { get, del } from '@/api' 
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const searchText = ref('')
const orders = ref([])
const currentTab = ref('all')
const userComments = ref([]) // 存储用户的评论列表

const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return '';
    const date = new Date(dateTimeString);
    return date.toLocaleString(); 
};
const filteredOrders = computed(() => {
    let filtered = orders.value;
    const searchLower = searchText.value.toLowerCase(); // 将搜索文本转为小写，方便不区分大小写搜索

    // 1. 根据搜索文本过滤
    if (searchText.value) {
        filtered = filtered.filter(order => {
            // 检查商家名称是否包含搜索文本
            const businessNameMatch = order.businessName.toLowerCase().includes(searchLower);

            // 检查订单详情中的商品名称是否包含搜索文本
            const goodsNameMatch = order.orderdetails && order.orderdetails.some(item =>
                item.goodsName.toLowerCase().includes(searchLower)
            );

            return businessNameMatch || goodsNameMatch;
        });
    }

    // 2. 根据订单状态标签过滤
    if (currentTab.value === 'paid') {
        filtered = filtered.filter(order => order.state === 1);
    } else if (currentTab.value === 'pending') {
        filtered = filtered.filter(order => order.state === 0);
    }
    
    // 3. 按照时间从近到远排序
    // 假设 order.created 是一个可以被 Date 对象解析的字符串
    filtered.sort((a, b) => {
        return new Date(b.created).getTime() - new Date(a.created).getTime();
    });

    return filtered;
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
            // 加载订单后，加载用户评论列表
            await loadUserComments(account.accountId)
        } else {
            ElMessage.error('获取订单列表失败')
        }
    } catch (error) {
        console.error('加载订单列表失败:', error)
        ElMessage.error('加载订单列表失败')
    }
}

// 获取用户评论列表
const loadUserComments = async (accountId) => {
    try {
        const response = await get(`/comment/list/user/${accountId}`)
        if (response.data.code === 20000) {
            userComments.value = response.data.resultdata || []
            console.log('用户评论列表:', userComments.value)
        } else {
            console.error('获取用户评论列表失败')
        }
    } catch (error) {
        console.error('加载用户评论列表失败:', error)
    }
}

// 检查订单是否已评价
const hasComment = (orderId) => {
    return userComments.value.some(comment => comment.order_id === orderId)
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

// 新增：跳转到订单详情页面
const goToOrderDetail = (orderId) => {
    router.push({ path: '/orderDetail', query: { orderId: orderId } });
};

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
    margin-top: 12vw;
    display: flex;
    align-items: center; /* 垂直居中对齐 */
    width: 90%;
    margin-left: auto; /* 水平居中 */
    margin-right: auto; /* 水平居中 */
    position: relative;
    padding: 0 3vw; /* 增加容器内边距 */
    box-sizing: border-box; /* 确保内边距包含在宽度内 */
}

.search-box input {
    flex-grow: 1; /* 让输入框占据可用空间 */
    height: 8vw;
    border: 1px solid #e0e0e0; /* 添加边框 */
    border-radius: 4vw;
    padding: 0 10vw 0 4vw; /* 增加右侧内边距，为图标留出空间 */
    font-size: 3.2vw;
    background-color: #fff;
    box-shadow: 0 0.2vw 1vw rgba(0, 0, 0, 0.05); /* 调整阴影 */
    outline: none; /* 移除聚焦时的外边框 */
}

.search-box i {
    position: absolute;
    right: 6vw; /* 调整图标位置 */
    top: 50%;
    transform: translateY(-50%);
    color: #999;
    font-size: 4.5vw; /* 稍微增大图标 */
    cursor: pointer; /* 添加手型光标 */
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