package com.moyu.boot.plugin.codeGen.model.entity;

import com.moyu.boot.common.mybatis.handler.CustomInsertListener;
import com.moyu.boot.common.mybatis.handler.CustomUpdateListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;

/**
 * 代码生成配置表
 *
 * @author shisong
 * @since 2025-09-14
 */
@Data
@Table(value = "gen_config", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
public class GenConfig {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @Id(keyType = KeyType.Auto)
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
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}