package com.soft.entity;
import com.soft.common.BaseEntity;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode; // 即使不继承BaseEntity，如果需要equals和hashCode方法，可以保留
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime; // 导入 LocalDateTime
import java.util.List;

@Data
// @EqualsAndHashCode(callSuper = true) // 移除：不再继承BaseEntity
@TableName("sys_orders")
public class Orders { // 更改：不再继承 BaseEntity

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
     * 商家名称 (已存在，但需要确保在服务层被填充)
     */
    @TableField(exist = false)
    private String businessName;

    /**
     * 订单总金额 (已存在)
     */
    @TableField("order_total")
    private BigDecimal amount;

    /**
     * 订单状态 (已存在)
     */
    @JsonProperty("state")
    @TableField("state")
    private Integer state;

    // /**
    //  * 订单备注
    //  */
    // @TableField("note")
    // private String note;

    // // /**
    //  * 配送地址ID
    //  */
    @TableField("da_id")
    private Long daId;

    /**
     * 创建时间
     */
    @TableField("created") // 更改：直接定义 created 字段
    private LocalDateTime created;

    /**
     * 修改时间
     */
    @TableField("updated") // 更改：直接定义 updated 字段
    private LocalDateTime updated;

    /**
     * 关联的商家信息 (不映射到数据库)
     */
    @TableField(exist = false)
    private Business business; // 新增：用于携带商家完整信息

    /**
     * 订单明细列表 (不映射到数据库)
     */
    @TableField(exist = false)
    private List<Ordersdetailet> orderdetails; // 确保这个字段被填充
}