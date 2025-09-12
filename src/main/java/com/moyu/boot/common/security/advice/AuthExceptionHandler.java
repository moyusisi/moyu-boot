package com.moyu.boot.common.security.advice;


import com.moyu.boot.common.core.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
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

    // security的授权异常(AccessDeniedException及子类) 先于security AuthenticationEntryPoint 处理
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> accessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        int code = HttpStatus.FORBIDDEN.value();
        log.error("权限不足", e);
        String message = "权限不足，无法访问：" + request.getRequestURI();
        return new Result<>(code, message);
    }

    // security的认证异常(AuthenticationException及子类) 先于security AccessDeniedHandler 处理
    @ExceptionHandler(AuthenticationException.class)
    public Result<?> authenticationException(HttpServletRequest request, AuthenticationException e) {
        int code = HttpStatus.UNAUTHORIZED.value();
        String message = "未认证，无法访问：" + request.getRequestURI();
        return new Result<>(code, message);
    }
}
