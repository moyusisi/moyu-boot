package com.moyu.boot.common.core.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 数据库表通用基础字段实体，需要此通用字段的实体可继承此类
 *
 * @author shisong
 * @since 2024-11-26
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 表基类实体的实例属性
     */
    public static final String CREATE_TIME = "createTime";
    public static final String CREATE_BY = "createBy";
    public static final String UPDATE_TIME = "updateTime";
    public static final String UPDATE_BY = "updateBy";

    public static final Set<String> baseFieldSet = new HashSet<>(Arrays.asList(CREATE_TIME, CREATE_BY, UPDATE_TIME, UPDATE_BY));

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateBy;
}
