package com.moyu.boot.support.sysConfig.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moyu.boot.support.sysConfig.model.entity.SysConfig;
import com.moyu.boot.support.sysConfig.model.param.SysConfigParam;
import com.moyu.boot.support.sysConfig.model.vo.SysConfigVO;
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

