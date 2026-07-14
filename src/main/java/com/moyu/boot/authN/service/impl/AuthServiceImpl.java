package com.moyu.boot.authN.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.moyu.boot.authN.model.param.UserLoginParam;
import com.moyu.boot.authN.service.AuthService;
import com.moyu.boot.common.authZ.model.LoginUser;
import com.moyu.boot.common.authZ.service.TokenService;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 登录认证服务类
 *
 * @author shisong
 * @since 2026-03-09
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private TokenService tokenService;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登陆
     *
     * @return token
     */
    @Override
    public String login(UserLoginParam param) {
        // 登录参数
        String username = param.getAccount();
        String password = param.getPassword();
        // 检查封禁(checkDisable会抛出DisableServiceException)
        if (StpUtil.isDisable(username)) {
            // 账户被冻结(临时冻结)
            throw new BusinessException(ResultCodeEnum.USER_ACCOUNT_FROZEN);
        }
        // 认证令牌（Security方式认证）
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
//        // 认证，会调用 UserDetailsServiceImpl#loadUserByUsername，认证失败会抛出AuthenticationException
//        Authentication authentication = authenticationManager.authenticate(authenticationToken);
//        // 放到Security上下文中(，不会往下走)
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        // 认证成功获取已认证的用户主体
//        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 普通方式认证
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(username);
        // 检查状态
        if (!loginUser.isEnabled()) {
            // 账户已停用、已作废
            throw new BusinessException(ResultCodeEnum.USER_ACCOUNT_DISABLED);
        }
        // 对比密码
        if (!passwordEncoder.matches(password, loginUser.getPassword())) {
            // 用户名或密码错误
            throw new BusinessException(ResultCodeEnum.USER_PASSWORD_ERROR);
        }
        // 生成token
        return tokenService.generateToken(loginUser);
    }

    /**
     * 注销登录
     */
    @Override
    public void logout() {
        String token = StpUtil.getTokenValue();
        if (StrUtil.isNotEmpty(token)) {
            // 置token失效
            tokenService.invalidateToken(token);
        }
    }
}
