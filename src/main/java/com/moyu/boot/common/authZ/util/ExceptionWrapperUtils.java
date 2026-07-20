package com.moyu.boot.common.authZ.util;

import cn.dev33.satoken.exception.NotLoginException;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.model.Result;

/**
 * 异常处理工具
 *
 * @author shisong
 * @since 2026-03-10
 */
public class ExceptionWrapperUtils {

    /**
     * 处理未登录异常的不同场景
     */
    public static Result<?> handleNotLogin(NotLoginException nle) {
        // 返回结果
        Result<?> result = null;
        // 未登录的具体细分
        switch (nle.getType()) {
            // 未能读取到有效 token
            case NotLoginException.NOT_TOKEN:
                result = new Result<>(ResultCodeEnum.ACCESS_UNAUTHORIZED, NotLoginException.NOT_TOKEN_MESSAGE);
                break;
            // token 无效
            case NotLoginException.INVALID_TOKEN:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED, NotLoginException.INVALID_TOKEN_MESSAGE);
                break;
            // token 已过期
            case NotLoginException.TOKEN_TIMEOUT:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED, NotLoginException.TOKEN_TIMEOUT_MESSAGE);
                break;
            // token 已被顶下线
            case NotLoginException.BE_REPLACED:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED, NotLoginException.BE_REPLACED_MESSAGE);
                break;
            // token 已被踢下线
            case NotLoginException.KICK_OUT:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED, NotLoginException.KICK_OUT_MESSAGE);
                break;
            // token 已被冻结（超时未访问，activeTimeout）
            case NotLoginException.TOKEN_FREEZE:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED);
                break;
            // 未按照指定前缀提交 token
            case NotLoginException.NO_PREFIX:
                result = new Result<>(ResultCodeEnum.ACCESS_UNAUTHORIZED, NotLoginException.NO_PREFIX_MESSAGE);
                break;
            // 当前会话未登录
            default:
                result = new Result<>(ResultCodeEnum.ACCESS_UNAUTHORIZED);
                break;
        }
        return result;
    }
}
