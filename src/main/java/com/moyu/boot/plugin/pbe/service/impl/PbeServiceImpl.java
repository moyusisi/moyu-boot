package com.moyu.boot.plugin.pbe.service.impl;

import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.plugin.pbe.model.param.PbeParam;
import com.moyu.boot.plugin.pbe.service.PbeService;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.exceptions.EncryptionInitializationException;
import org.springframework.stereotype.Service;

/**
 * @author shisong
 * @since 2026-08-06
 */
@Slf4j
@Service
public class PbeServiceImpl implements PbeService {


    @Override
    public String encrypt(PbeParam param) {
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
        String encryptedText = null;
        try {
            encryptedText = encryptor.encrypt(param.getPlainText());
        } catch (EncryptionInitializationException e) {
            // 初始化阶段就失败，还没真正加解密，如密码为空、算法参数不合法等
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "");
        } catch (Exception e) {
            // 解密失败：密码错误、salt不对、算法不匹配等或其他问题
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR.getCode(), "加密失败");
        }
        return encryptedText;
    }

    @Override
    public String decrypt(PbeParam param) {
        // 编码配置
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setPassword(param.getSecretKey());
        config.setAlgorithm(param.getAlgorithm());
        // 加密器
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setConfig(config);
        // 解密
        String plainText = null;
        try {
            plainText = encryptor.decrypt(param.getEncryptedText());
        } catch (EncryptionInitializationException e) {
            // 初始化阶段就失败，还没真正加解密，如密码为空、算法参数不合法等
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "");
        } catch (Exception e) {
            // 解密失败：密码错误、salt不对、算法不匹配等或其他问题
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR.getCode(), "解密失败");
        }
        return plainText;
    }
}
