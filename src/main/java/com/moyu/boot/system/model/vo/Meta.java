package com.moyu.boot.system.model.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 路由元信息对象
 * <a href="https://router.vuejs.org/zh/guide/advanced/meta.html">参考这里</a>
 *
 * @author shisong
 * @since 2025-03-16
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta {
    /**
     * 路由title
     */
    private String title;

    /**
     * 标识路由的类型
     *
     * @see com.moyu.boot.system.enums.ResourceTypeEnum#name().toLowerCase
     */
    private String type;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否隐藏
     */
    private Boolean hidden;

    /**
     * 【目录】是否简洁模式(只有一个菜单时，不显示目录直接显示该菜单)
     */
    private Boolean brief;

    /**
     * 【菜单】是否固定显示
     */
    private Boolean affix;

    /**
     * 【菜单】是否支持页面缓存
     */
    private Boolean keepAlive;

    /**
     * 【链接】链接的地址(包括内链外链)
     */
    private String url;
}
