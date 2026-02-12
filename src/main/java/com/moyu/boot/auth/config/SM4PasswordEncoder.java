package com.moyu.boot.auth.config;

import cn.hutool.crypto.SmUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author shisong
 * @since 2026-02-12
 */
public class SM4PasswordEncoder implements PasswordEncoder {

    // key长度必须为16
    private static final String SM4KEY = "KeyMustBe16Size.";

    @Override
    public String encode(CharSequence rawPassword) {
        // 自定义密钥
        byte[] key = SM4KEY.getBytes(StandardCharsets.UTF_8);
        return SmUtil.sm4(key).encryptHex(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] key = SM4KEY.getBytes(StandardCharsets.UTF_8);
        return Objects.equals(rawPassword.toString(),
                SmUtil.sm4(key).decryptStr(encodedPassword, StandardCharsets.UTF_8));
    }
}

