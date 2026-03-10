package com.moyu.boot.common.authZ.service;

/**
 * 加密器服务
 *
 * @author shisong
 * @since 2026-03-10
 */
public interface PasswordEncoder {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
