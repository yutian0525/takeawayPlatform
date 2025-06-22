<template>
    <div class="wrapper">
        <header>
        <p>商 家 列 表</p>
        </header>
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
        <Footer></Footer>
    </div>
     </template>
<script setup>
import Footer from '../components/Footer.vue'
import {ref,onMounted} from "vue"
import { get } from '@/api';
import {useRouter,useRoute} from "vue-router"
const router = useRouter();  //创建路由对象
const route = useRoute();
const businessList = ref([]);

//点击商家，获得商家编号，传递至商家详情页面
const toBusinessInfo=(id)=>{
    router.push({path:'/businessInfo',query:{businessId:id}});
}
 //请求服务器端，查询该分类下的所有商家数据
const loadBusiness=()=>{
 //获得首页跳转过来传递的分类编号categoryId参数值：
    let categoryId = route.query.categoryId;
    let url = `/business-category/listByCategoryId/${categoryId}`;
    get(url).then(res=>{
        console.log(res.data.resultdata);
        
        businessList.value = res.data.resultdata;
    })
 }
 const init = ()=>{
    loadBusiness();
 }
 onMounted(init); // 在组件挂载后立即加载数据
</script>
<style>
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
    /* 商家列表 */
    .wrapper .business{
        width:100%; 
        margin-bottom:14vw;
        padding-bottom: 15vw;
        margin-top: 10vw;
        
    }

    .wrapper .business li .business-img-quantity {
      width: 5vw;
      height: 5vw;
      background-color: red;
      color: #fff;
      font-size: 3.6vw;
      border-radius: 2.5vw;
  
      display: flex;
      justify-content: center;
      align-items: center;
  
      /*设置成绝对定位，不占文档流空间*/
      position: absolute;
      right: -1.5vw;
      top: -1.5vw;
    }

    .wrapper .business li{
        width:100%; box-sizing: border-box; padding:2.5vw; user-select: none;
        border-bottom:0.3vw solid #e2d1c3;  display: flex;
        
    }

    .wrapper .business li img{
        width:18vw; height:18vw; border-radius:2vw; position: relative ; z-index: 0;
    }

    .wrapper .business li .business-info{
        width:100%; box-sizing: border-box; padding:0 3vw;
    }

    .wrapper .business li .business-info .business-info-h{
        display: flex; justify-content: space-between; align-items: center;
    }
    .wrapper .business li .business-info .business-info-h h3{
        font-size: 4vw; color:#696969;
    }
    .wrapper .business li .business-info .business-info-h .business-info-like{
        width:4.6vw; height:3.4vw; color: #FF3D8D  ; font-size:4vw; margin-right: 4vw;
        display: flex; justify-content: center; align-items: center;
    }
    
    .wrapper .business li .business-info .business-info-h .business-info-like .heart_icon{
        background-image: url(../assets/heart.png);
        width:8vw;
        height:7vw;
        display: block;
        background-size: cover;
        background-position: center;
    }

    .wrapper .business li .business-info .business-info-star{
        display: flex; justify-content: space-between; align-items: center; font-size:3.1vw;
    }
    .wrapper .business li .business-info .business-info-star .business-info-star-left{
        display: flex; align-items: center;
    }
    .wrapper .business li .business-info .business-info-star .business-info-star-left .fa-star{
        color:#fcba26; margin-right:0.5vw;
    }
    .wrapper .business li .business-info .business-info-star .business-info-star-left p{
        color:#666; margin-left:1vw;
    }
    .wrapper .business li .business-info .business-info-star .business-info-star-right{
        background-color: #016cf4; color:#f9f9f9; font-size:2.8vw; border-radius:0.4vw; padding:0 0.8vw;
    }
    .wrapper .business li .business-info .business-info-delivery{
        display: flex; justify-content: space-between; align-items: center;
        color:#666; font-size: 3.1vw; margin-bottom: 2vw;
    }
    .wrapper .business li .business-info .business-info-explain{
        display: flex; justify-content:left; align-items: center;
    }
    .wrapper .business li .business-info .business-info-explain div{
        border:0.1vw solid #e2d1c3; font-size: 3vw; color:#666; border-radius:1vw; padding:0 2vw;
        background-color: #fdfcfb;
    }
</style>