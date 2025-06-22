<template>
<div class="wrapper">
    <header>
        <p>用 户 注 册</p>
    </header>
    <el-form ref="registerForm" :model="account"label-width="80px"class="form-box":rules="rules" @submit.prevent>
        <el-form-item label="账户" prop="accountId">
            <el-input v-model="account.accountId"placeholder="请输入注册账户(手机号)" @blur = CheckAccountId()></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
            <el-input v-model="account.password"type="password" show-password placeholder="请输入注册密码"/>
        </el-form-item>
        <el-form-item label="确认密码" prop="password2">
        <el-input v-model="account.password2" type="password" show-password placeholder="请确认注册密码"/>
        </el-form-item>
        <el-form-item label="昵称" prop="accountName">
            <el-input v-model="account.accountName"placeholder="请输入注册昵称"/>
        </el-form-item>
        <el-form-item label="性别" prop="accountSex">
            <el-radio-group v-model="account.accountsex">
                <el-radio label="1">男</el-radio>
                <el-radio label="0">女</el-radio>
            </el-radio-group>
        </el-form-item>
    <div class="button-register">
        <button @click="register()">注册</button>
    </div>
    <div class="button-login">
        <button @click="toLogin()">去登录</button>
    </div>
</el-form>
<Footer></Footer>
</div>
</template>
 <script setup>
 import Footer from '@/components/Footer.vue';
 import { ElMessage } from 'element-plus';
 import {ref,reactive} from "vue"
 import {get,post} from "@/api/index.js"
 import {useRouter} from "vue-router"
    //ElemetUI-Plus验证规则
    const router = useRouter();
    const password2 = ref();
    //定义变量 登录用户
    const account = reactive({
        accountId:'',  //登录手机号
        password:'',   //密码
        //password2:'',
        accountSex:'0',
        accountName:''
})
    const rules = reactive({
    accountId:[
        { required: true, message: '请输入登录账户', trigger: 'blur' }
    ],
    password:[
        { required: true, message: '请输入密码', trigger: 'blur' }
    ],
    password2:[
        { required: true, message: '请确认密码', trigger: 'blur' },
        {
            validator:(rule,value,callback)=>{
                if(value !== account.password){
                    callback(new Error("两次密码不一致"));
                }
                else{
                    callback();
                }
            }
        }
    ],
    accountName:[
        {required:true, message:'请输入昵称', trigger:'blur'}
    ],
    accountsex:[
        {required:true, message:'请选择性别', trigger:'blur'}
    ]
    })

//获得注册表单对象
const registerForm =ref(null);
const CheckAccountId = () =>{
    if(account.accountId != ''){
        let url = `/account/check/${account.accountId}`
        get(url).then(res => {
            if(res.data.code == 20005){
                ElMessage({
                    message:res.data.message,
                    type:'error',
                })

                account.accountId = '';
            }
        })
    }
}
const register =  () =>{
 //通过loginFrom表单对象，调用该对象 validate()验证方法，如果定义验证规则通过，v是true，否则是false
 registerForm.value.validate((v,f)=>{
 if(v){
    let url = "/account/register";
    post(url,account,true).then(res=>{
    if(res.data.code == 20000){
        ElMessage({
            message: '注册成功，请登录',
            type: 'success',
        })
        account.accountId = '';
        account.password = '';
        account.accountSex = 0;
        account.accountName = '';
        router.push('/login');
        
    }
    });
 }
 else{
    return false;
 }
 });
 }
 const toRegister = ()=>{
 router.push('/register');
 }


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
 /* APP标题部分样式 */
 /* 登录部分样式代码 */
 .wrapper .form-box{
 width:100%; margin-top:20vw;
 padding: 5vw;
 box-sizing: border-box;
 }
 .wrapper .form-box .el-input{
 width:90%;
 }
 .wrapper .button-login{
 width:100%;
 box-sizing: border-box;
 padding:4vw 4vw 0 4vw;
 }
 .wrapper .button-login button{
 width:100%;
 height:10vw;
 font-size: 3.8vw;
 background-color: #3cba92;
 border:none;
 border-radius: 1.5vw;
 color:#efefef;
 outline: none;
 letter-spacing: 2vw;
 }
 .wrapper .button-register{
 width:100%;
 box-sizing: border-box;
 padding:4vw;
 }
 .wrapper .button-register button{
 width:100%;
 height:10vw;
 font-size: 3.8vw;
 background-color: #fdfcfb;
 border:0.3vw solid #e2d1c3;
 border-radius: 1.5vw;
 color:#5f72bd;
 outline: none;
 letter-spacing: 2vw;
 }
 </style>