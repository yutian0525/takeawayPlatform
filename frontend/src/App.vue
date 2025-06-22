<template>
  <nav>
    <router-link to="/">Home</router-link> |
    <router-link to="/Login">Login</router-link>
    <router-link to="/Register">Register</router-link>
  </nav>
  <router-view/>
</template>

 <style>
 /* 主组件：所有组件都使用的样式。 */
 /* CSS重置 */
 body,div,ul,ol,li,p,h1,h2,h3,h4,h5,h6,span,a,img,form{
 margin: 0;
 padding: 0;
 }
 html,body{ 
width: 100%;
 font-family: Arial,'Microsoft Yahei';
 height:100%;
 }
 ul{
 list-style: none;  /* none 设置列表不显示列表符号 */
 }
 a{ text-decoration: none; }  /* 去掉页面上所有超链接下划线 */
 </style>
 <script>
 export default  {
 data() {
 return {
 //组件页面所需要的所有的变量  属性:值,
 name:'上海海洋',
 addr:'上海市临港',
 };
 },
 methods: {
 //组件页面所需的所有方法  定义忽略function关键词
 test(){
 },
 test2(){
          
      }
  },
 };
 </script>