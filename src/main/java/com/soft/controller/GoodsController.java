package com.soft.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.common.Result;
import com.soft.entity.Business;
import com.soft.entity.Goods;
import com.soft.service.BusinessService;
import com.soft.service.GoodsService;
import com.soft.service.impl.GoodsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.soft.common.BaseController;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-21
 */
@RestController
@CrossOrigin
@RequestMapping("/goods")
public class GoodsController extends BaseController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private BusinessService businessService;
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
    @GetMapping("/search/{searchQuery}")
    public Result Search(@PathVariable String searchQuery){
// 1. 查询匹配的商品列表
        List<Goods> glist = goodsService.list(new QueryWrapper<Goods>()
                .like("goods_name", searchQuery));

        // 2. 如果商品列表为空，直接返回空列表
        if (glist.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        // 3. 收集所有不重复的businessId
        Set<Long> businessIds = glist.stream()
                .map(Goods::getBusinessId)
                .collect(Collectors.toSet());

        // 4. 查询相关商家
        List<Business> blist = businessService.list(new QueryWrapper<Business>()
                .in("business_id", businessIds));

        // 5. 返回商家列表
        return Result.success(blist);
    }
}
