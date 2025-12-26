package com.moyu.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 路由地址url
     */
    private String path;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 数据范围(字典 0无限制 1本人数据 2本机构 3本机构及以下 4自定义)
     */
    private Integer dataScope;
    /**
     * 自定义scope集合,逗号分隔
     */
    private String scopes;

}