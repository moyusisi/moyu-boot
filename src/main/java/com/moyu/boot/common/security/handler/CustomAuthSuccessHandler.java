package com.moyu.boot.common.security.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 自定义的认证成功处理类。认证成功直接返回json数据告诉前端登陆成功(可用于 httpSecurity.formLogin()指定)
 * <a href="https://blog.csdn.net/weixin_43831002/article/details/126131233">参考阅读</a>
 *
 * @author shisong
 * @since 2024-12-30
 */
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String token = TokenService.generateToken(loginUser);
        // 认证成功直接返回json数据
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter printWriter = response.getWriter();
        printWriter.print(new ObjectMapper().writeValueAsString(Result.success(token)));
        printWriter.flush();
    }
}
