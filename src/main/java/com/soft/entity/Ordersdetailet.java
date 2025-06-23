package com.soft.entity;

// import com.soft.common.BaseEntity; // 移除：不再继承 BaseEntity
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
// import lombok.EqualsAndHashCode; // 移除：不再继承 BaseEntity，因此不需要 callSuper = true

@Data
// @EqualsAndHashCode(callSuper = true) // 移除此行
@TableName("sys_ordersdetailet")
public class Ordersdetailet { // 更改：不再继承 BaseEntity

    private static final long serialVersionUID = 1L; // 保持此行，如果需要序列化

    @TableId(value = "od_id", type = IdType.AUTO)
    private Long odId;

    @TableField("order_id")
    private Long orderId;

    @TableField("goods_id")
    private Long goodsId;

    @TableField("quantity")
    private Integer quantity;

    /**
     * 商品名称 (不映射到数据库)
     */
    @TableField(exist = false)
    private String goodsName;

    /**
     * 商品价格 (不映射到数据库)
     */
    @TableField(exist = false)
    private BigDecimal goodsPrice;

    /**
     * 商品图片 (不映射到数据库)
     */
    @TableField(exist = false)
    private String goodsImg; // 新增：商品图片字段
}
// 移除 created, updated, statu 字段，因为它们不在 sys_ordersdetailet 表中
// 如果您的数据库表 sys_ordersdetailet 确实有这些字段，请在此处手动添加它们
// 例如：
// @TableField("created")
// private LocalDateTime created;
// @TableField("updated")
// private LocalDateTime updated;
// @TableField("statu")
// private Integer statu;