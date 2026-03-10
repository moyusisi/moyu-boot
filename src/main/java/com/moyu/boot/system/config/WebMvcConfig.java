package com.moyu.boot.system.config;

import com.moyu.boot.system.interceptor.VueHistoryInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 配置类, 等同于web.xml
 *
 * @author shisong02
 * @since 2020-10-20
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private VueHistoryInterceptor vueHistoryInterceptor;

    /**
     * 跨域资源共享过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        // 1. 构建跨域配置规则
        CorsConfiguration config = new CorsConfiguration();
        // 设置允许的跨域源(credentials设置为include时，服务端的Access-Control-Allow-Origin不能设置为*)
        config.addAllowedOriginPattern("*");
        // 设置跨域访问可以携带cookie
        config.setAllowCredentials(true);
        // 设置允许的请求方法 允许所有的请求方法
        config.addAllowedMethod("*");
        // 设置允许的请求头 允许携带任何头信息
        config.addAllowedHeader("*");
        // 暴露的响应头（前端可获取的自定义头）
        config.addExposedHeader("Authorization");
        // 预检请求缓存时间(单位s)
        config.setMaxAge(3600L);

        //  2. 应用跨域配置规则到所有接口
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // 3. 返回配置好的CorsFilter
        return new CorsFilter(source);
    }

    /**
     * 添加拦截器
     * addPathPatterns 用于添加拦截规则, excludePathPatterns 排除拦截
     * /**： 匹配所有路径
     * /admin/**：匹配 /admin/ 下的所有路径
     * /secure/*：只匹配 /secure/user，不匹配 /secure/user/info
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加用户vue后处理拦截器
        registry.addInterceptor(vueHistoryInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/api/**", "/static/**", "/public/**", "/assets/**");
    }
}
