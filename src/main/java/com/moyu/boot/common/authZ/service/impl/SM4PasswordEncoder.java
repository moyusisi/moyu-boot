package com.moyu.boot.common.authZ.service.impl;

import cn.hutool.crypto.SmUtil;
import com.moyu.boot.common.authZ.config.AuthProperties;
import com.moyu.boot.common.authZ.service.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 密码编码器sm4算法实现
 *
 * @author shisong
 * @since 2026-02-12
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "custom.auth.crypto-type", havingValue = "sm4", matchIfMissing = true)
public class SM4PasswordEncoder implements PasswordEncoder {

    @Resource
    private AuthProperties properties;

    @Override
    public String encode(CharSequence rawPassword) {
        // 自定义密钥
        byte[] key = properties.getSm4Key().getBytes(StandardCharsets.UTF_8);
        return SmUtil.sm4(key).encryptHex(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] key = properties.getSm4Key().getBytes(StandardCharsets.UTF_8);
        String plain = SmUtil.sm4(key).decryptStr(encodedPassword, StandardCharsets.UTF_8);
        return Objects.equals(rawPassword, plain);
    }
}