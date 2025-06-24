package com.soft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.entity.Business;
import com.soft.entity.Orders;
import com.soft.mapper.OrdersMapper;
import com.soft.service.BusinessService;
import com.soft.service.OrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.service.OrdersdetailetService;
import com.soft.service.GoodsService;
import com.soft.entity.Goods;
import com.soft.entity.Ordersdetailet; // 导入 Ordersdetailet
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors; // 导入 Collectors

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

    @Autowired
    private OrdersdetailetService ordersdetailetService;

    @Autowired
    private GoodsService goodsService;

    @Override
    public List<Orders> getOrdersWithBusinessInfo(String accountId) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);
        List<Orders> ordersList = this.list(queryWrapper);

        ordersList.forEach(orders -> {
            Business business = businessService.getById(orders.getBusinessId());
            if (business != null) {
                orders.setBusinessName(business.getBusinessName());
            }
        });
        return ordersList;
    }

    @Override
    @Transactional // 开启事务
    public void createOrderWithDetails(Orders orders) {
        // 移除：不再显式设置 created 和 updated，依赖数据库默认值或由MyBatis-Plus自动填充（如果配置了）
        orders.setCreated(LocalDateTime.now());
        orders.setUpdated(LocalDateTime.now());
        // 移除：不再显式设置 state，使用前端传入的 state (0)
        // orders.setState(1);

        // 保存订单主表
        this.save(orders);

        // 获取保存后生成的订单ID
        Long orderId = orders.getOrderId();
        BigDecimal calculatedTotalAmount = BigDecimal.ZERO; // 初始化计算出的总金额

        // 保存订单明细并计算总金额
        if (orders.getOrderdetails() != null && !orders.getOrderdetails().isEmpty()) {
            for (Ordersdetailet detail : orders.getOrderdetails()) {
                detail.setOrderId(orderId); // 设置明细所属的订单ID
                // 移除：Ordersdetailet 不再继承 BaseEntity，因此不再有这些字段
                // detail.setCreated(LocalDateTime.now());
                // detail.setUpdated(LocalDateTime.now());
                // detail.setStatu(1);
                ordersdetailetService.save(detail);

                // 根据商品ID获取商品信息，计算总金额
                Goods goods = goodsService.getById(detail.getGoodsId());
                if (goods != null && goods.getGoodsPrice() != null && detail.getQuantity() != null) {
                    calculatedTotalAmount = calculatedTotalAmount.add(
                        goods.getGoodsPrice().multiply(BigDecimal.valueOf(detail.getQuantity()))
                    );
                }
            }
        }

        // 添加配送费到总金额
        Business business = businessService.getById(orders.getBusinessId());
        if (business != null && business.getDeliveryPrice() != null) {
            calculatedTotalAmount = calculatedTotalAmount.add(business.getDeliveryPrice());
        }

        // 更新订单主表的总金额
        orders.setAmount(calculatedTotalAmount);
        this.updateById(orders); // 更新数据库中的订单记录
    }

    @Override
    public Orders getOrderDetailForPayment(Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            return null;
        }

        // 填充商家信息
        Business business = businessService.getById(order.getBusinessId());
        if (business != null) {
            order.setBusiness(business); // 设置完整的 Business 对象
            order.setBusinessName(business.getBusinessName()); // 也可以单独设置 businessName
            // 假设 Business 实体有 businessImg 字段，无需额外设置，直接通过 order.getBusiness().getBusinessImg() 访问
        }

        // 填充订单明细
        QueryWrapper<Ordersdetailet> detailQueryWrapper = new QueryWrapper<>();
        detailQueryWrapper.eq("order_id", orderId);
        List<Ordersdetailet> details = ordersdetailetService.list(detailQueryWrapper);

        // 为每个订单明细填充商品名称、价格和图片
        if (details != null && !details.isEmpty()) {
            details = details.stream().map(detail -> {
                Goods goods = goodsService.getById(detail.getGoodsId());
                if (goods != null) {
                    detail.setGoodsName(goods.getGoodsName());
                    detail.setGoodsPrice(goods.getGoodsPrice());
                    detail.setGoodsImg(goods.getGoodsImg()); // 新增：设置商品图片
                }
                return detail;
            }).collect(Collectors.toList());
        }
        order.setOrderdetails(details);

        return order;
    }
}