package com.moyu.boot.common.security.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Security相关的常量
 *
 * @author shisong
 * @since 2024-12-27
 */
public interface SecurityConstants {

    /**
     * 超级管理员角色编码
     */
    String ROOT_ROLE = "ROOT";

    /**
     * token相关常量
     */
    class Token {
        /**
         * token在header中的标识(Authorization)
         */
        public static final String HEADER = "Authorization";
        /**
         * token令牌前缀
         */
        public static final String PREFIX = "Bearer ";
        /**
         * jwt秘钥，至少256-bit(32-byte)，如：7nPXLm0zLVdqKM5QTb03ahcRiWzoC2UC
         * RandomUtil.randomString(32)
         */
        public static final String SECRET = "7nPXLm0zLVdqKM5QTb03ahcRiWzoC2UC";
        /**
         * JWT token有效期(3天)
         */
        public static final int TOKEN_VALID_TIME = 60 * 60 * 24 * 3;
        /**
         * token过期的错误码
         */
        public static final int EXPIRED_ERROR_CODE = 5002;
    }

    /**
     * 无需校验的URL白名单
     * Arrays.asList 方法返回的是一个固定大小的 List ，不支持 add
     */
    List<String> WHITE_LIST = new ArrayList<>(Arrays.asList(
            // 登陆注册类请求
            "/api/login",
            "/api/register",
            "/api/captcha/**",

            // 认证授权请求
            "/api/auth/**",

            // 监控管理请求
            "/actuator/**",
            "/test/**",
            "/monitor/**",

            // 回调类
            "/callback/**",

            // 远程调用
            "/feign/**"
    ));
}
