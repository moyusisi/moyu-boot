package com.moyu.boot.common.security.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 令牌认证过滤器，支持多种tokenService实现。
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
        // 若已登录，上下文中设置认证信息
        if (StpUtil.isLogin()) {
            // 避免覆盖设置
            if (ObjectUtil.isEmpty(SecurityContextHolder.getContext().getAuthentication())) {
                // 从token中解析出认证信息
                Authentication authentication = tokenService.parseToken();
                // 放到Security上下文中
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 继续执行后续的过滤器链
        filterChain.doFilter(request, response);
    }
}
