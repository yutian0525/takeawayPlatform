import { createRouter, createWebHashHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import UserProfile from '@/views/UserProfile.vue'
import BusinessList from '@/views/BusinessList.vue'
import BusinessInfo from '@/views/BusinessInfo.vue'
import OrderConfirm from '@/views/OrderConfirm.vue'
import Address from '@/views/Address.vue'
import Payment from '@/views/Payment.vue'
import UserCart from '@/views/UserCart.vue'
import UserOrders from '@/views/UserOrders.vue'
import AccountChange from '@/views/AccountChange.vue'
import Comments from '@/views/comments.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home
  },
  {
    path: '/Login',
    name: 'login',
    component: Login
  },
  {
    path: '/register',
    name: 'register',
    component: Register
  },
  {
    path: '/center',
    name: 'center',
    component: UserProfile
  },
  {
    path: '/businessList',
    name: 'businessList',
    component: BusinessList
  },
  {
    path: '/businessInfo',
    name: 'businessInfo',
    component: BusinessInfo
  },
  {
    path: '/orderConfirm',
    name: 'orderConfirm',
    component: OrderConfirm
  },
  {
    path: '/address',
    name: 'address',
    component: Address
  },
  {
    path: '/payment',
    name: 'payment',
    component: Payment
  },
  {
    path: '/cart',
    name: 'cart',
    component: UserCart
  },
  {
    path: '/orderList',
    name: 'UserOrder',
    component: UserOrders
  },
  {
    path: '/accountChange',
    name: 'AccountChange',
    component: AccountChange
  },
  {
    path: '/comments',
    name: 'comments',
    component: Comments
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
