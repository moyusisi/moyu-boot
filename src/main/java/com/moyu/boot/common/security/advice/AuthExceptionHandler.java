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
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * HttpSecurity#exceptionHandling()处理认证异常(AuthenticationEntryPoint)和授权异常(AccessDeniedHandler)是在Filter层，
 * 当存在全局异常处理类@ExceptionHandler(Exception.class)时会先于Filter层处理，导致HttpSecurity中的配置无法处理
 * <a href="https://developer.aliyun.com/article/1477570">参考这里</a>
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

    // security的认证异常(AuthenticationException及子类) 先于security AccessDeniedHandler 处理
    @ExceptionHandler(AuthenticationException.class)
    public Result<?> authenticationException(AuthenticationException e) {
        Result<?> result = new Result<>(ResultCodeEnum.USER_LOGIN_EXCEPTION);
        // 登录异常细化
        if (e instanceof UsernameNotFoundException) {
            result = new Result<>(ResultCodeEnum.USER_ACCOUNT_NOT_EXIST);
        } else if (e instanceof LockedException) {
            result = new Result<>(ResultCodeEnum.USER_ACCOUNT_FROZEN);
        } else if (e instanceof DisabledException) {
            result = new Result<>(ResultCodeEnum.USER_ACCOUNT_DISABLED);
        } else if (e instanceof AccountExpiredException) {
            result = new Result<>(ResultCodeEnum.USER_ACCOUNT_EXPIRED);
        } else if (e instanceof BadCredentialsException) {
            result = new Result<>(ResultCodeEnum.USER_PASSWORD_ERROR);
        }
        return result;
    }

    // security的授权异常(AccessDeniedException及子类) 先于security AuthenticationEntryPoint 处理
    // sa权限认证的相关异常(SaTokenException的子类)也一起处理
    @ExceptionHandler({AccessDeniedException.class, NotLoginException.class, NotRoleException.class, NotPermissionException.class})
    public Result<?> accessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        log.warn("未授权访问：{}", request.getRequestURI());
        return new Result<>(ResultCodeEnum.ACCESS_UNAUTHORIZED);
    }
}
