package com.soft.controller;
import com.soft.common.Result;
import com.soft.entity.Goods;
import com.soft.service.GoodsService;
import com.soft.service.impl.GoodsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.soft.common.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-21
 */
@RestController
@RequestMapping("/goods")
public class GoodsController extends BaseController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 根据商品ID获取商品详细信息
     * @param goodsId 商品ID
     * @return 商品信息
     */
    @GetMapping("/info/{goodsId}")
    public Result getGoodsInfoById(@PathVariable Long goodsId) {
        Goods goods = goodsService.getById(goodsId);
        if (goods != null) {
            return Result.success(goods);
        } else {
            return Result.fail("未找到ID为 " + goodsId + " 的商品信息");
        }
    }
}
