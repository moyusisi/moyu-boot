package com.moyu.boot.auth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器(修改、重置密码使用)
 *
 * @author shisong
 * @since 2024-12-27
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 加密比较器
     *
     * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
