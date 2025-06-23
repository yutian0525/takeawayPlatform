package com.soft.common;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BaseEntity {
    /**
     * 创建时间
     */
    @TableField("created")
    private LocalDateTime created;
    /**
     * 修改时间
     */
    @TableField("updated")
    private LocalDateTime updated;
    @TableField("statu") // 更改：字段名和数据库映射改回 statu
    private Integer statu; // 更改：字段名从 state 改回 statu
}