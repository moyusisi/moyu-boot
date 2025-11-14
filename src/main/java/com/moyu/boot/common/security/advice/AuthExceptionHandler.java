package com.moyu.boot.common.security.advice;


import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * HttpSecurity#exceptionHandling()处理认证异常(AuthenticationEntryPoint)和授权异常(AccessDeniedHandler)是在Filter层
 * 此处的异常处理类@ExceptionHandler仅处理RestController中的异常
 * 使用SpringSecurity的@PreAuthorize 或 SaToken的@SaCheckPermission 进行权限校验时，会被此Advice处理
 * <p>
 * 此处定义高优先级的Advice，仅处理Spring Security的认证异常和授权异常，避免被其他ExceptionHandler处理
 *
 * @author shisong
 * @since 2022-09-08
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
@RestControllerAdvice(annotations = RestController.class)
public class AuthExceptionHandler {

    // security的认证异常(AuthenticationException及子类)
    @ExceptionHandler(AuthenticationException.class)
    public Result<?> authenticationException(AuthenticationException e) {
        // 登录异常
        return new Result<>(ResultCodeEnum.USER_LOGIN_EXCEPTION);
    }

    // security的授权异常(AccessDeniedException及子类) 先于security AuthenticationEntryPoint 处理
    // sa权限认证的相关异常(SaTokenException的子类)也一起处理(注意要使用AOP模式，不要使用拦截器模式,否则无法打印入参)
    @ExceptionHandler({AccessDeniedException.class, NotLoginException.class, NotRoleException.class, NotPermissionException.class})
    public Result<?> accessDeniedException(HttpServletRequest request, Exception e) {
        log.warn("未授权访问：{}", request.getRequestURI());
        return new Result<>(ResultCodeEnum.ACCESS_UNAUTHORIZED);
    }
}
