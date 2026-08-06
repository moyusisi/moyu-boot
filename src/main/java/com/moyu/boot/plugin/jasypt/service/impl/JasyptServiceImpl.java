package com.moyu.boot.plugin.jasypt.service.impl;

import com.moyu.boot.plugin.jasypt.model.param.EncryptorParam;
import com.moyu.boot.plugin.jasypt.service.JasyptService;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.springframework.stereotype.Service;

/**
 * @author shisong
 * @since 2026-08-06
 */
@Slf4j
@Service
public class JasyptServiceImpl implements JasyptService {


    @Override
    public String encrypt(EncryptorParam param) {
        // 编码配置
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setPassword(param.getSecretKey());
        config.setAlgorithm(param.getAlgorithm());
//        // 下面这些都是默认配置
//        config.setKeyObtentionIterations("1000");
//        config.setPoolSize("1");
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
//        config.setStringOutputType("base64");

        // 加密器
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setConfig(config);
        // 加密
        return encryptor.encrypt(param.getPlainText());
    }

    @Override
    public String decrypt(EncryptorParam param) {
        // 编码配置
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setPassword(param.getSecretKey());
        config.setAlgorithm(param.getAlgorithm());
        // 加密器
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setConfig(config);
        // 解密
        return encryptor.decrypt(param.getEncryptedText());
    }
}
