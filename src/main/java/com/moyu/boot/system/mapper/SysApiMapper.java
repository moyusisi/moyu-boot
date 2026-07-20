package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.moyu.boot.common.mybatis.annotation.DataPermission;
import com.moyu.boot.system.model.entity.SysApi;
import com.moyu.boot.system.model.vo.SysApiVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 针对表sys_api(接口信息表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2026-07-20
 */
@Mapper
public interface SysApiMapper extends BaseMapper<SysApi> {

    /**
     * 这是一个使用数据权限的例子，也是一个使用使用 Wrapper 自定义 SQL 的例子
     * <a href="https://baomidou.com/guides/wrapper/#%E4%BD%BF%E7%94%A8-wrapper-%E8%87%AA%E5%AE%9A%E4%B9%89-sql">参考这里</a>
     */
    @DataPermission(userColumn = "create_by")
    @Select("SELECT * FROM sys_api ${ew.customSqlSegment} LIMIT 5")
    List<SysApiVO> selectLimit(@Param(Constants.WRAPPER) Wrapper<SysApiVO> wrapper);
}

