package com.soft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.entity.Comment;

import java.util.List;
import java.util.Map;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {
    // 继承IService提供的基础CRUD方法
    
    /**
     * 获取评论列表及关联的商家信息
     * @param accountId 用户ID
     * @return 评论列表（包含商家信息）
     */
    List<Map<String, Object>> getCommentListWithBusiness(String accountId);
}