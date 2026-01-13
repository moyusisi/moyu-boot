package com.moyu.boot.common.security.handler;


import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 自定义未认证异常处理，访问需要认证的资源(请求路径配置了authenticated())时却无凭证则触发(在filter层处理)
 * <p>
 * AuthenticationFailureHandler接口的实现类是AuthenticationEntryPointFailureHandler，
 * 它通过AuthenticationEntryPoint进行处理
 * <a href="https://blog.csdn.net/weixin_43831002/article/details/126131233">(参考)</a>
 *
 * @author shisong
 * @since 2025-01-05
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 认证失败直接返回json数据告诉前端
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 未认证时默认返回
        Result<?> result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED);
        try {
            // 校验是否已经登录，如果未登录则抛出 `NotLoginException` 异常
            StpUtil.checkLogin();
        } catch (NotLoginException nle) {
            // 处理登录异常，区分未认证的具体场景
            result = handleNotLogin(nle);
            logNotLogin(request);
        }
        String responseBody = new ObjectMapper().writeValueAsString(result);
        log.info("Security层，访问{}未认证异常，处理返回:{}", request.getRequestURI(), responseBody);
        response.getWriter().print(responseBody);
    }

    // 统一处理未认证不同场景的异常
    private Result<?> handleNotLogin(NotLoginException nle) {
        // 返回结果
        Result<?> result = null;
        // 未登录的具体细分
        switch (nle.getType()) {
            // 未能读取到有效 token
            case NotLoginException.NOT_TOKEN:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED);
                break;
            // token 无效
            case NotLoginException.INVALID_TOKEN:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED);
                break;
            // token 已过期
            case NotLoginException.TOKEN_TIMEOUT:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED);
                break;
            // token 已被顶下线
            case NotLoginException.BE_REPLACED:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_REPLACED);
                break;
            // token 已被踢下线
            case NotLoginException.KICK_OUT:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_KICKOUT);
                break;
            // token 已被冻结
            case NotLoginException.TOKEN_FREEZE:
                result = new Result<>(ResultCodeEnum.ACCOUNT_FROZEN);
                break;
            // 未按照指定前缀提交 token
            case NotLoginException.NO_PREFIX:
                result = new Result<>(ResultCodeEnum.ACCESS_UNAUTHORIZED);
                break;
            // 当前会话未登录
            default:
                result = new Result<>(ResultCodeEnum.ACCESS_UNAUTHORIZED);
                break;
        }
        return result;
    }

    /**
     * 记录未认证访问的信息
     */
    private void logNotLogin(HttpServletRequest request) {
        String ip = ServletUtil.getClientIP(request);
        if (ObjectUtil.isNotEmpty(ip)) {
            log.info("From Ip:{}, User-Agent:{}", ip, ServletUtil.getHeaderIgnoreCase(request, "User-Agent"));
        }
    }
}