package com.moyu.boot.common.authZ.service;

/**
 * 加密器
 *
 * @author shisong
 * @since 2026-07-15
 */
public interface PasswordEncoder {

    /**
     * 加密
     */
    String encode(CharSequence rawPassword);

    /**
     * 密码匹配返回true，否则false
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
