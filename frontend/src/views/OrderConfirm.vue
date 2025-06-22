<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>确认订单</p>
    </header>

    <!-- 配送地址部分 -->
    <div class="order-info">
      <h5>订单配送至：</h5>
      <div class="order-info-address" @click="toAddress()">
        <p>{{ deliveryAddress!=null?deliveryAddress.address:'请选择配送地址' }}</p>
         <!-- <p>上海市静安区静安寺街道常德路195号</p> -->
        <i class="el-icon-arrow-right"></i>
      </div>
      <p v-if="deliveryAddress!=null">  
          {{deliveryAddress.contactName }}
          {{deliveryAddress.contactSex | fmtSex}} 
          <em>{{deliveryAddress.contactTel}}</em>
      </p> 
    </div>


    <h3>{{ business.businessName }}</h3>
    <!-- 订单明细部分 -->
    <ul class="order-detailed">
      <li v-for="item in cart" :key="item.goodsId">
        <div class="order-detailed-left">
          <img :src="item.goodsImg">
          <p>{{ item.goodsName }} x {{ item.quantity }}</p>
        </div>
        <p>&#165; {{ item.goodsPrice * item.quantity }}</p>
      </li>
    </ul>
    <div class="order-deliveryfee">
      <p>配送费</p>
      <p>&#165; {{ business.deliveryPrice }}</p>
    </div>

    <!-- 合计部分 -->
    <div class="total">
      <div class="total-left">总价：
        &#165; {{ totalPrice }}
      </div>
      <div class="total-right" @click="toPayment">
        去支付
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from 'element-plus';
import { get, post } from '@/api/index';
import { getSession, setSession } from '@/utils/storage';

const router = useRouter();
const account = ref(getSession('account'));
const orderDetails = ref(getSession('orderDetails') || []);
const businessInfo = ref(getSession('businessInfo') || {});
const totalAmount = ref(getSession('totalAmount') || '0.00');
const deliveryAddress = ref(null);
const business = ref({});
const cart = ref([]);

// 计算总价（包含配送费）
const finalTotal = computed(() => {
    const subtotal = parseFloat(totalAmount.value);
    const delivery = parseFloat(businessInfo.value.deliveryPrice || 0);
    return (subtotal + delivery).toFixed(2);
});

onMounted(async () => {
    if (!account.value) {
        ElMessage.error('请先登录');
        router.push('/login');
        return;
    }

    if (!orderDetails.value.length) {
        ElMessage.error('订单数据不存在');
        router.push('/cart');
        console.log(orderDetails.value);
        return;
    }

    // 获取默认收货地址
    try {
        const addressRes = await get(`/address/default/${account.value.accountId}`);
        if (addressRes.data.code === 20000) {
            deliveryAddress.value = addressRes.data.resultdata;
            // 保存地址信息到 sessionStorage
            setSession('deliveryAddress', deliveryAddress.value);
        }
    } catch (error) {
        console.error('获取默认地址失败:', error);
    }
});


// 计算总价（包含配送费）
const totalPrice = computed(() => {
    let total = 0;
    for (const item of cart.value) {
        total += item.goodsPrice * item.quantity;
    }
    total += business.value.deliveryPrice || 0;
    return total.toFixed(2);
});

// 跳转到支付页面
const toPayment = async () => {
    if (!deliveryAddress.value) {
        ElMessage.warning('请选择配送地址');
        return;
    }

    try {
        // 创建订单
        const orderData = {
            accountId: account.accountId,
            businessId: business.value.businessId,
            orderTotal: totalPrice.value,
            deliveryAddressId: deliveryAddress.value.id,
            orderItems: cart.value.map(item => ({
                goodsId: item.goodsId,
                quantity: item.quantity,
                itemPrice: item.goodsPrice
            }))
        };

        // 这里需要后端提供创建订单的接口
        const res = await post('/order/create', orderData);
        
        if (res.data.code === 20000) {
            // 保存订单ID，用于支付页面使用
            sessionStorage.setItem('orderId', res.data.resultdata);
            router.push('/payment');
        } else {
            ElMessage.error(res.data.message || '创建订单失败');
        }
    } catch (error) {
        console.error('创建订单失败:', error);
        ElMessage.error('创建订单失败，请重试');
    }
};

// 跳转到地址选择页面
const toAddress = () => {
    router.push('/address');
};

onMounted(async () => {
    if (!account.value) {
        ElMessage.error('请先登录');
        router.push('/login');
        return;
    }

    // 从 sessionStorage 获取购物车和商家信息
    const orderDetails = getSession('orderDetails');
    const businessInfo = getSession('businessInfo');
    
    console.log('获取到的订单数据:', orderDetails); // 调试用
    console.log('获取到的商家信息:', businessInfo); // 调试用

    if (!orderDetails || !businessInfo) {
        ElMessage.error('订单数据不存在');
        router.push('/cart');
        return;
    }

    // 设置购物车数据
    cart.value = orderDetails;
    business.value = businessInfo;

    // 获取默认收货地址
    try {
        const addressRes = await get(`/address/default/${account.value.accountId}`);
        if (addressRes.data.code === 20000) {
            deliveryAddress.value = addressRes.data.resultdata;
        }
    } catch (error) {
        console.error('获取默认地址失败:', error);
    }
});
</script>

<style scoped>
  /****************** 总容器 ******************/
  .wrapper {
    width: 100%;
    height: 100%;
    padding: 2vw;
    box-sizing: border-box;

    padding-bottom:16vw;
  }

  /****************** header部分 ******************/
  .wrapper header {
    width: 100%;
    height: 12vw;
    background:linear-gradient(to right,#fff1eb,#ace0f9 ); color:#596164;
    font-size: 4.8vw;
    letter-spacing:2vw;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;

    display: flex;
    justify-content: center;
    align-items: center;
  }

  /****************** 订单信息部分 ******************/
  .wrapper .order-info {
    /*注意这里，不设置高，靠内容撑开。因为地址有可能折行*/
    width: 95%;
    margin:16vw auto 10vw auto;
    
    background:linear-gradient(to right bottom,#d8edfd,#f6faff);
    box-sizing: border-box;
    color: #436283;
    border-radius:2vw;
    padding: 4vw;
    margin-bottom: 5vw;
    padding: 4vw;
    box-sizing: border-box;
    box-shadow: 0.5vw 0.5vw 3vw rgba(0, 0, 0, 0.1);
  }

  .wrapper .order-info h5 {
    font-size: 3.6vw;
    font-weight: 600;
    color:#001747;
    margin-bottom:4vw;
  }

  .wrapper .order-info .order-info-address {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;

    font-weight: 500;
    user-select: none;
    cursor: pointer;
    margin: 1vw 0;
    
  }

  .wrapper .order-info .order-info-address p {
    width: 90%;
    font-size: 4vw;
    text-align: left;
  }

  .wrapper .order-info .order-info-address i {
    font-size: 5vw;
  }

  .wrapper .order-info p {
    font-size: 3.5vw;
    margin-top: 2vw;
    text-align: right;
  }

  .wrapper h3 {
    box-sizing: border-box;
    padding: 3vw;
    font-size: 4vw;
    color: #66717d;
    border-bottom: solid 1px #ddd;
  }

  /****************** 订单明细部分 ******************/
  .wrapper .order-detailed {
    width: 100%;
  }

  .wrapper .order-detailed li {
    width: 100%;
    height: 16vw;
    box-sizing: border-box;
    padding: 10vw 3vw;
    color: #666;

    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .wrapper .order-detailed li .order-detailed-left {
    display: flex;
    align-items: center;
  }

  .wrapper .order-detailed li .order-detailed-left img {
    width: 16vw;
    height: 16vw;
    border-radius: 2vw;
  }

  .wrapper .order-detailed li .order-detailed-left p {
    font-size: 3.5vw;
    margin-left: 3vw;
    color: #393939;
  }

  .wrapper .order-detailed li p {
    font-size: 3.5vw;
    color: #df5643;
    font-weight: 700;
  }

  .wrapper .order-deliveryfee {
    width: 100%;
    box-sizing: border-box;
    padding: 2vw 3vw;
    color: #466457;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 3.8vw;
    background-color:#e5e7da;
    border-radius: 0.5vw;
    margin-top: 4vw;
  }

  /****************** 订单合计部分 ******************/
  .wrapper .total {
    width: 100%;
    height: 14vw;

    position: fixed;
    left: 0;
    bottom: 0;

    display: flex;
  }

  .wrapper .total .total-left {
    flex: 2;
    background-color: #66717d;
    color: #fff;
    font-size: 4.5vw;
    font-weight: 700;
    user-select: none;

    display: flex;
    justify-content: center;
    align-items: center;
  }

  .wrapper .total .total-right {
    flex: 1;
    background-color: #ff812c;
    color: #fff;
    font-size: 4.5vw;
    font-weight: 700;
    user-select: none;
    cursor: pointer;

    display: flex;
    justify-content: center;
    align-items: center;
  }
</style>
