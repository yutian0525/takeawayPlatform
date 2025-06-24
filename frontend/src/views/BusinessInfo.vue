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
                                <i class="el-icon-minus">-</i>
                            </el-button>
                            <span>{{ item.quantity }}</span>
                            <el-button @click="add(item)">
                                <i class="el-icon-plus">+</i>
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
    <!-- 购物车按钮 -->
    <div class="cart-button" @click="openCartPopup">
        <img src="../assets/cart.png" alt="Cart" class="cart-icon"> <!-- 替换为图片 -->
        <span class="cart-count" v-if="totalQuantityForCurrentBusiness > 0">{{ totalQuantityForCurrentBusiness }}</span>
    </div>

    <!-- 购物车弹窗 -->
    <div class="cart-popup-overlay" v-if="showCartPopup" @click.self="closeCartPopup">
        <div class="cart-popup-content">
            <h3>购物车</h3>
            <ul class="cart-items-list">
                <li v-for="item in currentBusinessCartItems" :key="item.goodsId" class="cart-item">
                    <img :src="item.goods.goodsImg" alt="Goods Image">
                    <div class="item-details">
                        <p class="item-name">{{ item.goods.goodsName }} x {{ item.quantity }}</p>
                        <p class="item-price">&#165;{{ (item.goods.goodsPrice * item.quantity).toFixed(2) }}</p>
                    </div>
                </li>
            </ul>
            <div class="cart-summary">
                <p>总计: &#165;{{ totalCartAmount.toFixed(2) }}</p>
                <button @click="goToConfirmOrder">去结算</button>
            </div>
            <button class="close-popup-btn" @click="closeCartPopup">X</button>
        </div>
    </div>
</template>

<script setup>
import Footer from '../components/Footer.vue'
import { ref, onMounted, computed } from "vue"
import { get, post } from '@/api';
import { useRouter, useRoute } from "vue-router"
import { Plus, Minus, ShoppingCart } from '@element-plus/icons-vue'; // 引入 ShoppingCart 图标
import { ElMessage } from 'element-plus';
import { setSession } from '@/utils/storage'; // 引入 setSession

const router = useRouter();
const route = useRoute();
const business = ref([]);

// 获得商家编号
let businessId = route.query.businessId;

// 用户登录信息
const account = JSON.parse(sessionStorage.getItem("account"));

// 页面显示商品列表对象
const goods = ref([]);
const cartList = ref(JSON.parse(sessionStorage.getItem('cartList')) || []);

// 购物车弹窗显示状态
const showCartPopup = ref(false);

// 加载商家详情，并在成功后加载商品
const loadBusinessInfo = async () => {
    try {
        const res = await get(`/business/info/${businessId}`);
        if (res.data && res.data.code === 20000) {
            business.value = res.data.resultdata;
            // 商家信息加载成功后，再加载商品列表
            loadGoodsByBusinessId();
        } else {
            ElMessage.error(res.data?.message || '加载商家信息失败');
        }
    } catch (error) {
        console.error('加载商家信息请求异常:', error);
        ElMessage.error('加载商家信息异常');
    }
};

// 根据商家编号加载商品列表
function loadGoodsByBusinessId() {
    let url = `/business/listByBusinessId/${businessId}`;
    get(url).then(res => {
        if (res.data && res.data.code === 20000) {
            let tempArray = res.data.resultdata;
            if (Array.isArray(tempArray)) {
                // 从 sessionStorage 中获取 cartList 数据
                const cartList = JSON.parse(sessionStorage.getItem('cartList')) || [];
                // 找到当前商家的购物车项
                const currentBusinessCart = cartList.find(b => b.businessId === business.value.businessId);

                for (let i = 0; i < tempArray.length; i++) {
                    tempArray[i].goodsPrice = parseFloat(tempArray[i].goodsPrice);
                    let quantity = 0;
                    if (currentBusinessCart) {
                        // 从当前商家的购物车项中查找该商品
                        const cartItem = currentBusinessCart.items && currentBusinessCart.items.find(c => c.goods.goodsId === tempArray[i].goodsId);
                        // 如果购物车有，则更新为购物车数量，否则为0
                        quantity = cartItem ? cartItem.quantity : 0;
                    }
                    tempArray[i].quantity = quantity;
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

    // 查找或创建当前商家的购物车对象
    let currentBusinessCart = cartList.value.find(b => b.businessId === business.value.businessId);
    if (!currentBusinessCart) {
        currentBusinessCart = {
            businessId: business.value.businessId,
            items: []
        };
        cartList.value.push(currentBusinessCart);
    }

    // 在当前商家的购物车中查找商品
    let goodsItemInCart = currentBusinessCart.items.find(cartItem => cartItem.goods.goodsId === item.goodsId);
    const originalQuantity = goodsItemInCart ? goodsItemInCart.quantity : 0;

    // 乐观更新本地数量
    if (goodsItemInCart) {
        goodsItemInCart.quantity++;
    } else {
        // 如果购物车中没有该商品，则创建一个新的商品项并添加到当前商家的购物车中
        goodsItemInCart = {
            goods: { ...item, goodsPrice: parseFloat(item.goodsPrice) }, // 确保 goodsPrice 是数字，并保留完整的 goods 对象
            quantity: 1
        };
        currentBusinessCart.items.push(goodsItemInCart);
    }

    // 同步更新 goods 列表中的商品数量，确保 UI 反映最新状态
    const goodsItemInList = goods.value.find(g => g.goodsId === item.goodsId);
    if (goodsItemInList) {
        goodsItemInList.quantity = goodsItemInCart.quantity; // 同步更新页面显示数量
    }

    let apiEndpoint = '';
    let requestBody = {
        goodsId: item.goodsId,
        businessId: business.value.businessId,
        accountId: account.accountId,
        quantity: goodsItemInCart.quantity,
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
            // 确保 cartList.value 已经包含了最新的数据，然后同步到 sessionStorage
            sessionStorage.setItem('cartList', JSON.stringify(cartList.value));
        } else {
            ElMessage.error(res.data ? res.data.message : '添加购物车失败');
            // 后端操作失败，回滚本地数量变化
            if (goodsItemInCart.quantity > 0) { // 避免负数
                goodsItemInCart.quantity--;
            } else {
                // 如果是第一次添加失败，则从购物车中移除
                const index = cartList.value.indexOf(goodsItemInCart);
                if (index > -1) {
                    currentBusinessCart.items.splice(index, 1);
                }
                // 如果当前商家购物车为空，则从 cartList 中移除该商家购物车
                if (currentBusinessCart.items.length === 0) {
                    const businessCartIndex = cartList.value.indexOf(currentBusinessCart);
                    if (businessCartIndex > -1) {
                        cartList.value.splice(businessCartIndex, 1);
                    }
                }
            }
            if (goodsItemInList) {
                goodsItemInList.quantity = originalQuantity;
            }
        }
    } catch (error) {
        console.error('添加购物车请求异常:', error);
        ElMessage.error('添加购物车异常');
        // 请求本身失败，回滚本地数量变化
        if (goodsItemInCart.quantity > 0) { // 避免负数
            goodsItemInCart.quantity--;
        } else {
            const index = cartList.value.indexOf(goodsItemInCart);
            if (index > -1) {
                currentBusinessCart.items.splice(index, 1);
            }
            // 如果当前商家购物车为空，则从 cartList 中移除该商家购物车
            if (currentBusinessCart.items.length === 0) {
                const businessCartIndex = cartList.value.indexOf(currentBusinessCart);
                if (businessCartIndex > -1) {
                    cartList.value.splice(businessCartIndex, 1);
                }
            }
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

    // 查找当前商家的购物车对象
    let currentBusinessCart = cartList.value.find(b => b.businessId === business.value.businessId);

    if (!currentBusinessCart || !currentBusinessCart.items) {
        return; // 如果没有当前商家的购物车或购物车为空，则不执行任何操作
    }

    // 在当前商家的购物车中查找商品
    let goodsItemInCart = currentBusinessCart.items.find(cartItem => cartItem.goods.goodsId === item.goodsId);

    if (!goodsItemInCart || goodsItemInCart.quantity === 0) {
        return; // 如果购物车中没有该商品或数量已为0，则不执行任何操作
    }

    const originalQuantity = goodsItemInCart.quantity;

    // 乐观更新本地数量
    goodsItemInCart.quantity--;

    // 同步更新 goods 列表中的商品数量
    const goodsItemInList = goods.value.find(g => g.goodsId === item.goodsId);
    if (goodsItemInList) {
        goodsItemInList.quantity = goodsItemInCart.quantity; // 同步更新页面显示数量
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
    } else {
        // 数量大于0，更新数量，调用 /cart/update
        apiEndpoint = '/cart/update';
    }

    try {
        const res = await post(apiEndpoint, requestBody, true);

        if (res.data && res.data.code === 20000) {
            ElMessage.success('商品数量已更新');
            if (goodsItemInCart.quantity === 0) {
                // 如果数量为0，从当前商家的购物车中移除该商品
                const index = currentBusinessCart.items.indexOf(goodsItemInCart);
                if (index > -1) {
                    currentBusinessCart.items.splice(index, 1);
                }
                // 如果当前商家购物车为空，则从 cartList 中移除该商家购物车
                if (currentBusinessCart.items.length === 0) {
                    const businessCartIndex = cartList.value.indexOf(currentBusinessCart);
                    if (businessCartIndex > -1) {
                        cartList.value.splice(businessCartIndex, 1);
                    }
                }
            }
            // 确保 cartList.value 已经包含了最新的数据，然后同步到 sessionStorage
            sessionStorage.setItem('cartList', JSON.stringify(cartList.value));
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

// 计算当前商家购物车中的商品列表
const currentBusinessCartItems = computed(() => {
    if (!business.value || !business.value.businessId) {
        return [];
    }
    // 找到当前商家的购物车项，直接使用响应式变量 cartList.value
    const currentBusinessCart = cartList.value.find(b => b.businessId === business.value.businessId);
    return currentBusinessCart && currentBusinessCart.items ? currentBusinessCart.items : [];
});

// 计算当前商家购物车中商品的总数量
const totalQuantityForCurrentBusiness = computed(() => {
    return currentBusinessCartItems.value && currentBusinessCartItems.value.length > 0
        ? currentBusinessCartItems.value.reduce((total, item) => total + item.quantity, 0)
        : 0;
});

// 计算当前商家购物车中商品的总金额
const totalCartAmount = computed(() => {
    return currentBusinessCartItems.value.reduce((total, item) => total + (item.goods.goodsPrice * item.quantity), 0);
});

// 打开购物车弹窗
const openCartPopup = () => {
    showCartPopup.value = true;
};

// 关闭购物车弹窗
const closeCartPopup = () => {
    showCartPopup.value = false;
};

// 跳转到确认订单页面 (修改为与UserCart.vue结算逻辑一致)
const goToConfirmOrder = () => {
    if (totalQuantityForCurrentBusiness.value === 0) {
        ElMessage.warning('购物车为空，请先添加商品！');
        return;
    }
    if (!account || !account.accountId) {
        ElMessage.error('请先登录');
        router.push('/login');
        return;
    }

    // 准备订单详情，直接使用完整的 item 对象，其中包含了 goods 对象
    const orderDetails = currentBusinessCartItems.value.map(item => item);

    // 准备商家信息
    const businessInfo = {
        businessId: business.value.businessId,
        businessName: business.value.businessName,
        businessAddress: business.value.businessAddress,
        starPrice: business.value.starPrice,
        deliveryPrice: business.value.deliveryPrice
    };

    // 保存到 sessionStorage
    setSession('orderDetails', orderDetails);
    setSession('businessInfo', businessInfo);
    setSession('cartList', cartList.value); // 确保将所有商家的购物车数据保存到 cartList

    // 跳转到订单确认页面
    router.push('/orderConfirm');
    closeCartPopup(); // 跳转后关闭弹窗
};

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
    top: 30px;
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

/* 购物车按钮样式 */
.cart-button {
    position: fixed;
    bottom: 20px;
    right: 20px;
    background-color: #FFFACD; /* 淡黄色 */
    color: white;
    width: 50px; /* 圆形按钮宽度 */
    height: 50px; /* 圆形按钮高度 */
    border-radius: 50%; /* 使其成为圆形 */
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    z-index: 100;
    /* font-size: 24px; 移除，因为现在是图片 */
}

.cart-button .cart-count {
    position: absolute;
    top: -5px;
    right: -5px;
    background-color: #ff4d4f; /* 红色提示 */
    color: white;
    font-size: 12px;
    padding: 2px 6px;
    border-radius: 10px;
    min-width: 20px;
    text-align: center;
    line-height: 1;
}

.cart-button .cart-icon { /* 新增图片样式 */
    width: 30px; /* 调整图片大小 */
    height: 30px;
    object-fit: contain;
}

/* 购物车弹窗覆盖层 */
.cart-popup-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

/* 购物车弹窗内容 */
.cart-popup-content {
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    width: 90%;
    max-width: 400px;
    max-height: 80vh; /* 限制最大高度 */
    display: flex;
    flex-direction: column;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    position: relative;
}

.cart-popup-content h3 {
    text-align: center;
    margin-top: 0;
    margin-bottom: 15px;
    color: #333;
    font-size: 20px;
}

.cart-items-list {
    list-style: none;
    padding: 0;
    margin: 0;
    flex-grow: 1; /* 允许列表内容区域增长 */
    overflow-y: auto; /* 列表内容溢出时显示滚动条 */
    margin-bottom: 15px;
}

.cart-item {
    display: flex;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px dashed #eee;
}

.cart-item:last-child {
    border-bottom: none;
}

.cart-item img {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
    margin-right: 10px;
}

.cart-item .item-details {
    flex-grow: 1;
}

.cart-item .item-name {
    font-size: 16px;
    color: #333;
    margin: 0 0 5px;
}

.cart-item .item-price {
    font-size: 14px;
    color: #ff5722;
    font-weight: bold;
    margin: 0;
}

.cart-summary {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 15px;
    border-top: 1px solid #eee;
    margin-top: auto; /* 将总结部分推到底部 */
}

.cart-summary p {
    font-size: 18px;
    color: #333;
    font-weight: bold;
    margin: 0;
}

.cart-summary button {
    background-color: #409EFF;
    color: white;
    border: none;
    padding: 8px 15px;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
}

.close-popup-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    background: none;
    border: none;
    font-size: 24px; /* 调整图标大小 */
    color: #999;
    cursor: pointer;
    outline: none;
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