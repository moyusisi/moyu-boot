package com.moyu.boot.plugin.thirdPartyApi.mapper;

import com.moyu.boot.plugin.thirdPartyApi.model.entity.ThirdPartyApi;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表third_party_api(三方集成接口表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2026-08-06
 */
@Mapper
public interface ThirdPartyApiMapper extends BaseMapper<ThirdPartyApi> {

}

