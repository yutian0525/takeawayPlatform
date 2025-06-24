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

.rating-section, .comment-section {
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
</style>