package com.soft.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soft.common.BaseController;
import com.soft.common.Result;
import com.soft.entity.Deliveryaddress;
import com.soft.service.DeliveryaddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/deliveryaddress")
public class DeliveryaddressController extends BaseController {

    @Autowired
    private DeliveryaddressService deliveryaddressService;

    // 获取用户的所有配送地址
    @GetMapping("/list/{accountId}")
    public Result list(@PathVariable String accountId) {
        try {
            List<Deliveryaddress> list = deliveryaddressService.getAddressByAccountId(accountId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.fail("获取地址列表失败：" + e.getMessage());
        }
    }

    // 获取默认地址
    @GetMapping("/default/{accountId}")
    public Result getDefaultAddress(@PathVariable String accountId) {
        try {
            Deliveryaddress address = deliveryaddressService.getDefaultAddress(accountId);
            return Result.success(address);
        } catch (Exception e) {
            return Result.fail("获取默认地址失败：" + e.getMessage());
        }
    }
    // 添加新地址
    @PostMapping("/save")
    public Result save(@RequestBody Deliveryaddress address) {
        boolean success = deliveryaddressService.save(address);
        return success ? Result.success(null) : Result.fail("添加地址失败");
    }

    // 更新地址
    @PostMapping("/update")
    public Result update(@RequestBody Deliveryaddress address) {
        boolean success = deliveryaddressService.updateById(address);
        return success ? Result.success(null) : Result.fail("更新地址失败");
    }

    // 删除地址
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = deliveryaddressService.removeById(id);
        return success ? Result.success(null) : Result.fail("删除地址失败");
    }
}