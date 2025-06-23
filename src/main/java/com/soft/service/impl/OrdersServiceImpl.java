package com.soft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.entity.Business;
import com.soft.entity.Orders;
import com.soft.mapper.OrdersMapper;
import com.soft.service.BusinessService;
import com.soft.service.OrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Byterain
 * @since 2025-06-22
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private BusinessService businessService;

    @Override
    public List<Orders> getOrdersWithBusinessInfo(String accountId) {
        // 1. 获取用户的所有订单
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId)
                .orderByDesc("create_time");
        List<Orders> orders = this.list(queryWrapper);

        // 如果没有订单，直接返回空列表
        if (orders.isEmpty()) {
            return orders;
        }

        // 2. 获取所有涉及的商家ID
        List<Long> businessIds = orders.stream()
                .map(Orders::getBusinessId)
                .distinct()
                .collect(Collectors.toList());

        // 3. 批量查询商家信息
        QueryWrapper<Business> businessQueryWrapper = new QueryWrapper<>();
        businessQueryWrapper.in("business_id", businessIds);
        List<Business> businesses = businessService.list(businessQueryWrapper);

        // 4. 创建商家ID到商家名称的映射
        Map<Long, String> businessMap = businesses.stream()
                .collect(Collectors.toMap(
                        Business::getBusinessId,
                        Business::getBusinessName
                ));

        // 5. 为每个订单设置商家名称
        orders.forEach(order -> 
            order.setBusinessName(businessMap.get(order.getBusinessId()))
        );

        return orders;
    }
}