package com.moyu.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单权限表
 *
 * @TableName sys_menu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_resource")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysResource extends BaseEntity {
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 父编码
     */
    private String parentCode;

    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 菜单类型（字典 1模块 2目录 3菜单 4按钮 5外链）
     *
     * @see com.moyu.boot.system.enums.ResourceTypeEnum
     */
    private Integer resourceType;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 链接地址
     */
    private String link;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否可见（0不可见 1可见）
     */
    private Integer visible;

    /**
     * 归属模块
     */
    private String module;

    /**
     * 排序顺序
     */
    private Integer sortNum;

    /**
     * 使用状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 扩展信息
     */
    private String extJson;

    /**
     * 备注
     */
    private String remark;

}