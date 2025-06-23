<template>
    <div class="wrapper">
        <header>
        <p>搜 索 结 果</p>
        </header>
        <!-- 搜索栏 -->
        <div class="search">
            <div class="search-top" ref="fixedBox">
                <div class="search-box">
                    <i class="search_icon" aria-hidden="true" />
                    <input type="text" placeholder="搜索商家名称、商品名称" v-model="searchQuery" @keyup.enter="loadSuggestion(searchQuery)"/>
                </div>
            </div>
        </div>
        <!--搜索结果不为空-->
        <ul class="business">
            <li v-for="business in businessList" :key="business.businessId" @click="toBusinessInfo(business.businessId)">
                <div style="position: relative; z-index:0">
                    <img :src="business.businessImg" />
                    <div class="business-img-quantity">
                    5
                    </div>
                </div>
                <div class="business-info">
                    <div class="business-info-h">
                        <h3>{{ business.businessName }}</h3>
                            <div class="business-info-like">
                            <i class="heart_icon" />
                            </div>
                    </div>
                    <div class="business-info-star">
                        <div class="business-info-star-left">
                            <i class="el-icon-star-on fa-star" v-for="n in Math.floor(5.0)" :key="n" />
                            <p> 5.0  月售345单</p>
                        </div>
                    <div class="business-info-star-right">
                        {{ business.remarks }}
                    </div>
                    </div>
                    <div class="business-info-delivery">
                        <p>&#165; {{ business.starPrice }} 起送 | &#165; {{ business.deliveryPrice }} 配送</p>
                    </div>
                    <div class="business-info-explain">
                        <div>
                        {{ business.businessExplain }}
                        </div>
                    </div>
                </div>
            </li>
        </ul>
        <!--搜索结果为空---->
        <ul class="NullInfo" v-if="businessList.length === 0">
            <li>未查询到与 "{{ searchQuery }}" 相关的内容</li>
        </ul>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { get } from '@/api/index';
import { useRoute, useRouter } from 'vue-router';

let businessList = ref([]);
const route = useRoute();
const router = useRouter();
let searchQuery = route.query.searchQuery;

//输出搜索结果
const loadSuggestion = (searchQuery) =>{
    businessList.value = [];
    loadBusiness();
    loadGoodsBusiness();
}

// 添加数据并去重
const mergeUniqueBusinessList = (newData, uniqueKey = 'businessId') => {
    const map = new Map();
    businessList.value.forEach(item => map.set(item[uniqueKey], item));
    newData.forEach(item => map.set(item[uniqueKey], item));
    businessList.value = Array.from(map.values());
};

// 加载搜索得到的商家数据
const loadBusiness = async () => {
    try {
        const res = await get(`/business/search/${encodeURIComponent(searchQuery)}`);
        if (res.data.resultdata && Array.isArray(res.data.resultdata)) {
            mergeUniqueBusinessList(res.data.resultdata);
            console.log(searchQuery);
            console.log(encodeURIComponent(searchQuery));
            console.log(res.data.resultdata);
            
        } else {
            console.error('无效的数据格式', res);
        }
    } catch (error) {
        console.error('加载商家数据失败:', error);
    }
};

// 加载搜索到的商品商家信息
const loadGoodsBusiness = async () => {
    try {
        const res = await get(`/goods/search/${encodeURIComponent(searchQuery)}`);
        if (res.data.resultdata && Array.isArray(res.data.resultdata)) {
            mergeUniqueBusinessList(res.data.resultdata);
            console.log(searchQuery);
            console.log(encodeURIComponent(searchQuery));
            console.log(res.data.resultdata)
        } else {
            console.error('无效的数据格式', res);
        }
    } catch (error) {
        console.error('加载商家数据失败:', error);
    }
};

const toBusinessInfo=(id)=>{
    router.push({path:'/businessInfo',query:{businessId:id}});
}

// 页面加载时执行
onMounted(() => {
    loadBusiness();
    loadGoodsBusiness();
});
</script>

<style scoped>
/* 全局样式 */
.wrapper {
    font-family: Arial, sans-serif;
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

header {
    text-align: center;
    margin-bottom: 20px;
}

header p {
    font-size: 24px;
    font-weight: bold;
    color: #333;
}

/* 搜索栏样式 */
.search {
    display: flex;
    justify-content: center;
    margin-top: 30px;
}

.search-top {
    display: flex;
    align-items: center;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 4px;
    overflow: hidden;
}

.search-box {
    display: flex;
    align-items: center;
    width: 100%;
}

.search_icon {
    margin: 0 10px;
    font-size: 18px;
    color: #888;
}

input[type="text"] {
    width: 100%;
    padding: 10px;
    border: none;
    outline: none;
    font-size: 16px;
}

/* 搜索结果样式 */
.business {
    list-style: none;
    padding: 0;
    margin: 0;
}

.business li {
    display: flex;
    align-items: center;
    padding: 15px;
    border-bottom: 1px solid #ddd;
    cursor: pointer;
    transition: background-color 0.3s;
}

.business li:hover {
    background-color: #f0f0f0;
}

.business img {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    margin-right: 15px;
}

.business-img-quantity {
    position: absolute;
    top: 0;
    right: 0;
    background-color: #ff4500;
    color: #fff;
    font-size: 12px;
    padding: 2px 6px;
    border-radius: 12px;
}

.business-info {
    flex: 1;
}

.business-info-h {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.business-info-h h3 {
    font-size: 18px;
    font-weight: bold;
    color: #333;
}

.business-info-like {
    color: #ff4500;
    font-size: 20px;
}

.business-info-star {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 5px;
}

.business-info-star-left {
    display: flex;
    align-items: center;
}

.business-info-star-left p {
    margin-left: 5px;
    font-size: 14px;
    color: #666;
}

.business-info-star-right {
    font-size: 14px;
    color: #666;
}

.business-info-delivery {
    margin-top: 5px;
    font-size: 14px;
    color: #666;
}

.business-info-explain {
    margin-top: 5px;
    font-size: 14px;
    color: #666;
}

/* 空结果提示样式 */
.NullInfo {
    list-style: none;
    padding: 0;
    margin: 20px 0;
    text-align: center;
    color: #888;
}

.NullInfo li {
    padding: 10px;
    font-size: 16px;
}
</style>