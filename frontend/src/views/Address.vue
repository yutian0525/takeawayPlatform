<template>
    <div class="wrapper">
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>地址管理</p>
        </header>

        <ul class="addresslist">
            <template v-if="addressList.length > 0">
                <li v-for="(address, index) in addressList" :key="address.id">
                    <div class="address-item">
                        <div class="user-info">
                            <span class="avatar">{{ address.contactName.charAt(0) }}</span>
                            <span class="name">{{ address.contactName }}</span>
                            <span class="tel">{{ address.contactTel }}</span>
                            <span class="gender">{{ address.contactSex === 1 ? '先生' : '女士' }}</span>
                        </div>
                        <div class="address-info">
                            <span class="label">地址</span>
                            <span class="content">{{ address.address }}</span>
                        </div>
                        <div class="actions">
                            <el-icon class="edit" @click.stop="editAddress(address)"><Edit /></el-icon>
                            <el-icon class="delete" @click.stop="deleteAddress(address.id)"><Delete /></el-icon>
                        </div>
                    </div>
                </li>
            </template>
            <template v-else>
                <p class="none">还没有添加配送地址</p>
            </template>
        </ul>

        <div class="add-address" @click="addNewAddress">
            <i class="fa fa-map-marker"></i>
            <span>新增收货地址</span>
        </div>

        <Footer></Footer>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Edit, Delete } from '@element-plus/icons-vue'
import Footer from '../components/Footer.vue';
import { get, post } from '@/api/index';
import { getSession } from '@/utils/storage';

const router = useRouter();
const account = ref(getSession('account'));
const addressList = ref([]);

// 加载地址列表
const loadAddressList = async () => {
    if (!account.value) {
        ElMessage.error('请先登录');
        router.push('/login');
        return;
    }

    try {
        const res = await get(`/deliveryaddress/list/${account.value.accountId}`);
        console.log('接口返回的数据:', res.data); // 添加调试信息
        if (res.data.code === 20000) {
            addressList.value = res.data.resultdata || []; // 防止未定义
        } else {
            ElMessage.error('加载地址列表失败');
        }
    } catch (error) {
        console.error('加载地址列表失败:', error);
        ElMessage.error('加载地址列表异常');
    }
};

// 删除地址
const deleteAddress = async (id) => {
    try {
        await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });

        const res = await post(`/deliveryaddress/delete/${id}`);
        if (res.data.code === 20000) {
            ElMessage.success('删除成功');
            loadAddressList(); // 重新加载地址列表
        } else {
            ElMessage.error(res.data.message || '删除失败');
        }
    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除地址失败:', error);
            ElMessage.error('删除地址失败');
        }
    }
};

// 编辑地址
const editAddress = (address) => {
    sessionStorage.setItem('editAddress', JSON.stringify(address));
    router.push('/editAddress');
};

// 添加新地址
const addNewAddress = () => {
    router.push('/editAddress');
};

onMounted(() => {
    console.log('组件 mounted');
    console.log('account:', account.value);
    if (account.value) {
        console.log('开始加载地址列表...');
        loadAddressList();
    } else {
        ElMessage.error('请先登录');
        router.push('/login');
    }
});
</script>