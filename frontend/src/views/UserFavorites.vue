<template>
    <div class="wrapper">
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>店铺关注</p>
        </header>

        <div class="favorites-container">
            <div v-if="favoriteList.length > 0" class="favorites-list">
                <div v-for="business in favoriteList" :key="business.businessId" class="business-item" @click="toBusinessInfo(business.businessId)">
                    <div class="business-logo">
                        <img :src="business.businessImg" alt="商家图片">
                    </div>
                    <div class="business-info">
                        <div class="business-name">{{ business.businessName }}</div>
                        <div class="business-desc">{{ business.businessExplain }}</div>
                        <div class="business-stats">
                            <span>评分: {{ business.score || '暂无' }}</span>
                            <span>月售: {{ business.monthlySale || 0 }}</span>
                            <span>配送: ¥{{ business.deliveryPrice }}</span>
                        </div>
                    </div>
                    <div class="business-actions">
                        <button class="unfavorite-btn" @click.stop="unfavoriteBusiness(business.businessId)">
                            <i class="fa fa-heart"></i> 取消关注
                        </button>
                    </div>
                </div>
            </div>
            <div v-else class="empty-state">
                <i class="fa fa-heart-o empty-icon"></i>
                <p>您还没有关注任何店铺</p>
                <button @click="router.push('/')" class="explore-btn">去浏览店铺</button>
            </div>
        </div>

        <Footer></Footer>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Footer from '@/components/Footer.vue';
import { get, post } from '@/api';
import { ElMessage } from 'element-plus';

const router = useRouter();
const favoriteList = ref([]);

// 获取当前登录用户信息
const account = JSON.parse(sessionStorage.getItem('account'));

// 加载用户关注的店铺列表
const loadFavorites = async () => {
    if (!account) {
        ElMessage.error('请先登录');
        router.push('/login');
        return;
    }

    try {
        const res = await get('/favorite/list', {
            accountId: account.accountId
        });
        console.log('收藏列表响应:', res.data);
        
        // 检查响应状态码
        if (res.data && (res.data.code === 200 || res.data.code === 20000)) {
            // 根据状态码确定数据来源
            let businessList = [];
            if (res.data.code === 20000 && res.data.resultdata) {
                businessList = res.data.resultdata;
            } else if (res.data.data) {
                businessList = res.data.data;
            }
            
            console.log('解析后的关注店铺列表:', businessList);
            favoriteList.value = businessList;
            
            if (favoriteList.value.length === 0) {
                console.log('收藏列表为空');
            }
        } else {
            ElMessage.error(res.data ? (res.data.msg || res.data.message || '加载关注店铺失败') : '加载关注店铺失败');
        }
    } catch (error) {
        console.error('加载关注店铺请求异常:', error);
        ElMessage.error('加载关注店铺异常');
    }
};

// 取消关注店铺
const unfavoriteBusiness = async (businessId) => {
    if (!account) {
        ElMessage.error('请先登录');
        router.push('/login');
        return;
    }

    try {
        const res = await post('/favorite/cancel', {
            accountId: account.accountId,
            businessId: businessId
        }, false); // 设置为false，使用表单格式提交
        
        console.log('取消收藏响应:', res.data);
        if (res.data && (res.data.code === 200 || res.data.code === 20000)) {
            ElMessage.success('取消关注成功');
            // 从列表中移除已取消关注的店铺
            favoriteList.value = favoriteList.value.filter(item => item.businessId !== businessId);
        } else {
            ElMessage.error(res.data ? (res.data.msg || res.data.message) : '取消关注失败');
        }
    } catch (error) {
        console.error('取消关注请求异常:', error);
        ElMessage.error('取消关注异常');
    }
};

// 跳转到商家详情页面
const toBusinessInfo = (businessId) => {
    router.push({ path: '/businessInfo', query: { businessId: businessId } });
};

// 页面加载时获取关注列表
onMounted(() => {
    loadFavorites();
});
</script>

<style scoped>
.wrapper {
    width: 100%;
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 20px;
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
    align-items: center;
    padding: 0 15px;
    box-sizing: border-box;
    z-index: 1000;
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
    letter-spacing: 2vw;
}

.favorites-container {
    margin-top: 14vw;
    padding: 10px;
}

.favorites-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.business-item {
    display: flex;
    background-color: #fff;
    border-radius: 8px;
    padding: 15px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    position: relative;
}

.business-logo {
    width: 80px;
    height: 80px;
    margin-right: 15px;
    flex-shrink: 0;
}

.business-logo img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 4px;
}

.business-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.business-name {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 5px;
}

.business-desc {
    font-size: 12px;
    color: #666;
    margin-bottom: 10px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
}

.business-stats {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    font-size: 12px;
    color: #666;
}

.business-actions {
    display: flex;
    align-items: center;
    margin-left: 10px;
}

.unfavorite-btn {
    background-color: #ff4d4f;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 5px 10px;
    font-size: 12px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 5px;
}

.unfavorite-btn i {
    font-size: 14px;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 50px 20px;
    text-align: center;
}

.empty-icon {
    font-size: 50px;
    color: #ccc;
    margin-bottom: 20px;
}

.empty-state p {
    color: #666;
    margin-bottom: 20px;
}

.explore-btn {
    background-color: #3cba92;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 10px 20px;
    font-size: 14px;
    cursor: pointer;
}
</style>