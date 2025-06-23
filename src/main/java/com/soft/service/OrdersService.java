package com.soft.service;

import com.soft.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Byterain
 * @since 2025-06-22
 */
public interface OrdersService extends IService<Orders> {
    /**
     * 获取用户订单列表，包含商家信息
     * @param accountId 用户ID
     * @return 订单列表
     */
    List<Orders> getOrdersWithBusinessInfo(String accountId);
}