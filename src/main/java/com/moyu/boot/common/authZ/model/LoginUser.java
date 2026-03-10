package com.moyu.boot.common.authZ.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;


/**
 * 登陆用户对象
 *
 * @author shisong
 * @since 2024-12-27
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户账号，唯一标识 account
     */
    private String username;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 当前组织机构(岗位相关)
     */
    private String orgCode;
    /**
     * 当前岗位(可能为空)
     */
    private String groupCode;
    /**
     * 角色集合
     */
    private Set<String> roles;

    /**
     * 权限集合(仅接口的权限标记)
     */
    private Set<String> perms;

    /**
     * 数据范围(动态变化)
     * @see com.moyu.boot.common.core.annotation.PreDataScope
     */
    private Integer dataScope;
    /**
     * 数据权限集合
     */
    private Set<String> scopeSet;

    /**
     * 接口权限对应的数据范围
     */
    private Map<String, LoginUser.DataScopeInfo> permScopeMap;

    /**
     * 数据范围信息
     **/
    @Data
    public static class DataScopeInfo {

        /**
         * 数据权限(字典 0无限制 1仅本人数据 2仅本机构 3本机构及以下 4本公司及以下 5自定义)
         *
         * @see com.moyu.boot.common.core.enums.DataScopeEnum
         */
        private Integer dataScope;

        /**
         * 数据范围集合
         */
        private Set<String> scopeSet;
    }

}
