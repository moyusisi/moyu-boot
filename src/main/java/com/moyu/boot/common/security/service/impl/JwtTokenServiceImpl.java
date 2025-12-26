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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
//                .setExtra("orgCode", loginUser.getOrgCode())
//                .setExtra("groupCode", loginUser.getGroupCode())
//                .setExtra("groupOrgCode", loginUser.getGroupOrgCode())
//                .setExtra("roles", loginUser.getRoles())
//                .setExtra("perms", loginUser.getPerms())
//                .setExtra("permScopeMap", loginUser.getPermScopeMap())
        );
        // 账户相关的信息缓存到Account-Session中(Simple模式才支持session)
        StpUtil.getSession().set("name", loginUser.getName());
        // 将登录用户信息缓存到Token-Session中
        StpUtil.getTokenSession().set("loginUser", loginUser);
        return StpUtil.getTokenValue();
    }

    @Override
    public String refreshToken(LoginUser loginUser) {
        // jwt无法将原token置为失效，仅可以生成新token（置为失效只需前端删掉原token即可）
        return generateToken(loginUser);
    }

    @Override
    public Authentication parseToken() {
        // 获取 jwt Token 的扩展参数, 只在jwt模式下生效
//        Object roles = StpUtil.getExtra("roles");
//        Object perms = StpUtil.getExtra("perms");
//        Object permScopeMap = StpUtil.getExtra("permScopeMap");
//        // 构造当前登录用户信息
//        LoginUser loginUser = LoginUser.builder().enabled(true)
//                .username((String) StpUtil.getExtra("username"))
//                .orgCode((String) StpUtil.getExtra("orgCode"))
//                .groupCode((String) StpUtil.getExtra("groupCode"))
//                .groupOrgCode((String) StpUtil.getExtra("groupOrgCode"))
//                .roles(ObjectUtil.isEmpty(roles) ? new HashSet<>() : new HashSet<>((List<String>) roles))
//                .perms(ObjectUtil.isEmpty(perms) ? new HashSet<>() : new HashSet<>((List<String>) perms))
//                .permScopeMap(ObjectUtil.isEmpty(permScopeMap) ? new HashMap<>() : (Map<String, LoginUser.DataScopeInfo>) permScopeMap)
//                .build();
        // 从会话中获取缓存的数据(Simple模式才支持session，无session时必须从jwt中解析loginUser)
        LoginUser loginUser = (LoginUser) StpUtil.getTokenSession().get("loginUser");
        // 初始化authorities后才可使用springSecurity鉴权
        loginUser.initAuthorities();
        // 根据登录用户信息生成认证信息
        return new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
    }
}
