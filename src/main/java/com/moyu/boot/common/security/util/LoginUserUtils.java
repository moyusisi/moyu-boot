package com.moyu.boot.common.security.util;

import cn.dev33.satoken.stp.StpUtil;
import com.moyu.boot.common.security.model.LoginUser;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 登录用户工具类
 *
 * @author shisong
 * @since 2025-01-06
 */
@Slf4j
public class LoginUserUtils {

    /**
     * ROOT管理员角色编码
     */
    public static final String ROOT_ROLE = "ROOT";

    /**
     * 获取当前登录用户信息
     **/
    public static Optional<LoginUser> getLoginUser() {
        // StpUtil.getTokenSession()时会校验是否登录(checkLogin)
        return Optional.of((LoginUser) StpUtil.getTokenSession().get("loginUser"));
    }


    /**
     * 获取用户账号
     *
     * @return String 用户账号
     */
    public static String getUsername() {
        return getLoginUser().map(LoginUser::getUsername).orElse(null);
    }

    /**
     * 获取当前部门code
     */
    public static String getOrgCode() {
        return getLoginUser().map(LoginUser::getOrgCode).orElse(null);
    }

    /**
     * 获取用户角色集合
     */
    public static Set<String> getRoles() {
        return getLoginUser().map(LoginUser::getRoles).orElse(new HashSet<>());
    }

    /**
     * 获取用户权限集合
     */
    public static Set<String> getPerms() {
        return getLoginUser().map(LoginUser::getPerms).orElse(new HashSet<>());
    }

    /**
     * 获取数据权限范围
     */
    public static Integer getDataScope() {
        return getLoginUser().map(LoginUser::getDataScope).orElse(null);
    }

    /**
     * 获取用户数据权限范围
     */
    public static Set<String> getScopes() {
        return getLoginUser().map(LoginUser::getScopeSet).orElse(new HashSet<>());
    }

    /**
     * 是否为root超级管理员
     */
    public static boolean isRoot() {
        return getRoles().contains(ROOT_ROLE);
    }

}
