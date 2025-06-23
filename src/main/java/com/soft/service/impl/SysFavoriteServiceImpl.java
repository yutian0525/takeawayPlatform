package com.soft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.entity.SysFavorite;
import com.soft.mapper.SysFavoriteMapper;
import com.soft.service.SysFavoriteService;
import org.springframework.stereotype.Service;

/**
 * 收藏服务实现类
 */
@Service
public class SysFavoriteServiceImpl extends ServiceImpl<SysFavoriteMapper, SysFavorite> implements SysFavoriteService {

    @Override
    public SysFavorite findByAccountIdAndBusinessId(String accountId, Long businessId) {
        QueryWrapper<SysFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);
        queryWrapper.eq("business_id", businessId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean save(SysFavorite sysFavorite) {
        return super.save(sysFavorite);
    }

    @Override
    public boolean update(SysFavorite sysFavorite) {
        return super.updateById(sysFavorite);
    }
}