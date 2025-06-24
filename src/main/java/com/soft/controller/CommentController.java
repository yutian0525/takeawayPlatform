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
import java.time.LocalDateTime;

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

    /**
     * 添加评论
     * @param comment 评论信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody Comment comment) {
        // 设置评论状态为有效
        comment.setStatu(1);
        // 设置创建时间和更新时间
        comment.setCreated(LocalDateTime.now());
        comment.setUpdated(LocalDateTime.now());

        boolean success = commentService.save(comment);
        if (success) {
            return Result.success(null);
        } else {
            return Result.fail("添加评论失败");
        }
    }
    
    /**
     * 修改评论
     * @param comment 评论信息
     * @return 修改结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody Comment comment) {
        // 设置更新时间
        comment.setUpdated(LocalDateTime.now());
        
        boolean success = commentService.updateById(comment);
        if (success) {
            return Result.success(null);
        } else {
            return Result.fail("修改评论失败");
        }
    }
    
    /**
     * 删除评论
     * @param id 评论ID
     * @return 删除结果
     */
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = commentService.removeById(id);
        if (success) {
            return Result.success(null);
        } else {
            return Result.fail("删除评论失败");
        }
    }
    
    /**
     * 获取评论详情
     * @param id 评论ID
     * @return 评论详情
     */
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Integer id) {
        Comment comment = commentService.getById(id);
        if (comment != null) {
            return Result.success(comment);
        } else {
            return Result.fail("评论不存在");
        }
    }
}