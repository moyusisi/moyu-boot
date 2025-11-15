package com.moyu.boot.plugin.codeGen.model.bo;


import lombok.Data;

/**
 * 字段元数据
 *
 * @author shisong
 * @since 2025-09-16
 */
@Data
public class ColumnMetaData {

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 字段类型
     */
    private String dataType;

    /**
     * 字段描述
     */
    private String columnComment;

    /**
     * 字段长度
     */
    private Long maxLength;

    /**
     * 是否主键(1-是 0-否)
     */
    private Integer primaryKey;

    /**
     * 是否可为空(1-是 0-否)
     */
    private Integer nullable;

    /**
     * 字符集
     */
    private String characterSetName;

    /**
     * 排序规则
     */
    private String collationName;

}