package com.moyu.boot.plugin.codegen.model.param;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyu.boot.common.core.model.BasePageParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * 表查询参数
 *
 * @author shisong
 * @since 2025-09-14
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableQueryParam extends BasePageParam {
    //********** 额外字段 **********//
    /**
     * 待删除的id列表，通常不会集联删除
     */
    private Set<Long> ids;

    /**
     * 名称关键词
     */
    private String searchKey;

    /**
     * 要排除的表名
     */
    @JsonIgnore
    private List<String> excludeTables;

    //********** db中存在的字段 **********//
    /**
     * 主键id
     */
    private Long id;
}
