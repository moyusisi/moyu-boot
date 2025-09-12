package com.moyu.boot.common.security.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyu.boot.common.core.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 自定义的认证失败处理类。认证失败时不需要页面跳转，只需要返回json数据告诉前端即可。
 * 与5.2中新增的AuthenticationEntryPointFailureHandler通过AuthenticationEntryPoint方式实现AuthenticationFailureHandler等效
 * <a href="https://blog.csdn.net/weixin_43831002/article/details/126131233">参考阅读</a>
 *
 * @author shisong
 * @since 2024-12-30
 */
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 认证失败直接返回json数据告诉前端
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        int code = HttpStatus.UNAUTHORIZED.value();
        String message = "认证失败";
        response.getWriter().print(new ObjectMapper().writeValueAsString(new Result<>(code, message)));
    }
}
