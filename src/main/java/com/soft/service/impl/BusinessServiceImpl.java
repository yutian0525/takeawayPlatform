package com.soft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.entity.Business;
import com.soft.entity.SysFavorite;
import com.soft.mapper.BusinessMapper;
import com.soft.mapper.SysFavoriteMapper;
import com.soft.service.BusinessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Byterain
 * @since 2025-06-20
 */
@Service
public class BusinessServiceImpl extends ServiceImpl<BusinessMapper, Business> implements BusinessService {

    @Autowired
    private SysFavoriteMapper sysFavoriteMapper;

    @Override
    public List<Business> getFavoriteBusinessesByAccountId(String accountId) {
        // 查询用户收藏的商家ID列表
        QueryWrapper<SysFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);
        queryWrapper.eq("statu", 1); // 只查询状态为有效的收藏
        List<SysFavorite> favorites = sysFavoriteMapper.selectList(queryWrapper);
        
        // 如果没有收藏记录，返回空列表
        if (favorites == null || favorites.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取商家ID列表
        List<Long> businessIds = new ArrayList<>();
        for (SysFavorite favorite : favorites) {
            businessIds.add(favorite.getBusinessId());
        }
        
        // 查询商家信息
        QueryWrapper<Business> businessQueryWrapper = new QueryWrapper<>();
        businessQueryWrapper.in("business_id", businessIds);
        return this.list(businessQueryWrapper);
    }
}
