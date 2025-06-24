<template>
    <div class="wrapper">
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>编辑收货地址</p>
        </header>

        <form @submit.prevent="saveAddress">
            <div class="form-group">
                <label for="name">姓名</label>
                <input type="text" id="name" v-model="address.contactName" placeholder="请输入姓名">
            </div>
            <div class="form-group">
                <label for="tel">手机号</label>
                <input type="tel" id="tel" v-model="address.contactTel" placeholder="请输入手机号">
            </div>
            <div class="form-group">
                <label for="address">收货地址</label>
                <input type="text" id="address" v-model="address.address" placeholder="请选择收货地址">
            </div>
            <div class="form-group">
                <label>性别</label>
                <div>
                    <input type="radio" id="male" value="1" v-model="address.contactSex">
                    <label for="male">男</label>
                    <input type="radio" id="female" value="0" v-model="address.contactSex">
                    <label for="female">女</label>
                </div>
            </div>
            <button type="submit" class="save-btn">保存地址</button>
        </form>

        <Footer></Footer>
    </div>
</template>

<style scoped>
.wrapper {
    width: 100%;
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 60px;
}

header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 44px;
    background-color: #ffc107;
    display: flex;
    align-items: center;
    padding: 0 15px;
    z-index: 100;
}

header i {
    font-size: 24px;
    color: #333;
}

header p {
    flex: 1;
    text-align: center;
    font-size: 18px;
    color: #333;
    margin: 0;
}

.form-group {
    margin: 15px;
}

.save-btn {
    width: 90%;
    margin: 20px auto;
    padding: 10px;
    background-color: #ffc107;
    border: none;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
}
</style>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { post } from '@/api/index';

const router = useRouter();
const address = ref(JSON.parse(sessionStorage.getItem('editAddress')));

const saveAddress = async () => {
    try {
        const res = await post('/deliveryaddress/update', address.value);
        if (res.data.code === 20000) {
            ElMessage.success('保存成功');
            router.back();
        } else {
            ElMessage.error(res.data.message || '保存失败');
        }
    } catch (error) {
        console.error('保存地址失败:', error);
        ElMessage.error('保存地址失败');
    }
};

onMounted(() => {
    if (!address.value) {
        ElMessage.error('地址信息不存在');
        router.back();
    }
});
</script>