package com.moyu.boot.authN.service;

import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.system.model.entity.SysUser;

/**
 * 用户信息服务类
 * 原本是SpringSecurity中的接口，对其改造
 *
 * @author shisong
 * @since 2026-03-09
 */
public interface UserDetailsService {

    /**
     * 通过username加载登录用户信息
     */
    SysUser loadUserByUsername(String username);

    /**
     * 根据用户实体构造登录用户信息
     */
    LoginUser buildUserDetails(SysUser sysUser);
}
