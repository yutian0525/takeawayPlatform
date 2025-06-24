package com.soft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.entity.SysFavorite;

/**
 * 收藏服务接口
 */
public interface SysFavoriteService extends IService<SysFavorite> {

    /**
     * 根据用户ID和商家ID查找收藏记录
     * @param accountId 用户ID
     * @param businessId 商家ID
     * @return 收藏记录
     */
    SysFavorite findByAccountIdAndBusinessId(String accountId, Long businessId);

    /**
     * 保存收藏记录
     * @param sysFavorite 收藏记录
     * @return 是否成功
     */
    boolean save(SysFavorite sysFavorite);

    /**
     * 更新收藏记录
     * @param sysFavorite 收藏记录
     * @return 是否成功
     */
    boolean update(SysFavorite sysFavorite);
}