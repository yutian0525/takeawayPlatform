package com.soft.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.common.Result;
import com.soft.entity.Business;
import com.soft.entity.Cart;
import com.soft.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.soft.common.BaseController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-22
 */
@RestController
@CrossOrigin
@RequestMapping("/cart")
public class CartController extends BaseController {
    @Autowired
    private CartService cService;
    @GetMapping("/listCart/{accountId}/{businessId}")
    public Result ListCart(@PathVariable String accountId,@PathVariable String businessId) {
        List<Cart> clist = cService.list(new QueryWrapper<Cart>().eq("accountId", accountId).eq("businessId", businessId));
        return Result.success(clist);
    }
}
