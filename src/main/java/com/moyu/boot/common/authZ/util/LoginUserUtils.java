package com.moyu.boot.common.authZ.util;

import com.moyu.boot.common.authZ.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
        Optional<LoginUser> optUser = Optional.empty();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // 用户凭证，已登录用户为 LoginUser，未登录用户为username
            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser) {
                optUser = Optional.of((LoginUser) principal);
            }
        }
        return optUser;
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
