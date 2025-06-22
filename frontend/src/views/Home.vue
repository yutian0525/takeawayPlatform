<template>
  <div class="wrapper">
        <!-- 头部分 -->
        <div class="header"> 
            <div class="location-text">
            <i class="location_icon"></i>
            <i v-if="isLogin">{{ account.accountName }}</i>
            <i v-else> 上海海洋大学</i>
        </div>
        <!-- 用户登录之后显示头像 -->
        <div class="location-account" v-if="isLogin">
            <img :src="account.accountImg" alt="用户头像" />
        </div>
        </div>
        <!-- 搜索栏 -->
        <div class="search">
            <div class="search-top" ref="fixedBox">
                <div class="search-box">
                    <i class="search_icon" aria-hidden="true" />
                    <input type="text" placeholder="搜索商家名称、商品名称" />
                </div>
            </div>
        </div>
        
        <!-- 商品的分类信息 -->
        <ul class="category-ul">
            <li v-for="category in categoryList" :key="category.categoryId" @click="toBusinessList(category.categoryId)">
            <img :src="category.categoryCover" />
            <p>{{ category.categoryName }}</p>
            </li>
        </ul>

        <!-- 横幅广告 -->
        <div class="banner">
            <h3>上海海洋大学临港校区</h3>

            <a>立即下单 &gt;</a>
        </div>

        <!-- 超级会员 -->
        <div class="supermember">
            <div class="left">
                <img src="../assets/super_member.png" />
                <h3>超级会员</h3>
                <p>&#8226; 每月享超级权益</p>
            </div>
            <div class="right">
                立即开通 &gt;
            </div>
        </div>

        <!--推荐商家-->
        <div class="show-business">
            <div class="show-business-line"></div>
            <p>推荐商家</p>
            <div class="show-business-line"></div>
        </div>

        <!-- 推荐方式 -->
        <ul class="showtype">
            <li>综合排序<i class="fa fa-caret-down" aria-hidden="true" /></li>
            <li>距离最近</li>
            <li>销量最高</li>
            <li>筛选<i class="fa fa-filter" aria-hidden="true" /></li>
        </ul>

        <!-- 商家列表信息 -->
        <ul class="business">
            <li v-for="business in businessList" :key="business.businessId">
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
    <!-- 步骤3：使用组件 -->
    <Footer></Footer>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Footer from '@/components/Footer.vue';
import {get,post} from '@/api/index'
import router from '@/router';
import { getSessionStorage } from "@/common.js";

// 用户登录信息
const account = JSON.parse(sessionStorage.getItem("account"))


// 判断是否登录
const isLogin = ref(!!account);
const categoryList = ref([]);
const businessList = ref([]);

//加载页面的商家数据
const loadBusiness = ()=>{
    get('/business/list').then(res=>{
    businessList.value = res.data.resultdata;
 });
 }
 //加载页面的商家分类
const loadCategory=()=>{
    get('/category/list').then(res=>{
    categoryList.value = res.data.resultdata;
 });
 }
 //根据商家分类编号 跳转至商家列表页面
const toBusinessList = (id) =>{
    //需要使用路由跳转时 传递参数
    router.push({path:'/businessList',query:{categoryId:id}})
}
 //页面初始化的方法
const init = ()=>{
    loadCategory();
    loadBusiness();
}
init();
</script>


<style scoped>
.wrapper{ width:100%; height:100%; }
    /*头部*/
    .wrapper .header{ width:100%; height:14vw; background:linear-gradient(to right,#fff1eb,#ace0f9 );
        display: flex; align-items: center; justify-content: space-between;
        
    }
    .wrapper .header .location-text{
        font-size:4vw; font-weight:600; color: #596164; margin:0 1vw 0 3vw;
        display: flex;
        justify-content: left;
    }

    .wrapper .header .location-text .location_icon{
        background-image: url(../assets/location.png);
        width:6.6vw;
        height:6.6vw;
        display: block;
        background-size: cover;
    }

    .wrapper .header .location-text .fa-angle-down{ margin-left:1.5vw; }
    
    .wrapper .header .location-account img{
        width:9vw; height:9vw; border-radius:5vw;
        margin:2vw 3vw 0 0;
        border:0.5vw solid #fff;
    }
    
    
    /*搜索栏*/
    .wrapper .search{ width:100%; height:15vw; margin-top:-2vw;
        position: relative; z-index:1000;
    }
    .wrapper .search .search-top{
        width:100%; height: 15vw; background:linear-gradient(to right,#fff1eb,#ace0f9 );
        display: flex; justify-content: center; align-items: center;
    }
    .wrapper .search .search-box{
        width:90%; height:9vw; background-color: #fff; border-radius:4vw;
        border:0.4vw solid #a1c4fd;
        display: flex; justify-content: center; align-items: center;
        font-size: 3.5vw; color:#79859E;  padding:0 10vw; box-sizing: border-box;
    }    

    .wrapper .search .search-box .search_icon{
        background-image: url(../assets/search.png);
        width:6.6vw;
        height:6.6vw;
        display: block;
        background-size: cover;
    }

    .wrapper .search .search-box input{
        width:100%; height:4.5vw; line-height: 4.5vw; font-size:4vw; border: none; outline: none;
        margin-left:2vw; color:#79859E;
    }


    /*商品分类*/
    .wrapper .category-ul{
        width:100%; height: 44vw; display: flex; flex-wrap: wrap; justify-content: space-around;
        align-content:center; padding:4.4vw; box-sizing: border-box;
    }
    .wrapper .category-ul li{
        width:18vw; height:18vw; display: flex; flex-direction: column; justify-content: center; align-items: center;
        user-select: none; cursor: pointer;
    }
    .wrapper .category-ul li img{
        width:13vw; height:11.3vw;
    }
    .wrapper .category-ul li p{
        font-size: 3.2vw; color:#79859E;
    }

    /*横幅广告*/
    .wrapper .banner{
        width:95%; margin: 0 auto; height:29vw;
        background-image: url(../assets/index_banner.png);
        background-repeat: no-repeat; background-size: cover; box-sizing: border-box; padding:3vw 6vw;
    }
    .wrapper .banner h3{ font-size:4.2vw; margin-bottom:1.2vw;}
    .wrapper .banner p{font-size: 3.4vw; color:#79859E; margin-bottom:2.4vw;}
    .wrapper .banner a{font-size:3.6vw; color:#FF3D8D; font-weight: 600; cursor: pointer;  }

    /*超级会员*/
    .wrapper .supermember{
        width:95%; margin: 0 auto; height:11.5vw; background: linear-gradient(to right bottom,#e2d1c3,#fdfcfb);
        margin-top:1.3vw; border-radius:1vw; color:#644f1b;
        display: flex; justify-content: space-between; align-items: center;
    }
    .wrapper .supermember .left{ display: flex; align-items: center; margin-left:4vw; user-select: none; }
    .wrapper .supermember .left img{ width:6vw; height:6vw; margin-right:2vw; }
    .wrapper .supermember .left h3{ font-size: 4vw; margin-right:2vw; }
    .wrapper .supermember .left p{ font-size:3vw;}
    .wrapper .supermember .right{ font-size: 3.4vw; margin-right: 4vw; }

    /*推荐商家*/
    .wrapper .show-business{
        width:100%; height:14vw; display: flex; justify-content: center; align-items: center;
    }
    .wrapper .show-business .show-business-line{
        width:8vw; height:0.3vw; background-color: #e2d1c3;
    }
    .wrapper .show-business p{
        font-size: 4vw; margin: 0 4vw; color:#79859E;
    }
    

    /*推荐方式*/
    .wrapper .showtype{
        width:100%; height:5vw; display: flex; justify-content: space-around; align-items: center;
        margin-bottom: 5vw; color:#79859E
    }
    .wrapper .showtype li{
        font-size:3.5vw; color:#79859E;
    }

    /* 商家列表 */
    .wrapper .business{
        width:100%; 
        margin-bottom:14vw;
        padding-bottom: 15vw;
        
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