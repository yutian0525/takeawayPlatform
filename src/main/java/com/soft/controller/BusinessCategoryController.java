package com.soft.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.common.Result;
import com.soft.entity.Business;
import com.soft.entity.BusinessCategory;
import com.soft.service.BusinessCategoryService;
import com.soft.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.soft.common.BaseController;

import java.util.ArrayList;
import java.util.List;

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
@RequestMapping("/business-category")
public class BusinessCategoryController extends BaseController {
    @Autowired
    private BusinessCategoryService bcService;
    @Autowired
    private BusinessService bService;
    @GetMapping("/listByCategoryId/{categoryId}")
    public Result listByCategoryId(@PathVariable Integer categoryId) {
        //步骤1：先通过分类编号 查询sys_business_category表中 某个分类下所有的商家编号。
        //List<BusinessCategory> bcList = bcService.list();
        //List<Business> bList = bService.list(new QueryWrapper<Business>().ne("statu", 0));
        List<BusinessCategory> bcList = bcService.list(new QueryWrapper<BusinessCategory>().eq("category_id", categoryId));
        List<Business> bList = new ArrayList<>();
        bcList.forEach(bc -> {
            Business business = bService.getById(bc.getBusinessId());
            bList.add(business);
        });
        return Result.success(bList);
        //return Result.success(null);
    }
}
