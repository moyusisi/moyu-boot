package com.moyu.boot.plugin.codeGen.config;


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
     * 包名，如:com.moyu.boot
     */
    private String packageName;

    /**
     * 模块名，如system。最终会与包名拼接在一起，如:com.moyu.boot.system
     */
    private String moduleName;

    /**
     * 作者
     */
    private String author;

    /**
     * 排除数据表名称列表
     */
    private List<String> excludeTables;

}
