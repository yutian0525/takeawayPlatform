import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { getCurDate } from './common'
import ElementPlus from 'element-plus';
import '/node_modules/element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'


console.log(getCurDate());
createApp(App).use(router).use(ElementPlus).mount('#app')
 // 全局注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    createApp(App).component(key, component)
 }