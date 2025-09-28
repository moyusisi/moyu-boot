package com.moyu.boot.plugin.codegen.model.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 代码生成字段配置表
 *
 * @author shisong
 * @since 2025-09-15
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldConfigVO {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 关联的实体配置ID
     */
    private Long tableId;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列类型
     */
    private String columnType;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段描述
     */
    private String fieldRemark;

    /**
     * 字段排序
     */
    private Integer fieldSort;

    /**
     * 最大长度
     */
    private Long maxLength;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 是否在列表显示
     */
    private Boolean showInList;

    /**
     * 是否在表单显示
     */
    private Boolean showInForm;

    /**
     * 是否在查询条件显示
     */
    private Boolean showInQuery;

    /**
     * 表单类型(输入方式)
     */
    private String formType;

    /**
     * 查询方式
     */
    private String queryType;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
