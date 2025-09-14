package com.moyu.boot.common.security.util;


import com.moyu.boot.common.security.constant.SecurityConstants;
import com.moyu.boot.common.security.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * SpringSecurity安全服务工具类
 *
 * @author shisong
 * @since 2025-01-06
 */
@Slf4j
public class SecurityUtils {

    /**
     * 获取Authentication
     */
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录用户信息
     **/
    public static Optional<LoginUser> getLoginUser() {
        Optional<LoginUser> loginUser = Optional.empty();
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            // 以登录用户为 LoginUser，未登录用户为username
            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser) {
                loginUser = Optional.of((LoginUser) principal);
            }
        }
        return loginUser;
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
     * 获取部门ID
     */
    public static String getOrgCode() {
        return getLoginUser().map(LoginUser::getOrgCode).orElse(null);
    }

    /**
     * 获取数据权限范围
     */
    public static Integer getDataScope() {
        return getLoginUser().map(LoginUser::getDataScope).orElse(null);
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
     * 获取用户数据权限范围
     */
    public static Set<String> getScopes() {
        return getLoginUser().map(LoginUser::getScopes).orElse(new HashSet<>());
    }

    /**
     * 获取用户授权集合
     */
    public static Set<String> getAuthorities() {
        return getLoginUser().map(e -> AuthorityUtils.authorityListToSet(e.getAuthorities())).orElse(new HashSet<>());
    }

    /**
     * 是否为root超级管理员
     */
    public static boolean isRoot() {
        return getRoles().contains(SecurityConstants.ROOT_ROLE);
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     未加密的原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配，true表示相同
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
