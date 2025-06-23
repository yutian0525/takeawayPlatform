<template>
    <div class="wrapper">
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>写评价</p>
        </header>

        <div class="comment-form">
            <div class="business-info">
                <div class="business-logo" v-if="business.businessImg">
                    <img :src="business.businessImg" alt="商家图片">
                </div>
                <div class="business-text">
                    <span class="business-name">{{ business.businessName }}</span>
                    <div class="business-details">
                        <span class="business-address">{{ business.businessAddress }}</span>
                    </div>
                </div>
            </div>

            <div class="rating-section">
                <span class="rating-label">总体评分</span>
                <el-rate v-model="commentForm.rate" :texts="['很差', '较差', '一般', '不错', '很好']" show-text></el-rate>
            </div>

            <div class="comment-section">
                <el-input
                    v-model="commentForm.coText"
                    type="textarea"
                    :rows="4"
                    placeholder="请分享您的用餐体验..."
                    maxlength="255"
                    show-word-limit
                ></el-input>
            </div>

            <div class="upload-section">
                <el-upload
                    class="upload-demo"
                    action="/api/upload"
                    :on-success="handleUploadSuccess"
                    :before-upload="beforeUpload"
                    :limit="1"
                    list-type="picture">
                    <el-button type="primary">上传图片</el-button>
                    <template #tip>
                        <div class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
                    </template>
                </el-upload>
            </div>

            <div class="submit-section">
                <el-button type="primary" @click="submitComment" :loading="submitting">发布评价</el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { post } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 获取从BusinessInfo传递过来的商家信息
const business = ref(route.query.business ? JSON.parse(route.query.business) : {})

// 用户登录信息
const account = JSON.parse(sessionStorage.getItem('account'))

// 评论表单数据
const commentForm = ref({
    accountId: account ? account.accountId : '',
    businessId: business.value.businessId,
    orderId: 113, // 暂时使用默认值
    rate: 5,
    coText: '',
    coImg: ''
})

const submitting = ref(false)

// 上传图片前的验证
const beforeUpload = (file) => {
    const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isJPGOrPNG) {
        ElMessage.error('只能上传JPG或PNG格式的图片！')
        return false
    }
    if (!isLt2M) {
        ElMessage.error('图片大小不能超过2MB！')
        return false
    }
    return true
}

// 图片上传成功的回调
const handleUploadSuccess = (response) => {
    if (response.code === 20000) {
        commentForm.value.coImg = response.data
        ElMessage.success('图片上传成功')
    } else {
        ElMessage.error('图片上传失败')
    }
}

// 提交评论
const submitComment = async () => {
    if (!account) {
        ElMessage.error('请先登录')
        router.push('/login')
        return
    }

    if (!commentForm.value.coText.trim()) {
        ElMessage.error('请输入评价内容')
        return
    }

    submitting.value = true
    try {
        // 使用true参数确保以JSON格式发送数据
        const res = await post('/comment/add', commentForm.value, true)
        if (res.data && res.data.code === 20000) {
            ElMessage.success('评价发布成功')
            router.back()
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
</script>

<style scoped>
.wrapper {
    width: 100%;
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 20px;
}

header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 44px;
    background-color: #fff;
    display: flex;
    align-items: center;
    padding: 0 15px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    z-index: 100;
}

header i {
    font-size: 24px;
    color: #333;
    cursor: pointer;
}

header p {
    flex: 1;
    text-align: center;
    font-size: 18px;
    color: #333;
    margin: 0;
}

.comment-form {
    margin-top: 54px;
    padding: 15px;
}

.business-info {
    background: #fff;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 15px;
    display: flex;
    align-items: center;
}

.business-logo {
    margin-right: 15px;
}

.business-logo img {
    width: 60px;
    height: 60px;
    border-radius: 4px;
    object-fit: cover;
}

.business-text {
    flex: 1;
}

.business-name {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 5px;
    display: block;
}

.business-address {
    font-size: 12px;
    color: #666;
}

.rating-section,
.comment-section,
.upload-section {
    background: #fff;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 15px;
}

.rating-label {
    display: block;
    margin-bottom: 10px;
    color: #333;
    font-size: 14px;
}

.submit-section {
    margin-top: 20px;
    padding: 0 15px;
}

.submit-section .el-button {
    width: 100%;
    height: 44px;
    font-size: 16px;
}

.upload-section .el-upload__tip {
    font-size: 12px;
    color: #666;
    margin-top: 5px;
}
</style>