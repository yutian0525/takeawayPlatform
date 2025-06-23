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
     * 根据ID获取订单信息 (现在返回完整的订单详情)
     * @param orderId
     * @return
     */
    @GetMapping("/getOrdersById/{orderId}")
    public Result getOrdersById(@PathVariable("orderId") Long orderId) { // 更改：参数类型为 Long
        Orders orders = ordersService.getOrderDetailForPayment(orderId); // 更改：调用新的服务方法
        if (orders == null) {
            return Result.fail("订单不存在");
        }
        return Result.success(orders);
    }

    /**
     * 创建订单
     * @param orders
     * @return
     */
    @PostMapping("/create")
    public Result create(@RequestBody Orders orders) {
        try {
            ordersService.createOrderWithDetails(orders);
            return Result.success(orders.getOrderId());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 支付
     * @param orderId
     * @return
     */
    @PostMapping("/pay/{orderId}") // 更改：将 @GetMapping 修改为 @PostMapping
    public Result pay(@PathVariable("orderId") Integer orderId) {
        Orders orders = ordersService.getById(orderId);
        if (orders == null) {
            return Result.fail("订单不存在");
        }
        orders.setState(1); // 保持不变，因为 Orders 实体现在有自己的 state 字段
        ordersService.updateById(orders);
        return Result.success(1);
    }

    /**
     * 更新订单状态
     * @param orders 包含 orderId 和 state 的订单对象
     * @return
     */
    @PostMapping("/updateState")
    public Result updateState(@RequestBody Orders orders) {
        try {
            // 假设 orders 对象中包含了 orderId 和 state
            // 根据 orderId 获取现有订单
            Orders existingOrder = ordersService.getById(orders.getOrderId());
            if (existingOrder == null) {
                return Result.fail("订单不存在");
            }
            // 更新订单状态
            existingOrder.setState(orders.getState());
            ordersService.updateById(existingOrder);
            return Result.success("订单状态更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新订单状态失败：" + e.getMessage());
        }
    }

    /**
     * 删除订单
     * @param orderId
     * @return
     */
    @DeleteMapping("/{orderId}") // 新增：删除订单接口
    public Result deleteOrder(@PathVariable("orderId") Long orderId) {
        try {
            boolean removed = ordersService.removeById(orderId);
            if (removed) {
                return Result.success("订单删除成功");
            } else {
                return Result.fail("订单删除失败，订单不存在或已被删除");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("删除订单异常：" + e.getMessage());
        }
    }
}