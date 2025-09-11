package com.moyu.boot.common.security.config;


import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.security.constant.SecurityConstants;
import com.moyu.boot.common.security.filter.JwtTokenAuthenticationFilter;
import com.moyu.boot.common.security.handler.AuthExceptionEntryPoint;
import com.moyu.boot.common.security.handler.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shisong
 * @since 2025-01-24
 */
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Resource
    private SecurityProperties properties;

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 设置跨域访问可以携带cookie
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOriginPattern("*");
        // 设置访问源请求头 允许携带任何头信息
        config.addAllowedHeader("*");
        // 设置访问源请求方法 允许所有的请求方法
        config.addAllowedMethod("*");
        // 初始化cors配置源对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 给配置源对象设置过滤的参数 == > 所有的路径都要求校验是否跨域
        source.registerCorsConfiguration("/**", config);
        // 返回配置好的CorsFilter
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF禁用，否则POST工具报403错误
        http.csrf().disable();
        // 允许跨域访问
        http.cors();

        // 禁用HTTP响应头的缓存控制，以确保敏感数据不会被缓存。默认情况下，会添加一些缓存控制头部，如no-store和private
        http.headers().cacheControl().disable().frameOptions().sameOrigin();
        // 设置会话会话创建策略为无状态, 基于token，不使用session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 放行白名单 TODO
        List<String> whiteList = SecurityConstants.WHITE_LIST;
        // 如果没有开启认证，则全放行
        if (ObjectUtil.notEqual(properties.getEnabled(), true)) {
            whiteList.add("/**");
        }
        // 白名单放行
        http.authorizeRequests().antMatchers(whiteList.toArray(new String[0])).permitAll();
        // 静态资源放行
        http.authorizeRequests().antMatchers("/static/**", "/public/**", "/**/*.ico").permitAll();
        // /api下的接口，需要认证才可访问
        http.authorizeRequests().antMatchers("/api/**").authenticated();

        // 添加JWT filter
        http.addFilterBefore(new JwtTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        // 添加CORS filter
        http.addFilterBefore(corsFilter(), JwtTokenAuthenticationFilter.class);

        // 异常处理。若有@ExceptionHandler处理AccessDeniedException和AuthenticationException此处配置不会起到作用
        http.exceptionHandling()
                // 认证异常处理，未认证访问的情况处理(不设置默认处理端点为：LoginUrlAuthenticationEntryPoint("/login"))
                .authenticationEntryPoint(new AuthExceptionEntryPoint())
                // 授权异常处理，访问权限不足时的处理
                .accessDeniedHandler(new CustomAccessDeniedHandler());
        return http.build();
    }
}
