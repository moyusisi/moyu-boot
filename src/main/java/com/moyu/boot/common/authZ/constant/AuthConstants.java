package com.moyu.boot.common.authZ.constant;

/**
 * 认证鉴权相关常量
 *
 * @author moyusisi
 * @since 2026-07-10
 */
public interface AuthConstants {
    /**
     * 令牌key
     */
    String TOKEN_NAME = "Authorization";
    /**
     * 令牌前缀
     */
    String TOKEN_PREFIX = "Bearer";

    /**
     * 登陆用户key
     */
    String LOGIN_USER = "loginUser";

    /**
     * ROOT角色编码
     */
    String ROOT_ROLE = "ROOT";
    /**
     * 角色前缀，用于区分 authorities 角色和权限， ROLE_* 角色 、无前缀的是权限
     */
    String ROLE_PREFIX = "ROLE_";
}
