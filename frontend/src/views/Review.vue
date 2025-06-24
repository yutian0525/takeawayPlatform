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

            <div class="upload-section">
                <div class="upload-title">添加图片</div>
                <div class="image-upload">
                    <input 
                        type="file" 
                        ref="fileInput" 
                        accept="image/jpeg,image/png,image/jpg" 
                        @change="handleFileChange" 
                        style="display: none"
                    >
                    <div v-if="!imagePreview" class="upload-box" @click="triggerFileInput">
                        <i class="el-icon-plus"></i>
                        <p>点击上传图片</p>
                    </div>
                    <div v-else class="preview-container">
                        <img :src="imagePreview" class="preview-image">
                        <div class="preview-actions">
                            <button @click="removeImage" class="remove-btn">删除</button>
                        </div>
                    </div>
                </div>
                <div class="upload-tip">只能上传jpg/png文件，且不超过2MB</div>
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
const imagePreview = ref('')
const imageBase64 = ref('')

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

// 触发文件选择
const triggerFileInput = () => {
    document.querySelector('input[type="file"]').click()
}

// 处理文件选择
const handleFileChange = (event) => {
    const file = event.target.files[0]
    if (!file) return
    
    // 验证文件类型和大小
    const isJPGOrPNG = ['image/jpeg', 'image/png', 'image/jpg'].includes(file.type)
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isJPGOrPNG) {
        ElMessage.error('只能上传JPG或PNG格式的图片！')
        return
    }
    if (!isLt2M) {
        ElMessage.error('图片大小不能超过2MB！')
        return
    }
    
    // 读取文件并转换为base64
    const reader = new FileReader()
    reader.onload = (e) => {
        imagePreview.value = e.target.result
        imageBase64.value = e.target.result
    }
    reader.readAsDataURL(file)
}

// 移除已选择的图片
const removeImage = () => {
    imagePreview.value = ''
    imageBase64.value = ''
    // 重置文件输入框
    const fileInput = document.querySelector('input[type="file"]')
    if (fileInput) fileInput.value = ''
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
            coText: comment.value,
            coImg: imageBase64.value // 添加图片base64数据
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
.comment-section,
.upload-section {
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

.upload-title {
    font-size: 4vw;
    color: #333;
    margin-bottom: 3vw;
}

.image-upload {
    display: flex;
    flex-wrap: wrap;
}

.upload-box {
    width: 25vw;
    height: 25vw;
    border: 1px dashed #d9d9d9;
    border-radius: 2vw;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    transition: border-color 0.3s;
}

.upload-box:hover {
    border-color: #409EFF;
}

.upload-box i {
    font-size: 6vw;
    color: #8c939d;
    margin-bottom: 2vw;
}

.upload-box p {
    font-size: 3vw;
    color: #8c939d;
    margin: 0;
}

.preview-container {
    position: relative;
    margin-right: 3vw;
    margin-bottom: 3vw;
}

.preview-image {
    width: 25vw;
    height: 25vw;
    object-fit: cover;
    border-radius: 2vw;
}

.preview-actions {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    padding: 1vw 0;
    border-bottom-left-radius: 2vw;
    border-bottom-right-radius: 2vw;
}

.remove-btn {
    background: none;
    border: none;
    color: #fff;
    font-size: 3vw;
    cursor: pointer;
}

.upload-tip {
    font-size: 3vw;
    color: #8c939d;
    margin-top: 2vw;
}
</style>