package com.soft.entity;

import com.soft.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 收货地址实体类
 * </p>
 *
 * @author Byterain
 * @since 2025-06-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_deliveryaddress") // 假设数据库表名为 delivery_address
public class Address extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO) // 主键ID
    private Long id;

    @TableField("account_id")
    private String accountId; // 用户手机号

    @TableField("contact_name")
    private String contactName; // 联系人姓名

    @TableField("contact_tel")
    private String contactTel; // 联系电话

    @TableField("contact_sex")
    private Integer contactSex; // 性别：1男，2女

    @TableField("address")
    private String address; // 详细地址

    @TableField("is_default")
    private Integer isDefault; // 是否默认地址：1是，0否

    /**
     * 用于接收前端传来的字符串性别："1"或"0"
     * 不映射到数据库字段
     */
    @TableField(exist = false)
    private String contactSexStr;

}