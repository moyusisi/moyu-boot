package com.moyu.boot.system.model.entity.ext;

import lombok.Data;

import java.util.List;

/**
 * sys_relation表中扩展字段对应的实体
 *
 * @author shisong
 * @since 2026-02-27
 */
@Data
public class RelationExt {

    /**
     * 当ext_json表示数据范围信息时对应此对象
     **/
    @Data
    public static class ScopeExt {

        /**
         * 数据权限(字典 0无限制 1仅本人数据 2仅本机构 3本机构及以下 4本公司及以下 5自定义)
         *
         * @see com.moyu.boot.common.core.enums.DataScopeEnum
         */
        private Integer dataScope;

        /**
         * 自定义scope集合
         */
        private List<String> scopeList;
    }
}
