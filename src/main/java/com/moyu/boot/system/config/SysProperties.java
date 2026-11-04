package com.moyu.boot.system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 系统相关配置
 *
 * @author shisong
 * @since 2025-01-24
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "custom.sys")
public class SysProperties {

    /**
     * 密码加密方式
     */
    private String cryptoType = "sm4";

    /**
     * sm4自定义密钥
     */
    private String sm4Key = "KeyMustBe16Size.";

}
