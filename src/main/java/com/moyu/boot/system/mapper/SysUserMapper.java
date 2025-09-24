package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.system.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表sys_user(用户信息)的数据库操作Mapper
 *
 * @author shisong
 * @since 2024-12-25 20:35:45
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
