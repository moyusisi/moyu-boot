package com.moyu.boot.plugin.ThirdPartyApp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.plugin.ThirdPartyApp.model.entity.ThirdPartyApp;
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

