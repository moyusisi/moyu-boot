package com.moyu.boot.plugin.pbe.service;

import com.moyu.boot.plugin.pbe.model.param.PbeParam;

/**
 * 配置加解密工具服务
 *
 * @author shisong
 * @since 2026-08-06
 */
public interface PbeService {

    /**
     * 加密
     */
    String encrypt(PbeParam param);

    /**
     * 解密
     */
    String decrypt(PbeParam param);
}
