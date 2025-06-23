package com.soft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.entity.Comment;
import com.soft.mapper.CommentMapper;
import com.soft.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    // 继承ServiceImpl提供的基础CRUD方法实现
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Override
    public List<Map<String, Object>> getCommentListWithBusiness(String accountId) {
        return commentMapper.getCommentListWithBusiness(accountId);
    }
    
    @Override
    public List<Map<String, Object>> getCommentListWithAccount(Long businessId) {
        return commentMapper.getCommentListWithAccount(businessId);
    }
}