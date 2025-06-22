package com.soft.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.common.Result;
import com.soft.entity.Account;
import com.soft.service.AccountService;
import com.soft.utils.MD5Utils;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.soft.common.BaseController;
import com.soft.common.Const;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-19
 */
@CrossOrigin
@RestController
@RequestMapping("/account")
@Log4j2
public class AccountController extends BaseController {
    @Autowired
    private AccountService aService;
    @GetMapping("/check/{accountId}")
    public Result check(@PathVariable String accountId){
        Account account = aService.getById(accountId);
        if(account == null){
            return Result.success("手机号码可以注册");
        }
        else{
            return Result.fail(20005,"用户已存在",null);
        }
    }
    @PostMapping("/login")
    public Result login(String accountId,String password) {
        log.info("手机号为 {} 用户正在登录APP端--", accountId);
        //手机号在sys_account表中是主键，是不重复的，查询返回就是单一对象
        Account account = aService.getOne(new QueryWrapper<Account>().eq("account_id", accountId));
        if (account == null) {
            return Result.fail("账户手机号码不存在");
        }
        else {
            //account不等于null，说明查询到该手机号码，继续比较密码是否一致
            String newPwd = MD5Utils.md5(password);  //将登录原文密码 先加密，变为 密文
            if (newPwd.equals(account.getPassword())) {
            //if (password.equals(account.getPassword())) {
                if (account.getStatu() == 0) {
                    return Result.fail("该账户被禁用或被注销，暂不可用");
                }
                else {
                    //登录成功，直接反馈登录账户对象信息
                    return Result.success(account);
                }
            }
            else {
                return Result.fail("登录密码不正确");
            }
        }
    }
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        log.info("用户{}正在注册", account.getAccountId());
        account.setPassword(MD5Utils.md5(account.getPassword()));
        account.setCreated(LocalDateTime.now());
        account.setUpdated(LocalDateTime.now());
        account.setStatu(1);
        if(account.getAccountSex()==1){
            account.setAccountImg(Const.DEFAULT_IMG_1 );
        }
        else{
            account.setAccountImg(Const.DEFAULT_IMG_0);
        }
        aService.save(account);
        return Result.success("用户注册成功，请登录");
    }
}
