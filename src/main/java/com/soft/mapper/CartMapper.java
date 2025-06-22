package com.soft.mapper;

import com.soft.entity.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper; // 导入 Mapper 注解

/**
 * <p>
 * 购物车 Mapper 接口
 * </p>
 *
 * @author Byterain
 * @since 2025-06-22
 */
@Mapper // 标记这是一个 MyBatis Mapper 接口
public interface CartMapper extends BaseMapper<Cart> {

}