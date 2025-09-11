package com.moyu.boot.common.security.service;


import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.util.JwtUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shisong
 * @since 2025-01-24
 */
public class TokenService {

    /**
     * 生成token
     */
    public static String generateToken(LoginUser loginUser) {

        return JwtUtils.createToken(loginUser);
    }


    /**
     * 从http请求中获取token
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        // 从header中获取token
        String token = request.getHeader("Authorization");
        if (ObjectUtil.isNotEmpty(token) && token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");
        }
        return token;
    }


    /**
     * 从http请求中获取token
     */
    public static LoginUser getLoginUserByToken(String token) {
        // 校验token，错误则抛异常
        Boolean valid = JwtUtils.verifyToken(token);
        // 直接从token解析jwt获取用户
        LoginUser loginUser = JwtUtils.getLoginUserFromToken(token);
        // 根据token获取tokenId，然后再从缓存中获取登录用户
        // userCache.get(JwtUtils.getId(token))

        // 用户不存在则表示登录已过期

        // 用户存在, 无痛刷新缓存，在登录过期前活动的用户自动刷新缓存时间

        // 返回当前登陆用户
        return loginUser;
    }

}
