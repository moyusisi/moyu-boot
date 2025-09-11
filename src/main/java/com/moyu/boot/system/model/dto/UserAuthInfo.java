package com.moyu.boot.system.model.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * 用于认证鉴权的用户信息对象
 *
 * @author shisong
 * @since 2025-01-07
 */
@Getter
@Setter
@ToString
@Builder
public class UserAuthInfo {
    /**
     * 账号
     */
    private String account;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 用户角色集合 ["root","admin"]
     */
    private Set<String> roles;

    /**
     * 用户权限标识集合
     */
    private Set<String> perms;
}
