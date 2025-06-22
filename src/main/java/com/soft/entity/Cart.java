package com.soft.entity;

import com.soft.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.soft.entity.Goods;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Byterain
 * @since 2025-06-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_cart")
public class Cart extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 无意义编号，编号
     */
    @TableId(value = "cart_id", type = IdType.AUTO)
    private Integer cartId;

    /**
     * 商品编号-sys_goods主键goods_id
     */
    @TableField("goods_id")
    private Integer goodsId;

    /**
     * 所属商家编号-sys_business主键businessId
     */
    @TableField("business_id")
    private Long businessId;

    /**
     * 所属用户编号sys_account表account_id
     */
    @TableField("account_id")
    private String accountId;
    // 自定义：商品对象 ，表示该变量不是映射数据库sys_cart表，该字段就是为了查询需要添加的


    /**
     * 同一类型商品的购买数量
     */
    @TableField("quantity")
    private Integer quantity;
    @TableField(exist = false)
    private Goods goods;
}
