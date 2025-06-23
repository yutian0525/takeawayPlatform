package com.soft.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.common.BaseController;
import com.soft.common.Result;
import com.soft.entity.Comment;
import com.soft.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/comment")
@CrossOrigin
public class CommentController extends BaseController {
    @Autowired
    private CommentService commentService;

    /**
     * 根据商家ID获取评论列表
     * @param businessId 商家ID
     * @return 评论列表
     */
    @GetMapping("/list/{businessId}")
    public Result listByBusinessId(@PathVariable Long businessId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("business_id", businessId)
                   .eq("statu", 1)  // 只查询未删除的评论
                   .orderByDesc("created");  // 按创建时间降序排序

        List<Comment> comments = commentService.list(queryWrapper);
        return Result.success(comments);
    }

    /**
     * 根据用户ID获取评论列表
     * @param accountId 用户ID
     * @return 评论列表
     */
    @GetMapping("/list/user/{accountId}")
    public Result listByAccountId(@PathVariable String accountId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId)
                   .eq("statu", 1)  // 只查询未删除的评论
                   .orderByDesc("created");  // 按创建时间降序排序

        List<Comment> comments = commentService.list(queryWrapper);
        return Result.success(comments);
    }
}