<template>
<div class="wrapper">
    <header>
        <p>信息修改</p>
    </header>
    <el-form ref="updateForm" :model="account" label-width="80px" class="form-box" :rules="rules" @submit.prevent>
        <el-form-item label="账户" prop="accountId">
            <el-input v-model="account.accountId" placeholder="账户不可修改" disabled></el-input>
        </el-form-item>
        <el-form-item label="密码验证" prop="password">
            <el-input v-model="account.password" type="password" show-password placeholder="请输入密码进行验证"/>
        </el-form-item>
        <el-form-item label="昵称" prop="accountName">
            <el-input v-model="account.accountName" placeholder="请输入新昵称"/>
        </el-form-item>
        <el-form-item label="性别" prop="accountSex">
            <el-radio-group v-model="account.accountSex">
                <el-radio label="1">男</el-radio>
                <el-radio label="0">女</el-radio>
            </el-radio-group>
        </el-form-item>
        <div class="button-update">
            <button @click="updateInfo()">确认修改</button>
        </div>
        <div class="button-back">
            <button @click="toProfile()">返回主页</button>
        </div>
    </el-form>
    <Footer></Footer>
</div>
</template>

<script setup>
import Footer from '@/components/Footer.vue';
import { ElMessage } from 'element-plus';
import { ref, reactive } from "vue";
import { get, post } from "@/api/index.js";
import { useRouter } from "vue-router";
import { getSessionStorage } from "@/common";

const router = useRouter();
const account = reactive({
    accountId: '',
    password: '',
    accountName: '',
    accountSex: ''
});

const updateForm = ref(null);
const rules = reactive({
    password: [
        { required: true, message: '请输入密码进行验证', trigger: 'blur' }
    ],
    accountName: [
        { required: true, message: '请输入新昵称', trigger: 'blur' }
    ],
    accountSex: [
        { required: true, message: '请选择性别', trigger: 'change' }
    ]
});

// 从SessionStorage获取用户信息
const userInfo = getSessionStorage("account");
if (userInfo) {
    account.accountId = userInfo.accountId;
    account.accountName = userInfo.accountName;
    account.accountSex = userInfo.accountSex;
}

const checkPassword = () => {
    const checkUrl = `/account/checkPassword?accountId=${account.accountId}&password=${account.password}`;
    get(checkUrl).then(res => {
        if (res.data.code === 20000) {
            updateForm.value.validate("accountName", (valid) => {
                if (valid) {
                    updateInfo();
                }
            });
        } else {
            ElMessage.error(res.data.message);
        }
    });
};

const updateInfo = () => {
    updateForm.value.validate((valid, fields) => {
        if (valid) {
            const updateUrl = `/account/update`;
            post(updateUrl, account).then(res => {
                if (res.data.code === 20000) {
                    ElMessage.success('信息修改成功');
                    router.push('/login');
                } else {
                    ElMessage.error(res.data.message);
                }
            });
        } else {
            console.log('error submit!', fields);
            return false;
        }
    });
};

const toLogin = () => {
    router.push('/login');
};
</script>

<style scoped>
.wrapper header {
    width: 100%;
    height: 12vw;
    background: linear-gradient(to right, #fff1eb, #ace0f9);
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
.wrapper .form-box {
    width: 100%;
    margin-top: 20vw;
    padding: 5vw;
    box-sizing: border-box;
}
.wrapper .form-box .el-input {
    width: 90%;
}
.wrapper .button-update,
.wrapper .button-login {
    width: 100%;
    box-sizing: border-box;
    padding: 4vw;
}
.wrapper .button-update button,
.wrapper .button-login button {
    width: 100%;
    height: 10vw;
    font-size: 3.8vw;
    border-radius: 1.5vw;
    color: #efefef;
    outline: none;
    letter-spacing: 2vw;
}
.wrapper .button-update button {
    background-color: #3cba92;
    border: none;
}
.wrapper .button-login button {
    background-color: #fdfcfb;
    border: 0.3vw solid #e2d1c3;
    color: #5f72bd;
}
</style>