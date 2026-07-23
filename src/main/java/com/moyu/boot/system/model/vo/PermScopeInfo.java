package com.moyu.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 接口的权限+数据范围信息
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermScopeInfo {

    /**
     * 按钮编码
     */
    private String code;

    /**
     * 按钮名称
     */
    private String btnName;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口地址
     */
    private String path;

    /**
     * 数据范围(字典 0无限制 1本人数据 2本机构 3本机构及以下 4自定义)
     */
    private Integer dataScope;
    /**
     * 自定义scope集合,逗号分隔
     */
    private List<String> scopeList;

}