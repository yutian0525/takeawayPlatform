<template>
    <div class="wrapper">
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>写评价</p>
        </header>

        <div class="comment-form" v-if="order">
            <div class="order-info">
                <div class="business-logo" v-if="order.business && order.business.businessImg">
                    <img :src="order.business.businessImg" alt="商家图片">
                </div>
                <div class="business-text">
                    <span class="business-name">{{ order.business ? order.business.businessName : '未知商家' }}</span>
                    <div class="order-details">
                        <span class="order-id">订单号: {{ order.orderId }}</span>
                        <span class="order-date">下单时间: {{ formatDateTime(order.created) }}</span>
                        <span class="order-amount">订单金额: {{ order.amount }}元</span>
                    </div>
                </div>
            </div>

            <div class="rating-section">
                <span class="rating-label">总体评分</span>
                <el-rate v-model="rating" :texts="['很差', '较差', '一般', '不错', '很好']" show-text></el-rate>
            </div>

            <div class="comment-section">
                <el-input
                    v-model="comment"
                    type="textarea"
                    :rows="4"
                    placeholder="请分享您的用餐体验..."
                    maxlength="255"
                    show-word-limit
                ></el-input>
            </div>

            <div class="submit-section">
                <el-button type="primary" @click="submitReview" :loading="submitting">发布评价</el-button>
            </div>
        </div>

        <div class="loading" v-else>
            <p>加载中...</p>
        </div>

        <Footer></Footer>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Footer from '@/components/Footer.vue'
import { get, post } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 获取路由参数中的orderId
const orderId = route.params.orderId

// 订单信息
const order = ref(null)

// 评分和评论
const rating = ref(5)
const comment = ref('')
const submitting = ref(false)

// 用户登录信息
const account = JSON.parse(sessionStorage.getItem('account'))

// 格式化日期时间
const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return ''
    const date = new Date(dateTimeString)
    return date.toLocaleString()
}

// 加载订单信息
const loadOrder = async () => {
    if (!orderId) {
        ElMessage.error('订单ID不存在')
        router.back()
        return
    }

    try {
        const response = await get(`/orders/getOrdersById/${orderId}`, {}, true)
        if (response.data.code === 20000) {
            order.value = response.data.resultdata
            console.log('订单信息:', order.value)
        } else {
            ElMessage.error('获取订单信息失败')
            router.back()
        }
    } catch (error) {
        console.error('加载订单信息失败:', error)
        ElMessage.error('加载订单信息失败')
        router.back()
    }
}

// 提交评价
const submitReview = async () => {
    if (!account) {
        ElMessage.error('请先登录')
        router.push('/login')
        return
    }

    if (!comment.value.trim()) {
        ElMessage.error('请输入评价内容')
        return
    }

    submitting.value = true
    try {
        // 确保从订单信息中获取正确的商家ID
        const businessId = order.value.business ? order.value.business.businessId : order.value.businessId
        
        if (!businessId) {
            ElMessage.error('无法获取商家信息')
            return
        }
        
        const reviewData = {
            accountId: account.accountId,
            businessId: businessId,
            orderId: orderId,
            rate: rating.value,
            coText: comment.value
        }
        
        console.log('提交评价数据:', reviewData)

        // 使用true参数确保以JSON格式发送数据
        const res = await post('/comment/add', reviewData, true)
        if (res.data && (res.data.code === 20000 || res.data.code === 200)) {
            ElMessage.success('评价发布成功')
            router.push('/orderList') // 评价成功后返回订单列表
        } else {
            ElMessage.error(res.data ? res.data.message : '评价发布失败')
        }
    } catch (error) {
        console.error('发布评价请求异常:', error)
        ElMessage.error('发布评价异常')
    } finally {
        submitting.value = false
    }
}

onMounted(() => {
    loadOrder()
})
</script>

<style scoped>
.wrapper {
    width: 100%;
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 12vw;
}

header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 12vw;
    background: linear-gradient(to right, #fff1eb, #ace0f9);
    display: flex;
    align-items: center;
    padding: 0 4vw;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    z-index: 100;
}

header i {
    font-size: 5vw;
    color: #333;
    cursor: pointer;
}

header p {
    flex: 1;
    text-align: center;
    font-size: 5vw;
    color: #596164;
    margin: 0;
    letter-spacing: 2vw;
}

.comment-form {
    margin-top: 16vw;
    padding: 4vw;
}

.order-info {
    background: #fff;
    border-radius: 2vw;
    padding: 4vw;
    margin-bottom: 4vw;
    display: flex;
    align-items: center;
}

.business-logo {
    margin-right: 4vw;
}

.business-logo img {
    width: 15vw;
    height: 15vw;
    border-radius: 1vw;
    object-fit: cover;
}

.business-text {
    flex: 1;
}

.business-name {
    font-size: 4.5vw;
    font-weight: bold;
    color: #333;
    margin-bottom: 2vw;
    display: block;
}

.order-details {
    display: flex;
    flex-direction: column;
}

.order-id, .order-date, .order-amount {
    font-size: 3.5vw;
    color: #666;
    margin-bottom: 1vw;
}

.rating-section,
.comment-section {
    background: #fff;
    border-radius: 2vw;
    padding: 4vw;
    margin-bottom: 4vw;
}

.rating-label {
    display: block;
    margin-bottom: 3vw;
    color: #333;
    font-size: 4vw;
}

.submit-section {
    margin-top: 5vw;
}

.submit-section .el-button {
    width: 100%;
    height: 11vw;
    font-size: 4.5vw;
}

.loading {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 50vh;
    font-size: 4vw;
    color: #666;
}
</style>