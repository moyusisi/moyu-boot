package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.system.model.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shisong
 * @description 针对表【sys_role(角色信息表)】的数据库操作Mapper
 * @createDate 2024-12-15 20:49:43
 * @Entity com.moyu.system.sys.model.entity.SysRole
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

}




