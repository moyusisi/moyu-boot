package com.moyu.boot.plugin.ThirdPartyApp.mapper;

import com.moyu.boot.plugin.ThirdPartyApp.model.entity.ThirdPartyApp;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表third_party_app(三方应用表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2026-08-17
 */
@Mapper
public interface ThirdPartyAppMapper extends BaseMapper<ThirdPartyApp> {

}

