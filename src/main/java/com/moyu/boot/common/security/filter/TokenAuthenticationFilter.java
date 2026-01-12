package com.moyu.boot.common.security.filter;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.common.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 令牌认证过滤器，支持多种tokenService实现。
 * Filter内抓「Token 校验异常」，AuthenticationEntryPoint 抓「无 Token 异常」，全局处理器抓「漏网异常」，三者结合覆盖所有场景。
 *
 * @author shisong
 * @since 2024-01-04
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Token 管理器
     */
    private final TokenService tokenService;

    public TokenAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1.校验当前客户端是否已经登录，如果未登录则抛出 `NotLoginException` 异常
            StpUtil.checkLogin();
        } catch (NotLoginException nle) {
            // 4.处理登录异常(无token的情况直接处理了,AuthenticationEntryPoint中理应都有Authentication)
            handleException(request, response, nle);
        }
        // 2.若已登录，上下文中设置认证信息
        if (StpUtil.isLogin()) {
            // 避免覆盖设置
            if (ObjectUtil.isEmpty(SecurityContextHolder.getContext().getAuthentication())) {
                // 从token中解析出认证信息
                Authentication authentication = tokenService.parseToken();
                // 放到Security上下文中
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 3. 继续执行后续的过滤器链
        filterChain.doFilter(request, response);
    }

    // 统一异常响应处理
    private void handleException(HttpServletRequest request, HttpServletResponse response, NotLoginException nle) throws IOException {
        // 认证失败直接返回json数据告诉前端
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 返回结果
        Result<?> result = null;
        // 未登录的具体细分
        switch (nle.getType()) {
            // 未能读取到有效 token
            case NotLoginException.NOT_TOKEN:
                result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED);
                logAuthAbnormal(request);
                break;
            // token 无效
            case NotLoginException.INVALID_TOKEN:
                result = new Result<>(ResultCodeEnum.ACCESS_UNAUTHORIZED);
                logAuthAbnormal(request);
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
                logAuthAbnormal(request);
                break;
            // 当前会话未登录
            default:
                result = new Result<>(ResultCodeEnum.ACCESS_PERMISSION_EXCEPTION);
                logAuthAbnormal(request);
                break;
        }

        String responseBody = new ObjectMapper().writeValueAsString(result);
        log.info("Filter层，访问{}认证异常，处理返回:{}", request.getRequestURI(), responseBody);
        // 写入响应
        response.getWriter().print(responseBody);
    }

    /**
     * 记录非正常情况认证失败的信息
     */
    private void logAuthAbnormal(HttpServletRequest request) {
        String ip = ServletUtil.getClientIP(request);
        if (ObjectUtil.isNotEmpty(ip)) {
            log.info("From Ip:{}, User-Agent:{}", ip, ServletUtil.getHeaderIgnoreCase(request, "User-Agent"));
        }
    }

}
