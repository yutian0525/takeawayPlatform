package com.soft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_comment")
public class Comment {
    @TableId(value = "co_id", type = IdType.AUTO)
    private Integer coId;
    
    private String accountId;
    private Long businessId;
    private Long orderId;
    private Double rate;
    private String coText;
    private String coImg;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Integer statu;
}