package com.moyu.boot.plugin.codegen.model.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 生成代码的配置信息(展示和提交均用此对象)
 *
 * @author shisong
 * @since 2025-09-17
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenConfigInfo {
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
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
     * 来源类型，TABLE、SQL
     */
    private String sourceType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 字段配置列表
     */
    private List<FieldConfigVO> fieldConfigList;
}
