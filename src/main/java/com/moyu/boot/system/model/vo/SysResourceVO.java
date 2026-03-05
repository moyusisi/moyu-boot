package com.moyu.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 资源权限视图对象
 *
 * @author moyusisi
 * @since 2026-02-13
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysResourceVO {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
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
     * 资源类型（字典 1模块 2目录 3菜单 4内链 5外链 6按钮）
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
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 修改人
     */
    private String updateBy;

    //***** 扩展信息extJson的展开 *****
    /**
     * 【目录】是否简洁模式(只有一个菜单时，不显示目录直接显示该菜单)
     */
    private Integer brief;

    /**
     * 【菜单】是否固定显示
     */
    private Integer affix;

    /**
     * 【菜单】是否支持页面缓存
     */
    private Integer keepAlive;
}