package com.soft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 收藏实体类
 */
@Data
@TableName("sys_favorite")
public class SysFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏id
     */
    @TableId(value = "favorite_id", type = IdType.AUTO)
    private Long favoriteId;

    /**
     * 收藏的用户
     */
    private String accountId;

    /**
     * 被收藏商家
     */
    private Long businessId;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 状态（1：有效，0：无效）
     */
    private Integer statu;
}