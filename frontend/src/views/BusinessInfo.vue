<template>
    <div class = 'wrapper'>
        <header>
            <p>商 家 详 细</p>
        </header>
        <!-- 商家信息-->
         <div class="business-info-banner">
            <!-- 查看全部评论-->
             <div class="top-right">
                <div class="AllComment">
                    查看评论
                    <i class="el-icon-d-arrow-right"></i>
                </div>
             </div>

             <div class="business-logo">
                <img :src="business.businessImg">
             </div>

             <div class="business-info">
                <h1>{{ business.businessName }}</h1>
                <p style="color: #333; align">&#165;  {{ business.starPrice }}起送 &nbsp;|&nbsp; &#165; {{ business.deliveryPrice }} 配送</p>
                <p>{{ business.businessExplain }}</p>
             </div>
             <ul>
                <li v-for="(item,index) in goods" :key="item.goodsId" class="list-item">
                    <div class="food-left">
                        <!--根据remark属性是否有值，动态显示-->
                        <div class="dish-img-feature" v-if="item.remarkers">
                            {{ item.remarks }}
                        </div>
                        <img :src="item.goodsImg">
                        <div class="food-left-info">
                            <h3>{{ item.goodsName }}</h3>
                            <p>{{ item.goodsExplain }}</p>
                            <p class="price">&#165; <em>{{ item.goodsPrice }}</em></p>
                        </div>
                    </div>
                    <div class="food-right">
                        <!-- disabled 标签失败（不可用）-->
                        <div class="quantity-control">
                            <el-button>
                                <minus />-
                            </el-button>
                            <span>{{ item.quantity }}</span>
                            <el-button>
                                <plus />+
                            </el-button>
                        </div>
                        <div>
                            <i class="el-icon-circle-plus-online"></i>
                        </div>
                    </div>
                    <div class="mask" v-if="item.statu==0">
                        <span>抱歉，该商品已下架</span>
                    </div>
                </li>
             </ul>
         </div>
    </div>
</template>

<script setup >
import Footer from '../components/Footer.vue'
import {ref,onMounted} from "vue"
import { get } from '@/api';
import {useRouter,useRoute} from "vue-router"
import { Plus, Minus } from '@element-plus/icons-vue';
const router = useRouter();  //创建路由对象
const route = useRoute();
const business = ref([]);

// 获得商家编号
let businessId = route.query.businessId;

// 页面显示商品列表对象
const goods = ref([]);

// 加载商家详情
const loadBusinessInfo = () => {
    let url = `/business/info/${businessId}`;
    get(url).then(res => {
        if (res.data.code == 20000) {
            business.value = res.data.resultdata;
        } else {
            ElMessage({
                message: res.data.message,
                type: 'error',
            });
        }
    });
};

// 根据商家编号加载商品列表
const loadGoodsByBusinessId = () => {
    let url = `/business/listByBusinessId/${businessId}`;
    get(url).then(res => {
        if (res.data.code == 20000) {
            let tempArray = res.data.resultdata;
            // 循环 tempArray，给每个商品添加 quantity 属性
            for (let i = 0; i < tempArray.length; i++) {
                tempArray[i].quantity = 0; // 每个商品的数量默认为 0
            }
            goods.value = tempArray;
        } else {
            ElMessage({
                message: '商品数据加载失败',
                type: 'error',
            });
        }
    });
};

// 页面加载时调用方法
onMounted(() => {
    loadBusinessInfo();
    loadGoodsByBusinessId();
});
</script>
<style>
.wrapper {
    padding: 0;
    background-color: #f8f8f8;
    margin-top: 50px;
}

header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #e0f7fa;
    padding: 10px;
    border-radius: 5px;
}

.business-info-banner {
    background-color: #fff;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    position: relative;
    text-align: center;
}

.top-right {
    position: absolute;
    top: 10px;
    right: 10px;
    display: flex;
    align-items: center;
}

.AllComment {
    font-size: 16px;
    color: #333;
    cursor: pointer;
}

.AllComment i {
    margin-left: 5px;
    font-size: 14px;
}

.business-logo img {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    margin-bottom: 10px;
}

.business-info h1 {
    font-size: 24px;
    margin: 0 0 10px;
    color: #333;
}

.business-info p {
    font-size: 14px;
    color: #666;
    margin: 5px 0;
}

.business-info p:first-child {
    color: #ff5722;
}

ul {
    list-style: none;
    padding: 0;
    margin-top: 20px;
}

li {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #eee;
}

.food-left {
    display: flex;
    align-items: center;
}

.dish-img-feature {
    background-color: #ff5722;
    color: #fff;
    padding: 2px 5px;
    border-radius: 3px;
    font-size: 12px;
    margin-right: 5px;
}

.food-left img {
    width: 70px;
    height: 70px;
    border-radius: 5px;
    margin-right: 10px;
}

.food-left-info h3 {
    font-size: 16px;
    text-align: left;
    margin: 0 0 5px;
    color: #333;
}

.food-left-info p {
    font-size: 14px;
    text-align: left;
    color: #666;
    margin: 0;
}

.price {
    font-size: 16px !important;
    color: #ff5722 !important;
}

.food-right {
    display: flex;
    align-items: center;
}

.food-right div {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 0 10px;
}

.food-right div i {
    font-size: 18px;
    cursor: pointer;
}

/* .list-item的定位样式 */
.list-item {
    position: relative;  /* 为li建立定位上下文 */
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #eee;
}

/* mask定位方式 */
.mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    color: white;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 5px;
    font-size: 14px; 
    z-index: 10; 
}
</style>