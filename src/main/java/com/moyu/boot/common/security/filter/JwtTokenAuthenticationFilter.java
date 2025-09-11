package com.moyu.boot.common.security.filter;

import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT令牌认证过滤器，用于从请求头中获取令牌并进行验证。
 *
 * @author shisong
 * @since 2024-01-04
 */
@Slf4j
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求携带的令牌
        String token = TokenService.getTokenFromRequest(request);
        if (ObjectUtil.isNotEmpty(token)) {
            // 验证token，并解析为jwt对象
            // 从缓存中获取用户信息
            LoginUser loginUser = TokenService.getLoginUserByToken(token);

            // 如果 loginUser 存在，设置SecurityContext
            if (ObjectUtil.isNotNull(loginUser) && ObjectUtil.isEmpty(SecurityContextHolder.getContext().getAuthentication())) {
                // 设置认证对象
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 继续执行后续的过滤器链
        filterChain.doFilter(request, response);
    }
}
