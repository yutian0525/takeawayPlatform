<template>
    <div class="wrapper">
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>编辑收货地址</p>
        </header>

        <div class="form-box">
            <div class="form-group">
                <div class="title">姓名</div>
                <input type="text" v-model="address.contactName" placeholder="收货人姓名">
                <div class="static-text">收货人</div>
            </div>
            <div class="form-group">
                <div class="title">手机号</div>
                <input type="tel" v-model="address.contactTel" placeholder="收货人手机号">
            </div>
            <div class="form-group">
                <div class="title">收货地址</div>
                <input type="text" v-model="address.address" placeholder="收货地址">
                <i class="fa fa-angle-right"></i>
            </div>
            <div class="form-group">
                <div class="title">性别</div>
                <div class="radio-box">
                    <input type="radio" id="male" value="1" v-model="address.contactSexStr">
                    <label for="male">男</label>
                    <input type="radio" id="female" value="2" v-model="address.contactSexStr">
                    <label for="female">女</label>
                </div>
            </div>
        </div>

        <button class="save-btn" @click="saveAddress">保存地址</button>

        <Footer></Footer>
    </div>
</template>

<style scoped>
.wrapper {
    width: 100%;
    min-height: 100vh;
    background-color: #f9f9f9;
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
    color: #fff;
}

header p {
    flex: 1;
    text-align: center;
    font-size: 18px;
    color: #fff;
    margin: 0;
}

.form-box {
    margin-top: 54px;
    background-color: #fff;
    border-top: solid 1px #ddd;
    border-bottom: solid 1px #ddd;
}

.form-group {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    border-bottom: solid 1px #ddd;
}

.form-group .title {
    width: 80px;
    font-size: 14px;
    color: #666;
}

.form-group input {
    flex: 1;
    border: none;
    outline: none;
    font-size: 14px;
    color: #999;
}

.form-group .static-text {
    margin-left: 10px;
    font-size: 14px;
    color: #999;
}

.form-group i {
    font-size: 20px;
    color: #999;
}

.form-group .radio-box {
    display: flex;
    align-items: center;
}

.form-group .radio-box input {
    width: 16px;
    height: 16px;
    border: solid 1px #999;
    border-radius: 50%;
    margin-right: 5px;
    appearance: none;
    -webkit-appearance: none;
    outline: none;
    position: relative;
}

.form-group .radio-box input:checked {
    background-color: #ffc107;
    border: solid 1px #ffc107;
}

.form-group .radio-box input:checked::before {
    content: '';
    position: absolute;
    top: 3px;
    left: 3px;
    width: 8px;
    height: 8px;
    background-color: #fff;
    border-radius: 50%;
}

.form-group .radio-box label {
    font-size: 14px;
    color: #999;
    margin-right: 20px;
}

.save-btn {
    width: calc(100% - 30px);
    height: 40px;
    margin: 15px auto;
    background-color: #ffc107;
    border: none;
    outline: none;
    border-radius: 4px;
    font-size: 16px;
    color: #fff;
    display: block;
}
</style>
<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { post, get } from '@/api/index';
import { getSession } from '@/utils/storage';
const router = useRouter();
const route = useRoute();
const account = ref(getSession('account'));
const address = ref({
    id: null,
    contactName: '',
    contactTel: '',
    address: '',
    contactSexStr: '1', // 默认男性
    accountId: ''
});
let n = ref(1);
onMounted(async () => {
    const a = getSession("editAddress")
    console.log("duqu:", a)
    if (a) {
        // // 编辑模式，从后端获取地址详情
        // try {
        //     const res = await get(`/address/detail/${addressId}`);
        //     if (res.data.code === 20000) {
        //         address.value = res.data.data;
        //         // 将 contactSex 转换为 contactSexStr
        //         address.value.contactSexStr = address.value.contactSex === 1 ? '1' : '2';
        //     } else {
        //         ElMessage.error(res.data.message || '获取地址详情失败');
        //         router.back();
        //     }
        // } catch (error) {
        //     console.error('获取地址详情失败:', error);
        //     ElMessage.error('获取地址详情失败');
        //     router.back();
        // }

        address.value = a;
        n.value = 1
    } else {
        // 新增模式，清空表单
        address.value = {
            id: null,
            contactName: '',
            contactTel: '',
            address: '',
            contactSexStr: '1', // 默认男性
        };
        n.value = 0
        console.log("4211",address.value)
    }
});

const saveAddress = async () => {
    try {
        // 根据 contactSexStr 设置 contactSex，因为后端需要 contactSex 字段
        address.value.contactSex = address.value.contactSexStr === '1' ? 1 : 2;
        address.value.accountId = account.value.accountId
        address.value.id = address.value.daId
        console.log("311",address.value)
        let res;
        if (n.value) {
            // 更新地址
            res = await post('/address/update', address.value, true);
        } else {
            // 新增地址
            res = await post('/address/add', address.value, true);
        }

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
</script>