<template>
    <div class='wrapper'>
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i> <!-- 添加返回按钮 -->
            <p>商 家 详 细</p>
        </header>
        <!-- 商家信息-->
        <div class="business-info-banner">
            <!-- 查看全部评论-->
            <div class="top-right">
                <div class="AllComment" @click="toComments">
                    查看评论
                    <i class="el-icon-d-arrow-right"></i>
                </div>
            </div>

            <div class="business-logo">
                <img :src="business.businessImg">
            </div>

            <div class="business-info">
                <h1>{{ business.businessName }}</h1>
                <p style="color: #333; align">&#165; {{ business.starPrice }}起送 &nbsp;|&nbsp; &#165; {{
                business.deliveryPrice }} 配送</p>
                <p>{{ business.businessExplain }}</p>
            </div>
            <ul>
                <li v-for="(item, index) in goods" :key="item.goodsId" class="list-item">
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
                            <el-button @click="minus(item)" :disabled="item.quantity === 0">
                                <minus />-
                            </el-button>
                            <span>{{ item.quantity }}</span>
                            <el-button @click="add(item)">
                                <plus />+
                            </el-button>
                        </div>
                        <div>
                            <i class="el-icon-circle-plus-online"></i>
                        </div>
                    </div>
                    <div class="mask" v-if="item.statu == 0">
                        <span>抱歉，该商品已下架</span>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import Footer from '../components/Footer.vue'
import { ref, onMounted, computed } from "vue"
import { get, post } from '@/api';
import { useRouter, useRoute } from "vue-router"
import { Plus, Minus } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();
const business = ref([]);

// 获得商家编号
let businessId = route.query.businessId;

// 用户登录信息
const account = JSON.parse(sessionStorage.getItem("account"));

// 页面显示商品列表对象
const goods = ref([]);
const cart = ref(JSON.parse(sessionStorage.getItem('cart')) || []);

// 加载商家详情
const loadBusinessInfo = () => {
    let url = `/business/info/${businessId}`;
    get(url).then(res => {
        if (res.data && res.data.code === 20000) {
            business.value = res.data.resultdata;
            console.log(business.value);
        } else {
            ElMessage({
                message: res.data ? res.data.message : '加载商家信息失败',
                type: 'error',
            });
        }
    }).catch(error => {
        console.error('加载商家信息请求异常:', error);
        ElMessage.error('加载商家信息异常');
    });
};

// 根据商家编号加载商品列表
const loadGoodsByBusinessId = () => {
    let url = `/business/listByBusinessId/${businessId}`;
    get(url).then(res => {
        if (res.data && res.data.code === 20000) {
            let tempArray = res.data.resultdata;
            if (Array.isArray(tempArray)) {
                for (let i = 0; i < tempArray.length; i++) {
                    tempArray[i].quantity = 0;
                    const cartItem = cart.value.find(c => c.goodsId === tempArray[i].goodsId);
                    if (cartItem) {
                        tempArray[i].quantity = cartItem.quantity;
                    }
                }
                goods.value = tempArray;
            } else {
                console.warn('后端返回的商品列表不是一个数组:', tempArray);
                goods.value = [];
            }
        } else {
            ElMessage({
                message: res.data ? res.data.message : '商品数据加载失败',
                type: 'error',
            });
        }
    }).catch(error => {
        console.error('加载商品列表请求异常:', error);
        ElMessage.error('加载商品列表异常');
    });
};

// 添加商品
const add = async (item) => {
    if (!account || !account.accountId) {
        ElMessage.error('请先登录');
        router.push('/login');
        return;
    }

    const goodsItemInCart = cart.value.find(cartItem => cartItem.goodsId === item.goodsId);
    const originalQuantity = goodsItemInCart ? goodsItemInCart.quantity : 0;

    // 乐观更新本地数量
    if (goodsItemInCart) {
        goodsItemInCart.quantity++;
    } else {
        item.quantity = 1;
        cart.value.push(item);
    }
    // 同步更新 goods 列表中的商品数量，确保 UI 反映最新状态
    const goodsItemInList = goods.value.find(g => g.goodsId === item.goodsId);
    if (goodsItemInList) {
        goodsItemInList.quantity = goodsItemInCart ? goodsItemInCart.quantity : item.quantity;
    }

    let apiEndpoint = '';
    let requestBody = {
        goodsId: item.goodsId,
        businessId: business.value.businessId,
        accountId: account.accountId,
        quantity: goodsItemInCart ? goodsItemInCart.quantity : item.quantity,
    };

    if (originalQuantity === 0) {
        // 第一次添加，调用 /cart/add
        apiEndpoint = '/cart/add';
    } else {
        // 已经存在，更新数量，调用 /cart/update
        apiEndpoint = '/cart/update';
    }

    try {
        const res = await post(apiEndpoint, requestBody, true);

        if (res.data && res.data.code === 20000) {
            ElMessage.success('商品已添加到购物车');
            sessionStorage.setItem('cart', JSON.stringify(cart.value));
            sessionStorage.setItem('business', JSON.stringify(business.value));
        } else {
            ElMessage.error(res.data ? res.data.message : '添加购物车失败');
            // 后端操作失败，回滚本地数量变化
            if (goodsItemInCart) {
                goodsItemInCart.quantity--;
            } else {
                cart.value.pop();
            }
            if (goodsItemInList) {
                goodsItemInList.quantity = originalQuantity;
            }
        }
    } catch (error) {
        console.error('添加购物车请求异常:', error);
        ElMessage.error('添加购物车异常');
        // 请求本身失败，回滚本地数量变化
        if (goodsItemInCart) {
            goodsItemInCart.quantity--;
        } else {
            cart.value.pop();
        }
        if (goodsItemInList) {
            goodsItemInList.quantity = originalQuantity;
        }
    }
};

// 减少商品
const minus = async (item) => {
    if (!account || !account.accountId) {
        ElMessage.error('请先登录');
        router.push('/login');
        return;
    }

    const goodsItemInCart = cart.value.find(cartItem => cartItem.goodsId === item.goodsId);

    if (!goodsItemInCart || goodsItemInCart.quantity === 0) {
        return; // 如果购物车中没有该商品或数量已为0，则不执行任何操作
    }

    const originalQuantity = goodsItemInCart.quantity;

    // 乐观更新本地数量
    goodsItemInCart.quantity--;

    // 同步更新 goods 列表中的商品数量
    const goodsItemInList = goods.value.find(g => g.goodsId === item.goodsId);
    if (goodsItemInList) {
        goodsItemInList.quantity = goodsItemInCart.quantity;
    }

    let apiEndpoint = '';
    let requestBody = {
        goodsId: item.goodsId,
        businessId: business.value.businessId,
        accountId: account.accountId,
        quantity: goodsItemInCart.quantity,
    };

    if (goodsItemInCart.quantity === 0) {
        // 数量变为0，调用 /cart/remove
        apiEndpoint = '/cart/remove';
        // 对于 remove 接口，quantity 字段可能不需要，但为了统一传递，可以保留
        // requestBody = { goodsId: item.goodsId, businessId: business.value.businessId, accountId: account.accountId };
    } else {
        // 数量大于0，更新数量，调用 /cart/update
        apiEndpoint = '/cart/update';
    }

    try {
        const res = await post(apiEndpoint, requestBody, true);

        if (res.data && res.data.code === 20000) {
            ElMessage.success('商品数量已更新');
            if (goodsItemInCart.quantity === 0) {
                const index = cart.value.indexOf(goodsItemInCart);
                cart.value.splice(index, 1); // 如果数量为0，从本地购物车中移除
            }
            sessionStorage.setItem('cart', JSON.stringify(cart.value));
        } else {
            ElMessage.error(res.data ? res.data.message : '更新购物车失败');
            // 后端操作失败，回滚本地数量变化
            goodsItemInCart.quantity = originalQuantity;
            if (goodsItemInList) {
                goodsItemInList.quantity = originalQuantity;
            }
        }
    } catch (error) {
        console.error('更新购物车请求异常:', error);
        ElMessage.error('更新购物车异常');
        // 请求本身失败，回滚本地数量变化
        goodsItemInCart.quantity = originalQuantity;
        if (goodsItemInList) {
            goodsItemInList.quantity = originalQuantity;
        }
    }
};

// 计算总数 (此函数未直接使用，但保留)
const totalQuantity = computed(() => {
    return cart.value.reduce((total, item) => total + item.quantity, 0);
});

// 跳转到评论页面
const toComments = () => {
    router.push({
        path: '/comments',
        query: {
            business: JSON.stringify(business.value)
        }
    });
};

onMounted(() => {
    loadBusinessInfo();
    loadGoodsByBusinessId();
});
</script>

<style scoped>
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
    width: 100px;
    height: 100px;
    border-radius: 50%;
    object-fit: cover;
    margin-bottom: 10px;
}

.business-info h1 {
    font-size: 24px;
    margin: 0 0 5px;
    color: #333;
}

.business-info p {
    font-size: 14px;
    color: #666;
    margin: 0;
}

ul {
    list-style: none;
    padding: 0;
    margin: 0;
}

.list-item {
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
    position: relative;
    /* 为li建立定位上下文 */
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

/* 顶部标题栏 */
.wrapper header {
    width: 100%;
    height: 12vw;
    background: linear-gradient(to right, #fff1eb, #ace0f9);
    /* 示例背景色，可根据实际情况调整 */
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

.wrapper header p {
    letter-spacing: 2vw;
}

.wrapper header .fa-angle-left {
    /* 返回按钮样式 */
    position: absolute;
    left: 4vw;
    font-size: 6vw;
    color: #596164;
}
</style>