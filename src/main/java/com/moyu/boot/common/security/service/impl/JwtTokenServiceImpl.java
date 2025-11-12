package com.moyu.boot.common.security.service.impl;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

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
                // 记录在 Token 上的扩展参数，只在 jwt 模式下生效
                .setExtra("username", loginUser.getUsername())
                .setExtra("orgCode", loginUser.getOrgCode())
                .setExtra("groupCode", loginUser.getGroupCode())
                .setExtra("roles", loginUser.getRoles())
                .setExtra("perms", loginUser.getPerms())
                .setExtra("dataScope", loginUser.getDataScope())
                .setExtra("scopes", loginUser.getScopes())
        );
        return StpUtil.getTokenValue();
    }

    @Override
    public Authentication parseToken() {
        // 获取 jwt Token 的扩展参数, 只在jwt模式下生效
        Object roles = StpUtil.getExtra("roles");
        Object perms = StpUtil.getExtra("perms");
        Object dataScope = StpUtil.getExtra("dataScope");
        Object scopes = StpUtil.getExtra("scopes");
        // 构造当前登录用户信息
        LoginUser loginUser = LoginUser.builder().enabled(true)
                .username((String) StpUtil.getExtra("username"))
                .orgCode((String) StpUtil.getExtra("orgCode"))
                .groupCode((String) StpUtil.getExtra("groupCode"))
                .roles(ObjectUtil.isEmpty(roles) ? new HashSet<>() : new HashSet<>((List<String>) roles))
                .perms(ObjectUtil.isEmpty(perms) ? new HashSet<>() : new HashSet<>((List<String>) perms))
                .dataScope(ObjectUtil.isEmpty(dataScope) ? null : ((Number) dataScope).intValue())
                .scopes(ObjectUtil.isEmpty(scopes) ? new HashSet<>() : new HashSet<>((List<String>) scopes))
                .build();
        // 初始化authorities后才可使用springSecurity鉴权
        loginUser.initAuthorities();
        // 根据登录用户信息生成认证信息
        return new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
    }
}
