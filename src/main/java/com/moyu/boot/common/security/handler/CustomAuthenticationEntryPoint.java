package com.moyu.boot.common.security.handler;


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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 自定义认证异常(登录异常)处理，未认证访问的情况处理(在filter层处理)
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

        // 登录失效，需要重新登录
        Result<?> result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED);
        String responseBody = new ObjectMapper().writeValueAsString(result);
        log.info("Security Filter层，认证异常处理返回:{}", responseBody);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(responseBody);
        printWriter.flush();
    }
}