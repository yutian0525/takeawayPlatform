package com.soft.entity;

import com.soft.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Byterain
 * @since 2025-06-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_account")
public class Account extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "account_id", type = IdType.INPUT)
    private String accountId;

    @TableField("password")
    private String password;

    @TableField("account_name")
    private String accountName;

    @TableField("account_sex")
    private Integer accountSex;

    @TableField("account_img")
    private String accountImg;

    @TableField("del_tag")
    private Integer delTag;

    public Account(String accountId, String password, String accountName, Integer accountSex) {
        this.accountId = accountId;
        this.password = password;
        this.accountName = accountName;
        this.accountSex = accountSex;
        this.accountImg = accountImg;
        this.delTag = delTag;
    }
    public Account() {}
}
