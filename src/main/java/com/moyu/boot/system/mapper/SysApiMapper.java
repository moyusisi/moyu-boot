package com.moyu.boot.system.mapper;

import com.moyu.boot.system.model.entity.SysApi;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表sys_api(接口信息表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2026-07-20
 */
@Mapper
public interface SysApiMapper extends BaseMapper<SysApi> {

}

