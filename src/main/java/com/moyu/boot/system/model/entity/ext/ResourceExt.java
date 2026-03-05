package com.moyu.boot.system.model.entity.ext;

import lombok.Data;

/**
 * sys_relation表中扩展字段对应的实体
 *
 * @author shisong
 * @since 2026-02-27
 */
@Data
public class ResourceExt {

    /**
     * 菜单资源的扩展属性(目录、菜单、内链、外链 共用)
     **/
    @Data
    public static class MetaExt {
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
}
