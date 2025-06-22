package com.soft.service.impl;

import com.soft.service.CartService;
import com.soft.mapper.CartMapper; // 假设您有 CartMapper
import com.soft.entity.Cart; // 假设您需要导入 Cart 实体
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service; // 确保导入这个注解

/**
 * <p>
 * 购物车服务实现类
 * </p>
 */
@Service // <-- 确保这里有 @Service 注解
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    // ... 您的 CartService 接口方法的实现 ...
}