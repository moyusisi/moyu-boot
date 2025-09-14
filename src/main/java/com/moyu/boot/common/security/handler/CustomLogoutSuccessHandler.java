package com.moyu.boot.common.security.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 注销登录处理类, 用于 httpSecurity.logout() 指定
 *
 * @author shisong
 * @since 2024-12-27
 */
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 当前登陆用户
        Optional<LoginUser> optUser = SecurityUtils.getLoginUser();
        if (optUser.isPresent()) {
            LoginUser loginUser = optUser.get();
            // 删除用户缓存记录 TODO
            // 记录用户退出日志
            log.info("退出登录:{}", loginUser.getUsername());
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(new ObjectMapper().writeValueAsString(Result.success("退出成功")));
    }
}