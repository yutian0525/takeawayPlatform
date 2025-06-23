<template>
    <div class="wrapper">
        <header>
        <p>全 部 分 类</p>
        </header>
        <div class="category">
            <li v-for="category in categoryList" :key="category.categoryId" @click="toBusinessList(category.categoryId)">
                <img :src="category.categoryCover" />
                <p>{{ category.categoryName }}</p>
            </li>
        </div>
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
const init = ()=>{
    loadCategory();
}
init();
</script>
<style>
    /*商品分类*/
    .wrapper .category{
        width:100%; height: 44vw; display: flex; flex-wrap: wrap; justify-content: space-around;
        align-content:center; padding:4.4vw; box-sizing: border-box;margin-top: 30px;
    }
    .wrapper .category li{
        width:18vw; height:18vw; display: flex; flex-direction: column; justify-content: center; align-items: center;
        user-select: none; cursor: pointer;
    }
    .wrapper .category li img{
        width:13vw; height:11.3vw;
    }
    .wrapper .category li p{
        font-size: 3.2vw; color:#79859E;
    }

</style>