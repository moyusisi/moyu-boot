package com.moyu.boot.common.security.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyu.boot.common.core.model.Result;
import org.springframework.http.HttpStatus;
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
 * 认证异常处理，未认证访问的情况处理
 *
 * @author shisong
 * @since 2025-01-05
 */
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 认证失败直接返回json数据告诉前端
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        int code = HttpStatus.UNAUTHORIZED.value();
        String message = "未认证，无法访问：" + request.getRequestURI();
        String responseBody = new ObjectMapper().writeValueAsString(new Result<>(code, message));
        PrintWriter printWriter = response.getWriter();
        printWriter.print(responseBody);
        printWriter.flush();
    }
}