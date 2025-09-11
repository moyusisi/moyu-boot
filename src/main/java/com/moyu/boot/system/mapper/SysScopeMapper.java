package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.system.model.entity.SysScope;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shisong
 * @description 针对表【sys_scope(数据权限分组表)】的数据库操作Mapper
 * @createDate 2025-02-27 10:19:59
 * @Entity com.moyu.system.sys.model.entity.SysScope
 */
@Mapper
public interface SysScopeMapper extends BaseMapper<SysScope> {

}




