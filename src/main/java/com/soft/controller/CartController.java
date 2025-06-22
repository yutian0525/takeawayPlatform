package com.soft.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.common.Result;
import com.soft.entity.Business;
import com.soft.entity.Cart;
import com.soft.entity.Goods;
import com.soft.service.CartService;
import com.soft.service.GoodsService; // 导入 GoodsService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.soft.common.BaseController;

import java.util.List;

/**
 * <p>
 * 前端控制器
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
    @Autowired // 注入 GoodsService
    private GoodsService goodsService;

        
    @GetMapping("/listCartByAccountId/{accountId}") 
    public Result listCart(@PathVariable String accountId) {
    QueryWrapper<Cart> qw = new QueryWrapper<>();
    qw.eq("account_id",accountId);
    List<Cart> list = cService.list(qw); // 将 cartService 改为 cService
    return Result.success(list);
    }
    //商家详情页面 加载购物车方法
    @GetMapping("/listCart/{accountId}/{businessId}")
    public Result listCart(@PathVariable String accountId,@PathVariable Long businessId){
    QueryWrapper<Cart> qw = new QueryWrapper<>();
    qw.eq("account_id",accountId);
    qw.eq("business_id",businessId);
    List<Cart> list = cService.list(qw); // 将 cartService 改为 cService
    //使用购物车中 goods_id，再次查询商品数据
    list.stream().forEach( cart ->{
    Goods goods = goodsService.getById(cart.getGoodsId());
    cart.setGoods(goods);
    });
    return Result.success(list);
    }


}
