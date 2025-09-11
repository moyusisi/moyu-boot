package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.system.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shisong
 * @description 针对表【sys_user(用户信息表)】的数据库操作Mapper
 * @createDate 2024-12-25 20:35:45
 * @Entity com.moyu.system.sys.model.entity.SysUser
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}




