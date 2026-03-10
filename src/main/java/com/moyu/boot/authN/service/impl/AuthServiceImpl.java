package com.moyu.boot.authN.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.moyu.boot.authN.model.param.UserLoginParam;
import com.moyu.boot.authN.service.AuthService;
import com.moyu.boot.authN.service.UserDetailsService;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.authZ.model.LoginUser;
import com.moyu.boot.common.authZ.service.PasswordEncoder;
import com.moyu.boot.common.authZ.service.TokenService;
import com.moyu.boot.system.model.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
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
        // 检查封禁
        if (StpUtil.isDisable(username)) {
            // 账户被冻结(临时冻结)
            throw new BusinessException(ResultCodeEnum.USER_ACCOUNT_FROZEN);
        }
        // 通过account获取用户
        SysUser sysUser = userDetailsService.loadUserByUsername(username);
        // 检查状态
        if (sysUser.getStatus() != 0) {
            // 账户已停用、已作废
            throw new BusinessException(ResultCodeEnum.USER_ACCOUNT_DISABLED);
        }
        // 对比密码
        if (!passwordEncoder.matches(password, sysUser.getPassword())) {
            // 用户名或密码错误
            throw new BusinessException(ResultCodeEnum.USER_PASSWORD_ERROR);
        }
        // 构造登录用户
        LoginUser loginUser = userDetailsService.buildUserDetails(sysUser);
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
