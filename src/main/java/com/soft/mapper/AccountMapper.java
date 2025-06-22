package com.soft.mapper;

import com.soft.entity.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.ManagedBean;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Byterain
 * @since 2025-06-19
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
