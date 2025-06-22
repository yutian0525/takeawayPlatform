package com.soft.service;

import com.soft.entity.Deliveryaddress;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface DeliveryaddressService extends IService<Deliveryaddress> {
    // 获取用户的所有地址
    List<Deliveryaddress> getAddressByAccountId(String accountId);
    
    // 获取用户的默认地址
    Deliveryaddress getDefaultAddress(String accountId);
}