package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.moyu.boot.common.mybatis.annotation.DataPermission;
import com.moyu.boot.system.model.entity.SysOrg;
import com.moyu.boot.system.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 针对表sys_user(用户信息)的数据库操作Mapper
 *
 * @author shisong
 * @since 2024-12-25 20:35:45
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询本部门所有的用户
     */
    @DataPermission(orgColumn = "code")
    @Select("SELECT * FROM sys_user LIMIT 3")
    List<SysOrg> selectDeptUser(@Param(Constants.WRAPPER) Wrapper<SysOrg> wrapper);
}
