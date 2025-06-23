<template>
    <div class='wrapper'>
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>商家评论</p>
        </header>
        <div class="business-info">
            <div class="business-logo">
                <img :src="business.businessImg">
            </div>
            <div class="business-details">
                <h2>{{ business.businessName }}</h2>
                <p>{{ business.businessExplain }}</p>
                <div class="business-stats">
                    <span>评分: {{ business.score || '暂无' }}</span>
                    <span>月售: {{ business.monthlySale || 0 }}</span>
                    <span>配送: ¥{{ business.deliveryPrice }}</span>
                </div>
            </div>
        </div>

        <div class="comments-section">
            <div class="comments-header">
                <h3>用户评论</h3>
                <div class="filter-buttons">
                    <el-button :class="{ active: activeFilter === 'all' }" @click="filterComments('all')">全部</el-button>
                    <el-button :class="{ active: activeFilter === 'positive' }" @click="filterComments('positive')">好评</el-button>
                    <el-button :class="{ active: activeFilter === 'negative' }" @click="filterComments('negative')">差评</el-button>
                </div>
            </div>

            <div class="comments-list" v-if="filteredComments.length > 0">
                <div v-for="comment in filteredComments" :key="comment.commentId" class="comment-item">
                    <div class="user-info">
                        <img :src="comment.userAvatar || defaultAvatar" class="user-avatar">
                        <span class="user-name">{{ comment.userName || '匿名用户' }}</span>
                        <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                    </div>
                    <div class="rating">
                        <el-rate v-model="comment.score" disabled show-score text-color="#ff9900"></el-rate>
                    </div>
                    <div class="comment-content">
                        <p>{{ comment.content }}</p>
                    </div>
                    <div class="order-items" v-if="comment.orderItems">
                        <span class="item" v-for="item in comment.orderItems" :key="item.goodsId">
                            {{ item.goodsName }}
                        </span>
                    </div>
                </div>
            </div>
            <div v-else class="no-comments">
                <el-empty description="暂无评论"></el-empty>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { get } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 获取从BusinessInfo传递过来的商家信息
const business = ref(route.query.business ? JSON.parse(route.query.business) : {})

// 评论列表数据
const comments = ref([])
const activeFilter = ref('all')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 加载评论列表
const loadComments = async () => {
    try {
        const res = await get(`/comment/list/${business.value.businessId}`)
        if (res.data && res.data.code === 20000) {
            comments.value = res.data.resultdata || []
        } else {
            ElMessage.error(res.data ? res.data.message : '加载评论失败')
        }
    } catch (error) {
        console.error('加载评论请求异常:', error)
        ElMessage.error('加载评论异常')
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

onMounted(() => {
    if (!business.value.businessId) {
        ElMessage.error('商家信息不完整')
        router.back()
        return
    }
    loadComments()
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

.business-info {
    background: #fff;
    padding: 15px;
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}

.business-logo img {
    width: 60px;
    height: 60px;
    border-radius: 5px;
    margin-right: 15px;
}

.business-details h2 {
    margin: 0 0 5px;
    font-size: 18px;
}

.business-details p {
    margin: 0 0 8px;
    color: #666;
    font-size: 14px;
}

.business-stats span {
    margin-right: 15px;
    color: #666;
    font-size: 13px;
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

.user-info {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}

.user-avatar {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    margin-right: 10px;
}

.user-name {
    font-weight: 500;
    margin-right: 10px;
}

.comment-time {
    color: #999;
    font-size: 12px;
}

.rating {
    margin-bottom: 10px;
}

.comment-content {
    margin-bottom: 10px;
    line-height: 1.5;
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
</style>