package com.moyu.boot.common.security.service;


import com.moyu.boot.common.security.model.LoginUser;
import org.springframework.security.core.Authentication;

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
     * jwt秘钥，至少256-bit(32-byte)，如：7nPXLm0zLVdqKM5QTb03ahcRiWzoC2UC
     * 可通过 RandomUtil.randomString(32) 生产
     */
    String TOKEN_SECRET = "7nPXLm0zLVdqKM5QTb03ahcRiWzoC2UC";

    /**
     * 生成token
     */
    String generateToken(LoginUser loginUser);

    /**
     * 更新token(原有的置为失效，然后新生成)
     */
    String refreshToken(LoginUser loginUser);

    /**
     * 解析 Token 获取认证信息(Spring Security 的核心组件)
     */
    Authentication parseToken();

    /**
     * 置 Token 失效
     */
    default void invalidateToken(String token) {
        // 默认空实现，不做操作
    }
}
