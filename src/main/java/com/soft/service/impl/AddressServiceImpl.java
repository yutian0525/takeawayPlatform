package com.soft.service.impl;

import com.soft.entity.Address;
import com.soft.mapper.AddressMapper;
import com.soft.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收货地址 服务实现类
 * </p>
 *
 * @author Byterain
 * @since 2025-06-23
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

}