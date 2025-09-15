package com.moyu.boot.plugin.codegen.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shisong
 * @since 2025-09-15
 */
@Getter
@Setter
@TableName(value = "gen_field_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenFieldConfig extends BaseEntity {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 关联的配置ID
     */
    private Long tableId;

    /**
     * 列名
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
     * 注释
     */
    private String fieldComment;

    /**
     * 字段排序
     */
    private Integer fieldSort;

    /**
     * 字段长度
     */
    private Long maxLength;

    /**
     * 是否必填
     */
    private Integer required;

    /**
     * 是否在查询条件显示(查)
     */
    private Integer showInQuery;

    /**
     * 是否在列表显示
     */
    private Integer showInList;

    /**
     * 是否在表单显示(增改)
     */
    private Integer showInForm;

    /**
     * 查询方式
     */
    private String queryType;

    /**
     * 表单类型(输入方式)
     */
    private String formType;

    /**
     * 字典类型
     */
    private String dictType;
}
