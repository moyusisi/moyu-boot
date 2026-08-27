package com.moyu.boot.system.mapper;

import com.moyu.boot.system.model.entity.SysOrg;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 针对表sys_org(组织机构表)的数据库操作Mapper
 *
 * @author shisong
 * @since 2024-11-26 09:55:33
 */
@Mapper
public interface SysOrgMapper extends BaseMapper<SysOrg> {

    /**
     * 查询下属组织机构code列表(包含本身)
     */
    @Select("SELECT * FROM sys_org WHERE code = #{orgCode} OR find_in_set(#{orgCode}, org_path)")
    List<SysOrg> selectChildren(@Param("orgCode") String orgCode);
}




