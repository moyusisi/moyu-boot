package com.moyu.boot.plugin.codegen.model.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author shisong
 * @since 2025-09-14
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableMetaData {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 排序规则，如：utf8mb4_general_ci
     */
    private String tableCollation;

    /**
     * 存储引擎，如：InnoDB
     */
    private String engine;

    /**
     * 字符集，如：utf8mb4
     */
    private String charset;

    /**
     * 创建时间
     */
    private String createTime;
}
