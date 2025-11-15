package com.moyu.boot.plugin.codeGen.model.param;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.core.model.BasePageParam;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
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
     * 指定表名集合
     */
    private Set<String> tableNameSet;
    /**
     * 名称关键词
     */
    private String searchKey;

    /**
     * sql语句
     */
    private String sql;

    /**
     * 要排除的表名
     */
    @JsonIgnore
    private List<String> excludeTables;

    //********** db中存在的字段 **********//
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

}