package com.soft.service;

import com.soft.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List; // 导入List

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Byterain
 * @since 2025-06-22
 */
public interface OrdersService extends IService<Orders> {
    // 获取用户的订单列表，包含商家信息
    List<Orders> getOrdersWithBusinessInfo(String accountId);

    // 创建订单及订单明细
    void createOrderWithDetails(Orders orders);

    // 新增：获取订单详情，包含商家信息和订单明细
    Orders getOrderDetailForPayment(Long orderId);
}