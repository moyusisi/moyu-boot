package com.moyu.boot.common.security.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * redis 令牌服务类，需要使用redis
 *
 * @author shisong
 * @since 2025-11-11
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "custom.security.session.type", havingValue = "redis")
public class RedisTokenServiceImpl implements TokenService {

    @Override
    public String generateToken(LoginUser loginUser) {
        // 登录
        StpUtil.login(loginUser.getUsername(), new SaLoginParameter()
                // 是否在登录后将 Token 写入到响应头
                .setIsWriteHeader(true)
        );
        // 账户相关的信息缓存到Account-Session中
        StpUtil.getSession().set("name", loginUser.getName());
        // 将登录用户信息缓存到Token-Session中
        StpUtil.getTokenSession().set("loginUser", loginUser);
        return StpUtil.getTokenInfo().getTokenValue();
    }

    @Override
    public void switchUser(LoginUser loginUser) {
        // 账户相关的信息缓存到Account-Session中
        StpUtil.getSession().set("name", loginUser.getName());
        // 将登录用户信息缓存到Token-Session中
        StpUtil.getTokenSession().set("loginUser", loginUser);
    }

    @Override
    public void invalidateToken(String token) {
        StpUtil.logoutByTokenValue(token);
    }
}
