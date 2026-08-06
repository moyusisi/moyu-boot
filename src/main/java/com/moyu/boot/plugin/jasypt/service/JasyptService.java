package com.moyu.boot.plugin.jasypt.service;

import com.moyu.boot.plugin.jasypt.model.param.EncryptorParam;

/**
 * 配置加解密工具服务
 *
 * @author shisong
 * @since 2026-08-06
 */
public interface JasyptService {

    /**
     * 加密
     */
    String encrypt(EncryptorParam param);

    /**
     * 解密
     */
    String decrypt(EncryptorParam param);
}
