package com.moyu.boot.common.security.service.impl;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * jwt 令牌服务类
 *
 * @author shisong
 * @since 2025-11-11
 */
@Service
@ConditionalOnProperty(value = "custom.security.session.type", havingValue = "jwt")
public class JwtTokenServiceImpl implements TokenService {

    @Override
    public String generateToken(LoginUser loginUser) {
        // 连缀写法追加多个
        StpUtil.login(loginUser.getUsername(), new SaLoginParameter()
                // 是否在登录后将 Token 写入到响应头
                .setIsWriteHeader(true)
                // extra只在 jwt 模式下生效, 在 Token 上记录扩展参数
                .setExtra("username", loginUser.getUsername())
        );
        // 账户相关的信息缓存到Account-Session中(Simple模式才支持session)
        StpUtil.getSession().set("name", loginUser.getName());
        // 将登录用户信息缓存到Token-Session中
        StpUtil.getTokenSession().set("loginUser", loginUser);
        return StpUtil.getTokenInfo().getTokenValue();
    }

    @Override
    public void switchUser(LoginUser loginUser) {
        // 账户相关的信息缓存到Account-Session中(Simple模式才支持session)
        StpUtil.getSession().set("name", loginUser.getName());
        // 将登录用户信息缓存到Token-Session中
        StpUtil.getTokenSession().set("loginUser", loginUser);
    }

}
