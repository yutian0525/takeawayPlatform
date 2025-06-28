import axios from "axios"
import qs from "qs"

//创建axios对象，做一些基础设置
const axiosInstance =  axios.create({
    baseURL:'http://aaa.yutiantian.top', // 请根据您的实际后端地址修改
    timeout:50000
});

//常用get方式请求，封装成一个get(). 参数：url请求路径，get请求传递参数就是在地址栏
// http://localhost:10001/goods?id=101   传统方式传递参数
// RESTFul  http://localhost:10001/goods/101
export function get(url, params){ // get方法现在可以接收params参数
   return axiosInstance.get(url, { params: params });
}

// HTTP协议 Get和 Post区别
//常用post方式请求，封装成一个post().  post传递参数是在请求体； 请求报文(行、头、体)
export function post(url,params,isJSON){
    let headers = {};

    if(isJSON){
        headers['Content-Type'] = 'application/json';
        params = JSON.stringify(params);
    } else {
        params = qs.stringify(params);
        headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
    }
    return  axiosInstance.post(url,params,{ headers: headers }); // 将headers作为第三个参数传递
}
