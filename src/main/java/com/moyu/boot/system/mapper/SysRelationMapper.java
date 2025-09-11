package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.system.model.entity.SysRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shisong
 * @description 针对表【sys_relation(用户角色权限关系表)】的数据库操作Mapper
 * @createDate 2024-12-16 21:15:35
 * @Entity com.moyu.system.sys.model.entity.SysRelation
 */
@Mapper
public interface SysRelationMapper extends BaseMapper<SysRelation> {

}




