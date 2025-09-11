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
     * 【目录】只有一个子路由是否始终显示
     */
    private Boolean alwaysShow;

    /**
     * 【菜单】在tabView中是否固定显示在tab中
     */
    private Boolean affix;

    /**
     * 【菜单】是否开启页面缓存
     */
    private Boolean keepAlive;

    /**
     * 【链接】链接的地址(包括内链外链)
     */
    private String url;
}
