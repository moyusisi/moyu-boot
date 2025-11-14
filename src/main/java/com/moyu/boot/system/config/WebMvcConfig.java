package com.moyu.boot.system.config;

import com.moyu.boot.system.interceptor.VueHistoryInterceptor;
import org.springframework.context.annotation.Configuration;
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
