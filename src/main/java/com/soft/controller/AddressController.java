package com.soft.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.common.BaseController;
import com.soft.common.Result;
import com.soft.entity.Address;
import com.soft.service.AddressService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 地址管理 前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-23
 */
@CrossOrigin
@RestController
@RequestMapping("/address")
@Log4j2
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;

    /**
     * 根据用户ID查询所有地址
     *
     * @param accountId 用户ID
     * @return 地址列表
     */
    @GetMapping("/list/{accountId}")
    public Result list(@PathVariable String accountId) {
        log.info("正在查询用户 {} 的地址列表", accountId);
        List<Address> addresses = addressService.list(new QueryWrapper<Address>().eq("account_id", accountId));
        return Result.success(addresses);
    }

    /**
     * 新增地址
     *
     * @param address 地址信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody Address address) {
        log.info("正在为用户 {} 新增地址", address.getAccountId());

        // 将 contactSexStr 转换为 contactSex
        if ("1".equals(address.getContactSexStr())) {
            address.setContactSex(1); // 男
        } else if ("0".equals(address.getContactSexStr())) {
            address.setContactSex(2); // 女
        }

        addressService.save(address);
        return Result.success("地址添加成功");
    }

    /**
     * 更新地址信息
     *
     * @param address 地址信息
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody Address address) {
        log.info("正在更新地址 ID: {}", address.getId());

        // 将 contactSexStr 转换为 contactSex
        if ("1".equals(address.getContactSexStr())) {
            address.setContactSex(1); // 男
        } else if ("0".equals(address.getContactSexStr())) {
            address.setContactSex(2); // 女
        }

        addressService.updateById(address);
        return Result.success("地址更新成功");
    }

    /**
     * 删除地址
     *
     * @param id 地址ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("正在删除地址 ID: {}", id);
        addressService.removeById(id);
        return Result.success("地址删除成功");
    }

    /**
     * 查询单个地址详情
     *
     * @param id 地址ID
     * @return 地址信息
     */
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        log.info("正在查询地址 ID: {}", id);
        Address address = addressService.getById(id);
        if (address == null) {
            return Result.fail("地址不存在");
        }
        return Result.success(address);
    }
}