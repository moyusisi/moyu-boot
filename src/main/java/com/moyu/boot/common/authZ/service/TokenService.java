package com.moyu.boot.common.authZ.service;

import cn.dev33.satoken.stp.StpUtil;
import com.moyu.boot.common.authZ.model.LoginUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
     * 生成token
     */
    String generateToken(LoginUser loginUser);

    /**
     * token不变，更换loginUser TODO
     */
    void switchUser(LoginUser loginUser);

    /**
     * 解析 Token 获取认证信息(Spring Security 的核心组件)
     */
    default Authentication parseToken() {
        // 从会话中获取缓存的数据
        LoginUser loginUser = (LoginUser) StpUtil.getTokenSession().get("loginUser");
        // 初始化authorities后才可使用springSecurity鉴权
        loginUser.initAuthorities();
        // 根据登录用户信息生成认证信息
        return new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
    }

    /**
     * 置 Token 失效
     */
    default void invalidateToken(String token) {
        // 默认空实现，不做操作
    }
}
