package com.moyu.boot.auth.controller;


import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.auth.model.param.UserLoginParam;
import com.moyu.boot.auth.service.LoginService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 登陆控制器
 *
 * @author shisong
 * @since 2025-01-22
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private LoginService loginService;

    /**
     * 用户登陆
     */
    @PostMapping("/login")
    public Result<String> userLogin(@Validated UserLoginParam loginParam) {
        String token = loginService.login(loginParam);
        return Result.success(token);
    }

    /**
     * 用户注销登陆(若不自定义，则需要在springSecurity中配置)
     */
    @PostMapping("/logout")
    public Result<String> userLogout(UserLoginParam loginParam) {
        return Result.success();
    }

}
