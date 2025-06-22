<template>
    <div class = "wrapper">
        <header>
            <p>购物车</p>
        </header>
        <div class="header">
            <img :src="user.avatar" alt="User Avatar" class="avatar" />
            <h2>{{ account.name }}</h2>
        </div>
       
    <Footer></Footer>
    </div>
</template>
<script setup>
    import Footer from '@/components/Footer.vue'
</script>
<style scoped>
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
  </style>