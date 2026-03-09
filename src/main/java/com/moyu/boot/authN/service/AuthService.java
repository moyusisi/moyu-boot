package com.moyu.boot.authN.service;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import com.moyu.boot.authN.model.param.UserLoginParam;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
    private UserDetailsService userDetailsService;

    @Resource
    private TokenService tokenService;

    @Value("${custom.security.sm4Key:KeyMustBe16Size.}")
    private String sm4Key;

    /**
     * 用户登陆
     *
     * @return token
     */
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
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(username);
        // 检查状态
        if (!loginUser.isEnabled()) {
            // 账户不可用、已作废
            throw new BusinessException(ResultCodeEnum.USER_ACCOUNT_DISABLED);
        }
        // encryptPwd
        String encryptPwd = SmUtil.sm4(sm4Key.getBytes(StandardCharsets.UTF_8)).encryptHex(password);
        // 对比密码
        if (!Objects.equals(loginUser.getPassword(), encryptPwd)) {
            // 用户名或密码错误
            throw new BusinessException(ResultCodeEnum.USER_PASSWORD_ERROR);
        }
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
