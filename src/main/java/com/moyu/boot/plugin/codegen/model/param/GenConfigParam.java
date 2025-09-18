package com.moyu.boot.plugin.codegen.model.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyu.boot.common.core.model.BasePageParam;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * 角色信息参数
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenConfigParam extends BasePageParam {
    //********** 额外字段 **********//
    /**
     * 待删除的id列表
     */
    private Set<Long> ids;
    /**
     * 名称关键词
     */
    private String searchKey;

    //********** db中存在的字段 **********//
    /**
     * 主键ID
     */
    private Long id;

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