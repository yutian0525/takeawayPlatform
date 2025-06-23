package com.soft.controller;

import com.soft.common.BaseController;
import com.soft.common.Result;
import com.soft.entity.Business;
import com.soft.entity.SysFavorite;
import com.soft.service.BusinessService;
import com.soft.service.SysFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  收藏控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-22
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController extends BaseController {

    @Autowired
    private SysFavoriteService sysFavoriteService;

    @Autowired
    private BusinessService businessService;

    /**
     * 添加收藏
     * @param sysFavorite 收藏信息
     * @return 结果
     */
    @PostMapping("/add")
    public Result addFavorite(@RequestBody SysFavorite sysFavorite) {
        // 设置创建和更新时间
        sysFavorite.setCreated(new Date());
        sysFavorite.setUpdated(new Date());
        // 设置状态为1（有效）
        sysFavorite.setStatu(1);
        
        // 检查是否已经收藏
        SysFavorite existFavorite = sysFavoriteService.findByAccountIdAndBusinessId(
                sysFavorite.getAccountId(), sysFavorite.getBusinessId());
        
        if (existFavorite != null) {
            // 如果已存在但状态为0（已取消），则更新状态为1
            if (existFavorite.getStatu() == 0) {
                existFavorite.setStatu(1);
                existFavorite.setUpdated(new Date());
                sysFavoriteService.update(existFavorite);
                return Result.success("收藏成功");
            }
            // 如果已存在且状态为1，则返回已收藏信息
            return Result.fail("已收藏该商家");
        }
        
        // 添加新收藏
        boolean success = sysFavoriteService.save(sysFavorite);
        if (success) {
            return Result.success("收藏成功");
        } else {
            return Result.fail("收藏失败");
        }
    }

    /**
     * 取消收藏
     * @param accountId 用户ID
     * @param businessId 商家ID
     * @return 结果
     */
    @PostMapping("/cancel")
    public Result cancelFavorite(@RequestParam("accountId") String accountId,
                                        @RequestParam("businessId") Long businessId) {
        // 查找收藏记录
        SysFavorite favorite = sysFavoriteService.findByAccountIdAndBusinessId(accountId, businessId);
        
        if (favorite == null) {
            return Result.fail("未收藏该商家");
        }
        
        // 将状态设置为0（已取消）
        favorite.setStatu(0);
        favorite.setUpdated(new Date());
        
        boolean success = sysFavoriteService.update(favorite);
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.fail("取消收藏失败");
        }
    }

    /**
     * 获取用户收藏的商家列表
     * @param accountId 用户ID
     * @return 商家列表
     */
    @GetMapping("/list")
    public Result getFavoriteList(@RequestParam("accountId") String accountId) {
        List<Business> businessList = businessService.getFavoriteBusinessesByAccountId(accountId);
        return Result.success(businessList);
    }

    /**
     * 检查用户是否收藏了某商家
     * @param accountId 用户ID
     * @param businessId 商家ID
     * @return 是否收藏
     */
    @GetMapping("/check")
    public Result checkFavorite(@RequestParam("accountId") String accountId,
                                        @RequestParam("businessId") Long businessId) {
        SysFavorite favorite = sysFavoriteService.findByAccountIdAndBusinessId(accountId, businessId);
        boolean isFavorite = (favorite != null && favorite.getStatu() == 1);
        return Result.success(isFavorite);
    }
}