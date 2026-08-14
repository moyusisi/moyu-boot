package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.system.model.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表sys_config(系统配置表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2026-05-28
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
}

