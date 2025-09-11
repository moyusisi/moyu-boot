package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.moyu.boot.common.mybatis.annotation.DataPermission;
import com.moyu.boot.system.model.entity.SysOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author shisong
 * @description 针对表【sys_org(组织机构表)】的数据库操作Mapper
 * @createDate 2024-11-26 09:55:33
 * @Entity com.moyu.system.modular.org.domain.SysOrg
 */
@Mapper
public interface SysOrgMapper extends BaseMapper<SysOrg> {

    /**
     * 查询下属组织机构code列表(包含本身)
     */
    @Select("SELECT * FROM sys_org WHERE code = #{orgCode} OR find_in_set(#{orgCode}, org_path)")
    List<SysOrg> selectChildren(@Param("orgCode") String orgCode);

    /**
     * 这是一个使用数据权限的例子，也是一个使用使用 Wrapper 自定义 SQL 的例子
     * <a href="https://baomidou.com/guides/wrapper/#%E4%BD%BF%E7%94%A8-wrapper-%E8%87%AA%E5%AE%9A%E4%B9%89-sql">参考这里</a>
     */
    @DataPermission(orgColumn = "code")
    @Select("SELECT * FROM sys_org ${ew.customSqlSegment} LIMIT 3")
    List<SysOrg> selectAll(@Param(Constants.WRAPPER) Wrapper<SysOrg> wrapper);

}




