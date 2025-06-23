package com.soft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    // 继承BaseMapper提供的基础CRUD方法
    
    /**
     * 获取评论列表及关联的商家信息
     * @param accountId 用户ID
     * @return 评论列表（包含商家信息）
     */
    List<Map<String, Object>> getCommentListWithBusiness(String accountId);
}