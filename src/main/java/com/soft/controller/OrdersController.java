package com.soft.controller;

import com.soft.common.Result;
import com.soft.entity.Orders;
import com.soft.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.soft.common.BaseController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-22
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrdersController extends BaseController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 获取用户的订单列表
     * @param accountId 用户ID
     * @return 订单列表
     */
    @GetMapping("/{accountId}")
    public Result getOrdersByAccountId(@PathVariable String accountId) {
        try {
            List<Orders> orders = ordersService.getOrdersWithBusinessInfo(accountId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.fail("获取订单列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取订单信息
     * @param orderId
     * @return
     */
    @GetMapping("/getOrdersById/{orderId}")
    public Result getOrdersById(@PathVariable("orderId") Integer orderId) {
        Orders orders = ordersService.getById(orderId);
        return Result.success(orders);
    }

    /**
     * 创建订单
     * @param orders
     * @return
     */
    @PostMapping("/create")
    public Result create(@RequestBody Orders orders) {
        ordersService.save(orders);
        return Result.success(orders.getOrderId());
    }

    /**
     * 支付
     * @param orderId
     * @return
     */
    @GetMapping("/pay/{orderId}")
    public Result pay(@PathVariable("orderId") Integer orderId) {
        Orders orders = ordersService.getById(orderId);
        orders.setStatu(1);
        ordersService.updateById(orders);
        return Result.success(1);
    }
}