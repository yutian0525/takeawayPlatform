<template>
    <div class='wrapper'>
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>我的评价</p>
        </header>

        <div class="comments-section">
            <div class="comments-header">
                <h3>我的评价记录</h3>
                <div class="filter-buttons">
                    <el-button :class="{ active: activeFilter === 'all' }" @click="filterComments('all')">全部</el-button>
                    <el-button :class="{ active: activeFilter === 'positive' }" @click="filterComments('positive')">好评</el-button>
                    <el-button :class="{ active: activeFilter === 'negative' }" @click="filterComments('negative')">差评</el-button>
                </div>
            </div>

            <div class="comments-list" v-if="filteredComments.length > 0">
                <div v-for="comment in filteredComments" :key="comment.commentId" class="comment-item">
                    <div class="business-info">
                        <div class="business-logo" v-if="comment.business_img">
                            <img :src="comment.business_img" alt="商家图片">
                        </div>
                        <div class="business-text">
                            <span class="business-name">{{ comment.business_name || '未知商家' }}</span>
                            <div class="business-details">
                                <span class="business-address">{{ comment.business_address }}</span>
                                <span class="business-explain">{{ comment.business_explain }}</span>
                            </div>
                        </div>
                    </div>
                    <div class="rating">
                        <el-rate v-model="comment.score" disabled show-score text-color="#ff9900"></el-rate>
                        <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                    </div>
                    <div class="comment-content">
                        <p>{{ comment.content }}</p>
                        <div v-if="comment.co_img" class="comment-image-container">
                            <img :src="comment.co_img" alt="评论图片" class="comment-image" />
                        </div>
                    </div>
                    <div class="order-items" v-if="comment.orderItems">
                        <span class="item" v-for="item in comment.orderItems" :key="item.goodsId">
                            {{ item.goodsName }}
                        </span>
                    </div>
                    <div class="comment-actions">
                        <el-button type="primary" size="small" @click="editComment(comment)">修改</el-button>
                        <el-button type="danger" size="small" @click="deleteComment(comment)">删除</el-button>
                    </div>
                </div>
            </div>
            <div v-else class="no-comments">
                <el-empty description="暂无评价"></el-empty>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSession } from '@/utils/storage'

const router = useRouter()

// 获取当前登录用户信息
const account = getSession('account')
if (!account) {
    ElMessage.error('请先登录')
    router.push('/login')
}

// 评论列表数据
const comments = ref([])
const activeFilter = ref('all')

// 加载评论列表
const loadComments = async () => {
    try {
        const res = await get(`/comment/list/user/${account.accountId}`)
        if (res.data && res.data.code === 20000) {
            comments.value = res.data.resultdata.map(comment => ({
                ...comment,
                commentId: comment.co_id,
                score: comment.rate,
                content: comment.co_text,
                createTime: comment.created,
                orderItems: [] // 订单商品列表待实现
            })) || []
            console.log('我的评价列表:', comments.value)
        } else {
            ElMessage.error(res.data ? res.data.message : '加载评价失败')
        }
    } catch (error) {
        console.error('加载评价请求异常:', error)
        ElMessage.error('加载评价异常')
    }
}

// 格式化日期
const formatDate = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 筛选评论
const filterComments = (filter) => {
    activeFilter.value = filter
}

// 根据筛选条件过滤评论
const filteredComments = computed(() => {
    switch (activeFilter.value) {
        case 'positive':
            return comments.value.filter(comment => comment.score >= 4)
        case 'negative':
            return comments.value.filter(comment => comment.score < 3)
        default:
            return comments.value
    }
})

// 编辑评论 - 跳转到编辑页面
const editComment = (comment) => {
    router.push(`/editComment/${comment.commentId}`)
}

// 删除评论
const deleteComment = async (comment) => {
    try {
        await ElMessageBox.confirm('确定要删除这条评价吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        
        const res = await post(`/comment/delete/${comment.commentId}`)
        if (res.data && res.data.code === 20000) {
            ElMessage.success('评价删除成功')
            // 重新加载评论列表
            loadComments()
        } else {
            ElMessage.error(res.data ? res.data.message : '删除评价失败')
        }
    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除评价请求异常:', error)
            ElMessage.error('删除评价失败')
        }
    }
}

onMounted(() => {
    if (account) {
        loadComments()
    }
})
</script>

<style scoped>
.wrapper {
    padding: 0;
    background-color: #f8f8f8;
    min-height: 100vh;
    padding-top: 12vw;
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

header .fa-angle-left {
    position: absolute;
    left: 4vw;
    font-size: 6vw;
    color: #596164;
}

.comments-section {
    background: #fff;
    padding: 15px;
}

.comments-header {
    margin-bottom: 15px;
}

.comments-header h3 {
    margin: 0 0 10px;
}

.filter-buttons {
    display: flex;
    gap: 10px;
}

.filter-buttons .el-button {
    padding: 6px 15px;
}

.filter-buttons .el-button.active {
    background-color: #409EFF;
    color: #fff;
}

.comment-item {
    border-bottom: 1px solid #eee;
    padding: 15px 0;
}

.comment-item:last-child {
    border-bottom: none;
}

.business-info {
    margin-bottom: 10px;
    display: flex;
    align-items: flex-start;
}

.business-logo {
    margin-right: 10px;
}

.business-logo img {
    width: 50px;
    height: 50px;
    border-radius: 4px;
    object-fit: cover;
}

.business-text {
    flex: 1;
}

.business-name {
    font-weight: bold;
    color: #333;
    font-size: 16px;
    display: block;
    margin-bottom: 5px;
}

.business-details {
    display: flex;
    flex-direction: column;
    gap: 4px;
    font-size: 12px;
    color: #666;
}

.business-address {
    color: #666;
}

.business-explain {
    color: #999;
}

.rating {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}

.comment-time {
    margin-left: 10px;
    color: #999;
    font-size: 12px;
}

.comment-content {
    margin-bottom: 10px;
    line-height: 1.5;
}

.comment-image-container {
    margin-top: 10px;
}

.comment-image {
    max-width: 20vw;
    height: auto;
    border-radius: 4px;
    display: block;
}

.order-items {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.order-items .item {
    background: #f5f5f5;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    color: #666;
}

.no-comments {
    text-align: center;
    padding: 30px 0;
}

.comment-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 10px;
}
</style>