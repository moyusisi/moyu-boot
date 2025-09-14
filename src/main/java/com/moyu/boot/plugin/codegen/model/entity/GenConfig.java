package com.moyu.boot.plugin.codegen.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成实体配置表
 *
 * @author shisong
 * @since 2025-09-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "gen_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenConfig extends BaseEntity {

    /**
     * 表名
     */
    private String tableName;

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
     * 业务名
     */
    private String businessName;

    /**
     * 父菜单ID
     */
    private Long parentMenuId;

    /**
     * 作者
     */
    private String author;
}