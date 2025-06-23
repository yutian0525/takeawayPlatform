package com.soft.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.common.BaseController;
import com.soft.common.Result;
import com.soft.entity.Comment;
import com.soft.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     * @return 评论列表（包含用户信息）
     */
    @GetMapping("/list/{businessId}")
    public Result listByBusinessId(@PathVariable Long businessId) {
        List<Map<String, Object>> comments = commentService.getCommentListWithAccount(businessId);
        return Result.success(comments);
    }

    /**
     * 根据用户ID获取评论列表
     * @param accountId 用户ID
     * @return 评论列表
     */
    @GetMapping("/list/user/{accountId}")
    public Result listByAccountId(@PathVariable String accountId) {
        List<Map<String, Object>> comments = commentService.getCommentListWithBusiness(accountId);
        return Result.success(comments);
    }
}