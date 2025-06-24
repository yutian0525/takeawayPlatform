<template>
    <div class="wrapper">
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>修改评价</p>
        </header>

        <div class="comment-form" v-if="comment">
            <div class="business-info" v-if="businessInfo">
                <div class="business-logo" v-if="businessInfo.business_img">
                    <img :src="businessInfo.business_img" alt="商家图片">
                </div>
                <div class="business-text">
                    <span class="business-name">{{ businessInfo.business_name || '未知商家' }}</span>
                    <div class="business-details">
                        <span class="business-address">{{ businessInfo.business_address }}</span>
                        <span class="business-explain">{{ businessInfo.business_explain }}</span>
                    </div>
                </div>
            </div>

            <div class="rating-section">
                <span class="rating-label">总体评分</span>
                <el-rate v-model="comment.rate" :texts="['很差', '较差', '一般', '不错', '很好']" show-text></el-rate>
            </div>

            <div class="comment-section">
                <el-input
                    v-model="comment.coText"
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
                    <div v-if="!imagePreview && !comment.coImg" class="upload-box" @click="triggerFileInput">
                        <i class="el-icon-plus"></i>
                        <p>点击上传图片</p>
                    </div>
                    <div v-else class="preview-container">
                        <img :src="imagePreview || comment.coImg" class="preview-image">
                        <div class="preview-actions">
                            <button @click="removeImage" class="remove-btn">删除</button>
                        </div>
                    </div>
                </div>
                <div class="upload-tip">只能上传jpg/png文件，且不超过2MB</div>
            </div>

            <div class="submit-section">
                <el-button type="primary" @click="submitEdit" :loading="submitting">保存修改</el-button>
                <el-button type="danger" @click="deleteComment" :loading="deleting">删除评价</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

// 获取路由参数中的commentId
const commentId = route.params.commentId

// 评论信息
const comment = ref(null)
const businessInfo = ref(null)
const submitting = ref(false)
const deleting = ref(false)
const imagePreview = ref('')
const imageBase64 = ref('')

// 用户登录信息
const account = JSON.parse(sessionStorage.getItem('account'))

// 加载评论信息
const loadComment = async () => {
    if (!commentId) {
        ElMessage.error('评论ID不存在')
        router.back()
        return
    }

    try {
        const response = await get(`/comment/detail/${commentId}`)
        if (response.data.code === 20000) {
            comment.value = response.data.resultdata
            console.log('评论信息:', comment.value)
            // 加载商家信息
            loadBusinessInfo()
        } else {
            ElMessage.error('获取评论信息失败')
            router.back()
        }
    } catch (error) {
        console.error('加载评论信息失败:', error)
        ElMessage.error('加载评论信息失败')
        router.back()
    }
}

// 加载商家信息
const loadBusinessInfo = async () => {
    if (!comment.value || !comment.value.businessId) return
    
    try {
        const response = await get(`/business/info/${comment.value.businessId}`)
        if (response.data.code === 20000) {
            // 处理后端返回的字段名与前端期望的字段名不一致的问题
            const data = response.data.resultdata
            businessInfo.value = {
                business_img: data.businessImg,
                business_name: data.businessName,
                business_address: data.businessAddress,
                business_explain: data.businessExplain
            }
            console.log('商家信息:', businessInfo.value)
        }
    } catch (error) {
        console.error('加载商家信息失败:', error)
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
    // 如果是编辑模式下的原有图片，也需要清除
    if (comment.value && comment.value.coImg) {
        comment.value.coImg = ''
    }
    // 重置文件输入框
    const fileInput = document.querySelector('input[type="file"]')
    if (fileInput) fileInput.value = ''
}

// 提交修改
const submitEdit = async () => {
    if (!account) {
        ElMessage.error('请先登录')
        router.push('/login')
        return
    }

    if (!comment.value.coText.trim()) {
        ElMessage.error('请输入评价内容')
        return
    }

    submitting.value = true
    try {
        // 如果有新上传的图片，使用新的base64数据
        if (imageBase64.value) {
            comment.value.coImg = imageBase64.value
        }
        
        const res = await post('/comment/update', comment.value, true)
        if (res.data && (res.data.code === 20000 || res.data.code === 200)) {
            ElMessage.success('评价修改成功')
            router.push('/myComments') // 修改成功后返回我的评价列表
        } else {
            ElMessage.error(res.data ? res.data.message : '评价修改失败')
        }
    } catch (error) {
        console.error('修改评价请求异常:', error)
        ElMessage.error('修改评价异常')
    } finally {
        submitting.value = false
    }
}

// 删除评论
const deleteComment = async () => {
    try {
        await ElMessageBox.confirm('确定要删除这条评价吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        
        deleting.value = true
        // 使用post方法调用删除接口
        const res = await post(`/comment/delete/${comment.value.coId}`)
        if (res.data && (res.data.code === 20000 || res.data.code === 200)) {
            ElMessage.success('评价删除成功')
            router.push('/myComments') // 删除成功后返回我的评价列表
        } else {
            ElMessage.error(res.data ? res.data.message : '评价删除失败')
        }
    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除评价请求异常:', error)
            ElMessage.error('删除评价失败')
        }
    } finally {
        deleting.value = false
    }
}

onMounted(() => {
    if (!account) {
        ElMessage.error('请先登录')
        router.push('/login')
        return
    }
    loadComment()
})
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

.business-details {
    font-size: 12px;
    color: #666;
}

.business-address, .business-explain {
    margin-right: 10px;
}

.rating-section, .comment-section, .upload-section {
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
    display: flex;
    justify-content: space-between;
}

.loading {
    margin-top: 54px;
    padding: 20px;
    text-align: center;
    color: #666;
}

.upload-title {
    font-size: 14px;
    color: #333;
    margin-bottom: 10px;
}

.image-upload {
    display: flex;
    flex-wrap: wrap;
}

.upload-box {
    width: 100px;
    height: 100px;
    border: 1px dashed #d9d9d9;
    border-radius: 4px;
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
    font-size: 24px;
    color: #8c939d;
    margin-bottom: 8px;
}

.upload-box p {
    font-size: 12px;
    color: #8c939d;
    margin: 0;
}

.preview-container {
    position: relative;
    margin-right: 10px;
    margin-bottom: 10px;
}

.preview-image {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 4px;
}

.preview-actions {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    padding: 4px 0;
    border-bottom-left-radius: 4px;
    border-bottom-right-radius: 4px;
}

.remove-btn {
    background: none;
    border: none;
    color: #fff;
    font-size: 12px;
    cursor: pointer;
}

.upload-tip {
    font-size: 12px;
    color: #8c939d;
    margin-top: 8px;
}
</style>