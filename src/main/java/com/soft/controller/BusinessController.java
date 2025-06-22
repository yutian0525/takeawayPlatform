package com.soft.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.common.BaseController;
import com.soft.common.Result;
import com.soft.entity.Business;
import com.soft.entity.BusinessCategory;
import com.soft.entity.Goods;
import com.soft.service.BusinessCategoryService;
import com.soft.service.BusinessService;
import com.soft.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-20
 */
@RestController
@CrossOrigin
@RequestMapping("/business")
public class BusinessController extends BaseController {
    @Autowired
    private BusinessService bService;
    @Autowired
    private BusinessCategoryService bcService;
    @Autowired
    private GoodsService gService;
    @GetMapping("/list")
    public Result list() {
        List<Business> list = bService.list(new QueryWrapper<Business>().ne("statu", 0));
        if (list == null) {
            return Result.fail(30001, "暂无商家数据显示", null);
        }
        return Result.success(list);
    }

    @GetMapping("/info/{businessId}")
    public Result info(@PathVariable Long businessId){
        Business business = bService.getById(businessId);
        if(business == null){
            return  Result.fail("商家的详情数据加载失败");
        }else{
            return Result.success(business);
        }
    }

    @GetMapping("/listByBusinessId/{businessId}")
    public Result listByBusinessId(@PathVariable Long businessId){
        List<Goods> goodsList = gService.list(new QueryWrapper<Goods>().eq("business_id",
                businessId));
        if(goodsList==null){
            return Result.fail("商品数据加载失败");
        }else{
            return Result.success(goodsList);
        }
    }



}
