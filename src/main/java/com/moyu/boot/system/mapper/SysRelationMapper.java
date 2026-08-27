package com.moyu.boot.system.mapper;

import com.moyu.boot.system.model.entity.SysRelation;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表sys_relation(用户角色权限关系表)的数据库操作Mapper
 *
 * @author shisong
 * @since 2024-12-25 20:35:45
 */
@Mapper
public interface SysRelationMapper extends BaseMapper<SysRelation> {

}




