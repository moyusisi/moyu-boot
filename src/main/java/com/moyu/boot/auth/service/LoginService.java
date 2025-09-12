package com.moyu.boot.auth.service;


import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import com.moyu.boot.auth.model.param.UserLoginParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class LoginService {

    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * 用户登陆
     *
     * @return token
     */
    public String login(UserLoginParam param) {
        // 前面的校验
        String username = param.getAccount();
        String password = param.getPassword();

        // 认证
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            // 该方法会在去调用 UserDetailsServiceImpl#loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            if (e instanceof UsernameNotFoundException) {
                throw new BusinessException(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
            } else if (e instanceof BadCredentialsException) {
                throw new BusinessException(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
            } else if (e instanceof LockedException) {
                throw new BusinessException(HttpStatus.UNAUTHORIZED.value(), "用户账号已锁定");
            } else if (e instanceof DisabledException) {
                throw new BusinessException(HttpStatus.UNAUTHORIZED.value(), "用户暂不可用");
            } else {
                log.error("登陆失败", e);
                throw new BusinessException(HttpStatus.UNAUTHORIZED.value(), "登陆失败");
            }
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        return TokenService.generateToken(loginUser);
    }
}
