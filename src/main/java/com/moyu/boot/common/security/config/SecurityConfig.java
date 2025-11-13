package com.moyu.boot.common.security.config;


import com.moyu.boot.common.security.filter.TokenAuthenticationFilter;
import com.moyu.boot.common.security.handler.CustomAccessDeniedHandler;
import com.moyu.boot.common.security.handler.CustomAuthenticationEntryPoint;
import com.moyu.boot.common.security.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * WebSecurityConfigurerAdapter已废弃，替代方案如下:
 * 使用 SecurityFilterChain Bean 配置 HttpSecurity
 * 使用 WebSecurityCustomizer Bean 来配置 WebSecurity
 *
 * @author shisong
 * @since 2025-01-24
 */
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Resource
    private SecurityProperties properties;

    @Resource
    private TokenService tokenService;

    /**
     * 跨域过滤器配置
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

    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 放行白名单
        List<String> whiteList = new ArrayList<>();
        // 如果没有开启认证，则全放行，否则按照白名单放行
        if (Boolean.FALSE.equals(properties.getEnabled())) {
            whiteList.add("/**");
        } else {
            whiteList.addAll(properties.getWhiteList());
        }
        // 白名单放行
        http.authorizeRequests().antMatchers(whiteList.toArray(new String[0])).permitAll();
        // 设置/api下的接口，需要认证才可访问
        http.authorizeRequests().antMatchers("/api/**").authenticated();

        // 允许跨域访问
        http.cors();
        // 禁用 CSRF 防护，否则POST工具报403错误，前后端分离无需此防护机制
        http.csrf().disable();
        // 设置会话会话创建策略为无状态, 基于token，不使用session，适用于前后端分离架构
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 禁用HTTP响应头的缓存控制，以确保敏感数据不会被缓存。默认情况下，会添加一些缓存控制头部，如no-store和private
        http.headers().cacheControl().disable();
        // 禁用 X-Frame-Options 响应头，允许页面被嵌套到 iframe 中
        http.headers().frameOptions().disable();
        // 禁用Spring Security默认自带的表单登录功能()
        http.formLogin().disable();
        // 禁用Spring Security默认注销功能
        http.logout().disable();
        // 禁用 HTTP Basic 认证，避免弹窗式登录
        http.httpBasic().disable();

        // 添加Token认证解析过滤器
        http.addFilterBefore(new TokenAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class);
        // 添加CORS filter
        http.addFilterBefore(corsFilter(), TokenAuthenticationFilter.class);

        // 异常处理。filter层，在HttpSecurity中设置的authenticated()或hasAuthority()会触发此异常处理机制
        http.exceptionHandling()
                // 认证异常处理，未认证访问的情况处理(不设置默认处理端点为：LoginUrlAuthenticationEntryPoint("/login"))
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                // 授权异常处理，访问权限不足时的处理
                .accessDeniedHandler(new CustomAccessDeniedHandler());
        return http.build();
    }

    /**
     * 自定义web安全配置，以忽略特定请求路径的安全性检查。
     * 该配置用于指定哪些请求路径不经过Spring Security过滤器链。通常用于静态资源文件。
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            List<String> ignoreList = properties.getIgnoreList();
            if (!CollectionUtils.isEmpty(ignoreList)) {
                web.ignoring().antMatchers(ignoreList.toArray(new String[0]));
            }
        };
    }

}
