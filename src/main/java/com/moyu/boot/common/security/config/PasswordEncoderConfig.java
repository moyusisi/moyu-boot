package com.moyu.boot.common.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author shisong
 * @since 2026-02-12
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 密码编码器
     *
     * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new SM4PasswordEncoder();
    }
}
