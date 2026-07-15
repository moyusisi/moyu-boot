package com.moyu.boot.common.authZ.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义认证鉴权相关配置
 *
 * @author shisong
 * @since 2025-01-24
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "custom.auth")
public class AuthProperties {

    /**
     * 是否启用登录鉴权功能(false等同于全加白)
     */
    private Boolean enabled = Boolean.TRUE;

    /**
     * 保护列表，需要认证访问的的路径
     */
    private List<String> authList = Arrays.asList("/api/**");

    /**
     * 白名单，认证鉴权过滤器直接放行的路径
     */
    private List<String> whiteList = new ArrayList<>();

    /**
     * 密码加密方式
     */
    private String cryptoType = "sm4";

    /**
     * sm4自定义密钥
     */
    private String sm4Key = "KeyMustBe16Size.";

    /**
     * 令牌类型
     * * jwt   : JWT令牌
     * * redis : 有状态令牌
     */
    private String tokenType = "redis";

}
