package com.moyu.boot.common.security.config;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shisong
 * @since 2025-01-24
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "custom.security")
public class SecurityProperties {

    /**
     * 是否启用鉴权功能(false等同于全加白)
     */
    private Boolean enabled = Boolean.TRUE;

    /**
     * 白名单，Security AuthorizationFilter 过滤器直接放行的路径
     */
    private List<String> whiteList = new ArrayList<>();

    /**
     * 忽略列表，不经过Security的任何过滤器(如静态资源)
     */
    private List<String> ignoreList = new ArrayList<>();

    /**
     * 会话配置
     */
    private SessionConfig session;

    /**
     * 会话配置类
     */
    @Data
    public static class SessionConfig {
        /**
         * 认证策略类型
         * * jwt   : 基于JWT的无状态认证
         * * redis : 基于Redis的有状态认证
         */
        private String type = "redis";
    }

}
