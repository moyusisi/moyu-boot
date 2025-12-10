package com.moyu.boot.plugin.codeGen.model.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 代码生成配置表
 *
 * @author shisong
 * @since 2025-09-14
 */
@Data
@TableName(value = "gen_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenConfig {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 实体类名
     */
    private String entityName;

    /**
     * 实体类描述
     */
    private String entityDesc;

    /**
     * 父菜单编码
     */
    private String parentMenuCode;

    /**
     * 作者
     */
    private String author;

    /**
     * 详情页打开方式，字典:0本页内打开,1独立页面打开'
     */
    private Integer detailOpenType;

    /**
     * 来源类型，TABLE、SQL
     */
    private String sourceType;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}