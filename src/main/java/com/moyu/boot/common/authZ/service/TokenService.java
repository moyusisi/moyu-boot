package com.moyu.boot.common.authZ.service;

import com.moyu.boot.common.authZ.model.LoginUser;

/**
 * 令牌服务，有多种实现
 *
 * @author shisong
 * @since 2025-01-24
 */
public interface TokenService {

    /**
     * 令牌key
     */
    String TOKEN_NAME = "Authorization";
    /**
     * 令牌前缀
     */
    String TOKEN_PREFIX = "Bearer";

    /**
     * 生成token
     */
    String generateToken(LoginUser loginUser);

    /**
     * token不变，更换loginUser TODO
     */
    void switchUser(LoginUser loginUser);

    /**
     * 置 Token 失效
     */
    default void invalidateToken(String token) {
        // 默认空实现，不做操作
    }
}
