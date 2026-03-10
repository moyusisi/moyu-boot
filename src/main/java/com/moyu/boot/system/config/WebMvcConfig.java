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
     * 如果优先级低可能会导致CORS策略不生效
     */
    @Bean
    public CorsFilter corsFilter() {
        // 1. 构建跨域配置规则
        CorsConfiguration config = new CorsConfiguration();
        // 跨域请求时允许的的请求源
        config.addAllowedOriginPattern("*");
        // 跨域请求时允许的请求方式
        config.addAllowedMethod("POST, GET, OPTIONS");
        // 跨域请求时允许前端携带的请求头
        config.addAllowedHeader("*");
        // 跨域请求是否允许携带cookie凭证(为true时，Access-Control-Allow-Origin不能设置为*)
        config.setAllowCredentials(true);
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
