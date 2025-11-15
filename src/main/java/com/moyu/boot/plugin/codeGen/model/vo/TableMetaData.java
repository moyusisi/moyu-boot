package com.moyu.boot.plugin.codeGen.model.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * @author shisong
 * @since 2025-09-14
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableMetaData {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
