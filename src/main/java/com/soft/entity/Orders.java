package com.soft.entity;
import com.soft.common.BaseEntity;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_orders")
public class Orders extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单编号
     */
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 下单用户编号--sys_account表account_id	
     */
    @TableField("account_id")
    private String accountId;

    /**
     * 商家编号--sys_business表business_id
     */
    @TableField("business_id")
    private Long businessId;

    /**
     * 订单总价
     */
    @TableField("order_total")
    private BigDecimal orderTotal;

    /**
     * 送货地址编号--sys_deliveryaddress表da_id
     */
    @TableField("da_id")
    private Integer daId;

    /**
     * 订单状态（0：未支付； 1：已支付）
     */
    @TableField("state")
    private Integer state;


}