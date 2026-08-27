package com.moyu.boot.system.mapper;

import com.moyu.boot.system.model.entity.SysLog;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表sys_log(系统日志表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {

}

