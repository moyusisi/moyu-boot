package com.moyu.boot.authN.service;


import com.moyu.boot.authN.model.param.UserLoginParam;

/**
 * 登陆服务类
 *
 * @author shisong
 * @since 2025-01-22
 */
public interface AuthService {

    /**
     * 用户登陆
     *
     * @return token
     */
    String login(UserLoginParam param);

    /**
     * 注销登录
     */
    void logout();

}
