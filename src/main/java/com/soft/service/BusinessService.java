package com.soft.service;

import com.soft.entity.Business;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Byterain
 * @since 2025-06-20
 */
public interface BusinessService extends IService<Business> {

    /**
     * 获取用户收藏的商家列表
     * @param accountId 用户ID
     * @return 商家列表
     */
    List<Business> getFavoriteBusinessesByAccountId(String accountId);
}
