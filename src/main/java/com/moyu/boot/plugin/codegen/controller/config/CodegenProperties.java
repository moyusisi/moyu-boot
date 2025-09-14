package com.moyu.boot.plugin.codegen.controller.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 代码生成器的配置
 *
 * @author shisong
 * @since 2025-09-14
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "codegen")
public class CodegenProperties {

    /**
     * 排除数据表名称列表
     */
    private List<String> excludeTables;

}
