package com.moyu.boot.auth.service;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.moyu.boot.auth.model.param.UserLoginParam;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 登陆服务类
 *
 * @author shisong
 * @since 2025-01-22
 */
@Slf4j
@Service
public class AuthService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private TokenService tokenService;

    /**
     * 用户登陆
     *
     * @return token
     */
    public String login(UserLoginParam param) {
        // 前面的校验
        String username = param.getAccount();
        String password = param.getPassword();
        // 认证令牌
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        // 认证，会调用 UserDetailsServiceImpl#loadUserByUsername
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        // 放到Security上下文中(认证失败会抛出AuthenticationException，不会往下走)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 认证成功获取已认证的用户主体
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        return tokenService.generateToken(loginUser);
    }

    /**
     * 注销登录
     */
    public void logout() {
        String token = StpUtil.getTokenValue();
        if (StrUtil.isNotEmpty(token)) {
            // 置token失效
            tokenService.invalidateToken(token);
            // 清除Security上下文
            SecurityContextHolder.clearContext();
        }
    }

}
