package com.moyu.boot.common.core.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 表基类实体的实例属性
     */
    public static final String ID = "id";
    public static final String DELETED = "deleted";
    public static final String CREATE_TIME = "createTime";
    public static final String CREATE_BY = "createBy";
    public static final String UPDATE_TIME = "updateTime";
    public static final String UPDATE_BY = "updateBy";

    @TableField(exist = false)
    public static final Set<String> baseFieldSet = new HashSet<>(Arrays.asList(ID, DELETED, CREATE_TIME, CREATE_BY, UPDATE_TIME, UPDATE_BY));

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 删除标志（0未删除  1已删除）
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}
