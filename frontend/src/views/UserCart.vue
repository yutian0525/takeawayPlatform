<template>
    <div class = "wrapper">
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>购物车</p>
        </header>

        <main>
            <div class="business-cart-list">
                <div class="business-cart-item" v-for="business in cartList" :key="business.businessId">
                    <div class="business-header">
                        <input type="checkbox" v-model="business.selected" @change="toggleBusinessSelection(business)">
                        <i class="fa fa-shopping-cart"></i>
                        <h3>{{ business.businessName }} ({{ business.businessAddress }})</h3>
                    </div>
                    <ul class="item-list">
                        <li class="item-card" v-for="item in business.items" :key="item.goodsId">
                            <input type="checkbox" v-model="item.selected" @change="toggleItemSelection(business, item)">
                            <img :src="item.goods.goodsImg" alt="Goods Image">
                            <div class="item-info">
                                <p class="item-name">{{ item.goods.goodsName }}</p>
                                <p class="item-price">{{ item.goods.goodsPrice }}元</p>
                            </div>
                            <div class="item-quantity">
                                <div class="quantity-btn minus" @click="minus(business, item)">-</div>
                                <div class="quantity-count">{{ item.quantity }}</div>
                                <div class="quantity-btn plus" @click="add(business, item)">+</div>
                            </div>
                        </li>
                    </ul>
                    <div class="delivery-info">
                        <p>起送 <span class="min-delivery">{{ business.starPrice }}</span> 元</p>
                        <p>当前 <span class="current-total">{{ calculateBusinessTotal(business) }}</span> 元</p>
                    </div>
                </div>
            </div>
            <div v-if="cartList.length === 0" class="empty-cart-message">
                购物车空空如也，快去添加商品吧！
            </div>
        </main>

        <div class="total-bar">
            <div class="select-all">
                <input type="checkbox" v-model="allSelected" @change="toggleAllSelection">
                <span>全选</span>
            </div>
            <button class="clear-all-btn" @click="clearAllCartItems">清除所有</button>
            <div class="total-price">
                <span>合计</span>
                <span class="amount">¥{{ calculateOverallTotal() }}</span>
            </div>
            <button class="checkout-btn" @click="handleCheckout">结算 ></button>
        </div>
       
        <Footer></Footer>
    </div>
</template>

<script setup>
    import { ref, computed, onMounted } from 'vue';
    import { useRouter } from 'vue-router';
    import Footer from '@/components/Footer.vue';
    import { get, post } from '@/api/index';
    import { getSession, setSession } from '@/utils/storage';
    import { ElMessage } from 'element-plus';

    const router = useRouter();
    const cartList = ref([]);
    
    // 从 sessionStorage 获取用户信息
    const account = ref(getSession('account'));

    // 加载购物车数据
    const loadCartData = async () => {
        if (!account.value) {
            ElMessage.error('请先登录');
            router.push('/login');
            return;
        }

        try {
            // 1. 获取所有购物车商品
            const cartRes = await get(`/cart/listCartByAccountId/${account.value.accountId}`);
            if (cartRes.data.code !== 20000) {
                ElMessage.error('加载购物车数据失败');
                return;
            }
            let cartArray = cartRes.data.resultdata;

            // 2. 为每个购物车商品获取其对应的商品详情
            // 使用 Promise.all 并行请求所有商品的详细信息
            const goodsPromises = cartArray.map(async (cartItem) => {
                // 假设后端有一个 /goods/info/{goodsId} 接口来获取商品详情
                const goodsInfoRes = await get(`/goods/info/${cartItem.goodsId}`);
                if (goodsInfoRes.data.code === 20000) {
                    cartItem.goods = goodsInfoRes.data.resultdata; // 将完整的商品对象附加到 cartItem 上
                } else {
                    console.warn(`加载商品ID ${cartItem.goodsId} 信息失败:`, goodsInfoRes.data.message);
                    cartItem.goods = null; // 如果获取失败，将 goods 设为 null
                }
                return cartItem;
            });
            // 等待所有商品详情请求完成
            cartArray = await Promise.all(goodsPromises);

            // 过滤掉那些没有成功加载商品详情的购物车项，避免后续报错
            cartArray = cartArray.filter(item => item.goods !== null);

            // 3. 按商家ID分组商品，并收集唯一的商家ID
            const groupedCartMap = new Map();
            const businessIdsToFetch = new Set();

            cartArray.forEach(cartItem => {
                const businessId = cartItem.businessId;
                businessIdsToFetch.add(businessId);

                if (!groupedCartMap.has(businessId)) {
                    groupedCartMap.set(businessId, {
                        businessId: businessId,
                        businessName: '', // 待填充
                        businessAddress: '', // 待填充
                        starPrice: 0, // 待填充
                        deliveryPrice:0,
                        selected: true, // 默认全选
                        items: []
                    });
                }
                // cartItem 现在已经包含了 goods 对象
                groupedCartMap.get(businessId).items.push({ ...cartItem, selected: true }); // 默认商品也全选
            });

            // 4. 批量获取商家信息
            const businessPromises = Array.from(businessIdsToFetch).map(async (id) => {
                const businessInfoRes = await get(`/business/info/${id}`);
                if (businessInfoRes.data.code === 20000) {
                    const businessData = businessInfoRes.data.resultdata;
                    const businessEntry = groupedCartMap.get(id);
                    businessEntry.businessName = businessData.businessName;
                    businessEntry.businessAddress = businessData.businessAddress;
                    businessEntry.starPrice = businessData.starPrice; // 假设起送价是starPrice
                    businessEntry.deliveryPrice = businessData.deliveryPrice;
                } else {
                    ElMessage.warning(`加载商家ID ${id} 信息失败`);
                }
            });
            await Promise.all(businessPromises);

            // 5. 更新 cartList
            cartList.value = Array.from(groupedCartMap.values());

        } catch (error) {
            console.error('加载购物车数据时发生错误:', error);
            ElMessage.error('加载购物车数据异常');
        }
    };

    // 计算属性：是否所有商品都被选中
    const allSelected = computed({
        get() {
            if (cartList.value.length === 0) return false;
            return cartList.value.every(business => business.selected);
        },
        set(value) {
            cartList.value.forEach(business => {
                business.selected = value;
                business.items.forEach(item => {
                    item.selected = value;
                });
            });
        }
    });

    // 切换商家全选状态
    const toggleBusinessSelection = (business) => {
        business.items.forEach(item => {
            item.selected = business.selected;
        });
    };

    // 切换商品选中状态
    const toggleItemSelection = (business, item) => {
        business.selected = business.items.every(i => i.selected);
    };

    // 更新商品数量 (与后端交互)
    const updateCart = async (business, item, num) => {
        if (!account.value) {
            ElMessage.error('请先登录');
            router.push('/login');
            return;
        }

        const newQuantity = item.quantity + num;
        if (newQuantity <= 0) {
            // 如果数量减到0或更少，则执行删除操作
            try {
                const res = await post("/cart/remove", {
                    goodsId: item.goods.goodsId,
                    businessId: business.businessId,
                    accountId: account.value.accountId,  // 修改这里
                }, true);
                if (res.data.code === 20000) {
                    ElMessage.success('商品已从购物车移除');
                    // 从本地数据中移除该商品
                    business.items = business.items.filter(i => i.goods.goodsId !== item.goods.goodsId);
                    // 如果商家下没有商品了，也移除该商家
                    if (business.items.length === 0) {
                        cartList.value = cartList.value.filter(b => b.businessId !== business.businessId);
                    }
                } else {
                    ElMessage.error('移除商品失败');
                }
            } catch (error) {
                console.error('移除商品请求失败:', error);
                ElMessage.error('移除商品异常');
            }
        } else {
            // 否则执行更新数量操作
            try {
                const res = await post("/cart/update", {
                    goodsId: item.goods.goodsId,
                    businessId: business.businessId,
                    accountId: account.value.accountId,  // 修改这里
                    quantity: newQuantity
                }, true);
                if (res.data.code === 20000) {
                    item.quantity = newQuantity; // 更新本地数量
                } else {
                    ElMessage.error('更新商品数量失败');
                }
            } catch (error) {
                console.error('更新商品数量请求失败:', error);
                ElMessage.error('更新商品数量异常');
            }
        }
    };

    // 减少商品数量
    const minus = (business, item) => {
        updateCart(business, item, -1);
    };

    // 增加商品数量
    const add = async (business, item) => {
        if (!account) {
            ElMessage.error('请先登录');
            router.push('/login');
            return;
        }

        // 检查本地商品数量是否为0，如果为0，说明是新增操作
        // 注意：这里假设后端 /cart/add 接口会处理重复添加的情况（例如，如果已存在则更新数量）
        // 如果后端 /cart/add 只是单纯新增，且前端需要判断是否已存在，则需要先查询
        // 根据您提供的后端代码，/cart/add 已经处理了存在则更新的逻辑，所以这里直接调用 updateCart 即可
        updateCart(business, item, 1);
    };

    // 计算单个商家的总价
    const calculateBusinessTotal = (business) => {
        let total = 0;
        business.items.forEach(item => {
            if (item.selected) {
                total += item.goods.goodsPrice * item.quantity;
            }
        });
        return total.toFixed(2);
    };

    // 计算所有选中商品的总价
    const calculateOverallTotal = () => {
        let total = 0;
        cartList.value.forEach(business => {
            business.items.forEach(item => {
                if (item.selected) {
                    total += item.goods.goodsPrice * item.quantity;
                }
            });
        });
        return total.toFixed(2);
    };

    // 清除所有购物车商品
    const clearAllCartItems = async () => {
        if (cartList.value.length === 0) {
            ElMessage.info('购物车已经是空的了');
            return;
        }
        if (confirm('确定要清空购物车吗？')) {
            if (!account.value) { // 修复：使用 account.value
                ElMessage.error('请先登录');
                router.push('/login');
                return;
            }

            const removePromises = [];
            // 遍历所有商家和商品，生成删除请求
            cartList.value.forEach(business => {
                business.items.forEach(item => {
                    removePromises.push(post("/cart/remove", {
                        goodsId: item.goods.goodsId,
                        businessId: business.businessId,
                        accountId: account.value.accountId, // 修复：使用 account.value.accountId
                    }, true));
                });
            });

            try {
                await Promise.all(removePromises); // 等待所有删除请求完成
                cartList.value = []; // 清空本地数据
                ElMessage.success('购物车已清空');
            } catch (error) {
                console.error('清空购物车失败:', error);
                ElMessage.error('清空购物车异常');
            }
        }
    };

    // 页面加载时调用
    onMounted(() => {
        loadCartData();
    });
     const handleCheckout = async () => {
        if (!account.value) {
            ElMessage.error('请先登录');
            router.push('/login');
            return;
        }

        // 获取选中的商品
        const selectedItems = [];
        let selectedBusiness = null;
        
        cartList.value.forEach(business => {
            const items = business.items.filter(item => item.selected);
            if (items.length > 0) {
                if (selectedBusiness && selectedBusiness.businessId !== business.businessId) {
                    ElMessage.warning('暂时只支持购买同一商家的商品');
                    return;
                }
                selectedBusiness = business;
                selectedItems.push(...items);
            }
        });

        if (selectedItems.length === 0) {
            ElMessage.warning('请选择要结算的商品');
            return;
        }

        try {
            // 准备订单数据
            const orderDetails = selectedItems.map(item => ({
                goodsId: item.goods.goodsId,
                goodsImg: item.goods.goodsImg,
                goodsName: item.goods.goodsName,
                goodsPrice: item.goods.goodsPrice,
                quantity: item.quantity
            }));

            const businessInfo = {
                businessId: selectedBusiness.businessId,
                businessName: selectedBusiness.businessName,
                businessAddress: selectedBusiness.businessAddress,
                starPrice: selectedBusiness.starPrice, //起送费
                deliveryPrice: selectedBusiness.deliveryPrice //配送费
            };

            // 保存到 sessionStorage
            setSession('orderDetails', orderDetails);
            setSession('businessInfo', businessInfo);

            console.log('保存的订单数据:', orderDetails); // 调试用
            console.log('保存的商家信息:', businessInfo); // 调试用

            // 跳转到订单确认页面
            router.push('/orderConfirm');
        } catch (error) {
            console.error('保存订单详情失败:', error);
            ElMessage.error('创建订单失败，请重试');
        }
    };

    // 在组件挂载时从 sessionStorage 恢复数据
    onMounted(() => {
        const savedCartList = getSession('cartList');
        if (savedCartList) {
            cartList.value = savedCartList;
        }
        loadCartData();
    });
</script>

   

<style scoped>
/* 顶部标题栏 */
.wrapper header{
    width:100%; height:12vw;
    background: linear-gradient(to right,#fff1eb,#ace0f9);
    color:#596164;
    font-size: 5vw;
    position: fixed; left:0; top: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}
.wrapper header p{
    letter-spacing: 2vw;
}
.wrapper header .fa-angle-left {
    position: absolute;
    left: 4vw;
    font-size: 6vw;
    color: #596164;
}

/* 主要内容区域 */
main {
    padding: 12vw 0 28vw 0; /* 顶部留出header空间，底部留出total-bar和footer空间 */
    overflow-y: auto;
    background-color: #f5f5f5;
    min-height: calc(100vh - 12vw - 14vw); /* 减去header和footer的高度 */
}

.empty-cart-message {
    text-align: center;
    padding: 20vw 0;
    font-size: 4.5vw;
    color: #999;
}

.business-cart-list {
    padding: 2vw;
}

.business-cart-item {
    background-color: #fff;
    border-radius: 2vw;
    margin-bottom: 3vw;
    padding: 3vw;
    box-shadow: 0 0.5vw 1vw rgba(0, 0, 0, 0.05);
}

.business-header {
    display: flex;
    align-items: center;
    margin-bottom: 2vw;
    padding-bottom: 2vw;
    border-bottom: 1px solid #eee;
}
.business-header input[type="checkbox"] {
    width: 4vw;
    height: 4vw;
    margin-right: 2vw;
    accent-color: #ff9900; /* 选中颜色 */
}
.business-header .fa-shopping-cart {
    font-size: 4.5vw;
    color: #ff9900;
    margin-right: 2vw;
}
.business-header h3 {
    font-size: 4vw;
    font-weight: bold;
    color: #333;
}

.item-list {
    list-style: none;
    padding: 0;
    margin: 0;
}

.item-card {
    display: flex;
    align-items: center;
    padding: 2vw 0;
    border-bottom: 1px dashed #eee;
}
.item-card:last-child {
    border-bottom: none;
}
.item-card input[type="checkbox"] {
    width: 4vw;
    height: 4vw;
    margin-right: 2vw;
    accent-color: #ff9900;
}
.item-card img {
    width: 18vw;
    height: 18vw;
    object-fit: cover;
    border-radius: 1vw;
    margin-right: 3vw;
}
.item-info {
    flex-grow: 1;
}
.item-info .item-name {
    font-size: 4vw;
    color: #333;
    margin-bottom: 1vw;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.item-info .item-price {
    font-size: 3.8vw;
    color: #ff6600;
    font-weight: bold;
}

.item-quantity {
    display: flex;
    align-items: center;
    border: 1px solid #ccc;
    border-radius: 1vw;
    overflow: hidden;
}
.item-quantity .quantity-btn {
    width: 6vw;
    height: 6vw;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 4vw;
    font-weight: bold;
    cursor: pointer;
    background-color: #f0f0f0;
    color: #666;
    user-select: none;
}
.item-quantity .quantity-btn.minus {
    border-right: 1px solid #ccc;
}
.item-quantity .quantity-btn.plus {
    border-left: 1px solid #ccc;
}
.item-quantity .quantity-count {
    width: 8vw;
    height: 6vw;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 3.8vw;
    color: #333;
}

.delivery-info {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-top: 2vw;
    padding-top: 2vw;
    border-top: 1px solid #eee;
    font-size: 3.5vw;
    color: #666;
}
.delivery-info p {
    margin-left: 4vw;
}
.delivery-info .min-delivery,
.delivery-info .current-total {
    color: #ff6600;
    font-weight: bold;
}

/* 底部结算栏 */
.total-bar {
    width: 100%;
    height: 14vw;
    background-color: #fff;
    position: fixed;
    left: 0;
    bottom: 14vw; /* 位于Footer之上 */
    display: flex;
    align-items: center;
    padding: 0 4vw;
    box-sizing: border-box;
    border-top: 1px solid #f0f0f0;
    z-index: 999;
}
.total-bar .select-all {
    display: flex;
    align-items: center;
    margin-right: 3vw;
}
.total-bar .select-all input[type="checkbox"] {
    width: 4.5vw;
    height: 4.5vw;
    margin-right: 1.5vw;
    accent-color: #ff9900;
}
.total-bar .select-all span {
    font-size: 3.8vw;
    color: #333;
}

.total-bar .clear-all-btn {
    background-color: #f0f0f0;
    border: none;
    padding: 2vw 3vw;
    border-radius: 1vw;
    font-size: 3.5vw;
    color: #666;
    cursor: pointer;
    margin-right: auto; /* 将合计和结算按钮推到右边 */
}

.total-bar .total-price {
    display: flex;
    align-items: baseline;
    margin-right: 3vw;
}
.total-bar .total-price span {
    font-size: 3.8vw;
    color: #333;
}
.total-bar .total-price .amount {
    font-size: 5vw;
    color: #ff6600;
    font-weight: bold;
    margin-left: 1vw;
}

.total-bar .checkout-btn {
    background-color: #4CAF50; /* 绿色 */
    color: #fff;
    border: none;
    padding: 3vw 5vw;
    border-radius: 1vw;
    font-size: 4.5vw;
    font-weight: bold;
    cursor: pointer;
    white-space: nowrap;
}

/* 底部导航栏 (Footer.vue) 的样式由其组件自身提供，这里只需要确保main和total-bar不被其遮挡 */
.wrapper .footer {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 14vw; /* 假设Footer高度为14vw */
    background-color: #fff;
    border-top: 1px solid #f0f0f0;
    z-index: 1000;
}
</style>