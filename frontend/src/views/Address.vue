<template>
    <div class="wrapper">
        <header>
            <i class="fa fa-angle-left" @click="router.back()"></i>
            <p>地址管理</p>
        </header>

        <ul class="addresslist">
            <template v-if="addressList.length > 0">
                <li v-for="(address, index) in addressList" :key="address.id" @click="selectAddress(address)">
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

.addresslist {
    margin-top: 54px;
    padding: 10px;
}

.address-item {
    background: #fff;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 10px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.user-info {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}

.avatar {
    width: 24px;
    height: 24px;
    background-color: #4caf50;
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 10px;
    font-size: 14px;
}

.name {
    font-size: 16px;
    font-weight: 500;
    margin-right: 10px;
}

.tel {
    color: #666;
    margin-right: 10px;
}

.gender {
    color: #666;
}

.address-info {
    display: flex;
    align-items: flex-start;
    margin-top: 8px;
}

.label {
    background-color: #ffc107;
    color: #fff;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 12px;
    margin-right: 8px;
}

.content {
    flex: 1;
    color: #333;
    font-size: 14px;
    line-height: 1.4;
}

.actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
    gap: 15px;
}

.actions .el-icon {
    font-size: 18px;
    color: #666;
    cursor: pointer;
}

.actions .edit {
    color: #2196f3;
}

.actions .delete {
    color: #f44336;
}

.add-address {
    position: fixed;
    bottom: 60px;
    left: 15px;
    right: 15px;
    height: 44px;
    background-color: #ffc107;
    border-radius: 22px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #333;
    font-size: 16px;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(255, 193, 7, 0.3);
}

.add-address i {
    margin-right: 8px;
    font-size: 20px;
}

.none {
    text-align: center;
    color: #999;
    padding: 30px 0;
}
</style>

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
        if (res.data.code === 20000) {
            addressList.value = res.data.resultdata;
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
    // 将地址信息存储到 sessionStorage
    sessionStorage.setItem('editAddress', JSON.stringify(address));
    router.push('/editAddress');
};

// 添加新地址
const addNewAddress = () => {
    sessionStorage.setItem('editAddress', "");
    router.push('/editAddress');
};

// 选择地址
const selectAddress = (address) => {
    // 如果是从确认订单页面跳转来的，选择地址后返回
    sessionStorage.setItem('selectedAddress', JSON.stringify(address));
    router.back();  
};

onMounted(() => {
    loadAddressList();
});
</script>
  