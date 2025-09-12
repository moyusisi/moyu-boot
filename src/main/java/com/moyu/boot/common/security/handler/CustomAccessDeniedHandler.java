package com.moyu.boot.common.security.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyu.boot.common.core.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 授权异常处理，无权限访问时的处理
 * 使用@PreAuthorize进行权限校验时，不通过就会进入自定义的异常
 *
 * @author shisong
 * @since 2025-01-05
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 认证失败直接返回json数据告诉前端
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        int code = HttpStatus.FORBIDDEN.value();
        String message = "权限不足，无法访问：" + request.getRequestURI();
        String responseBody = new ObjectMapper().writeValueAsString(new Result<>(code, message));
        PrintWriter printWriter = response.getWriter();
        printWriter.print(responseBody);
        printWriter.flush();
    }
}
