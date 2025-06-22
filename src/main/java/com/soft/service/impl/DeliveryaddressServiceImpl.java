package com.soft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.entity.Deliveryaddress;
import com.soft.mapper.DeliveryaddressMapper;
import com.soft.service.DeliveryaddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryaddressServiceImpl extends ServiceImpl<DeliveryaddressMapper, Deliveryaddress> implements DeliveryaddressService {

    @Override
    public List<Deliveryaddress> getAddressByAccountId(String accountId) {
        return list(new QueryWrapper<Deliveryaddress>()
                .eq("account_id", accountId)
                .orderByDesc("statu"));
    }

    @Override
    public Deliveryaddress getDefaultAddress(String accountId) {
        return getOne(new QueryWrapper<Deliveryaddress>()
                .eq("account_id", accountId)
                .eq("statu", 1)
                .last("LIMIT 1"));
    }
}