package com.moyu.boot.common.security.service.impl;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
                // extra只在 jwt 模式下生效，传入的 extra 参数将被忽略
                .setExtra("username", loginUser.getUsername())
        );
        // 账户相关的信息缓存到Account-Session中
        StpUtil.getSession().set("name", loginUser.getName());
        // 将登录用户信息缓存到Token-Session中
        StpUtil.getTokenSession().set("loginUser", loginUser);
        return StpUtil.getTokenValue();
    }

    @Override
    public String refreshToken(LoginUser loginUser) {
        // 置为失效
        invalidateToken(StpUtil.getTokenValue());
        // 重新生成
        return generateToken(loginUser);
    }

    @Override
    public Authentication parseToken() {
        // 从会话中获取缓存的数据
        LoginUser loginUser = (LoginUser) StpUtil.getTokenSession().get("loginUser");
        // 初始化authorities后才可使用springSecurity鉴权
        loginUser.initAuthorities();
        // 根据登录用户信息生成认证信息
        return new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
    }

    @Override
    public void invalidateToken(String token) {
        StpUtil.logoutByTokenValue(token);
    }
}
