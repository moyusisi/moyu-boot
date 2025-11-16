package com.moyu.boot.common.security.service.impl;


import cn.dev33.satoken.stp.StpInterface;
import com.moyu.boot.common.security.util.SecurityUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 的权限加载接口实现类，每次调用鉴权代码时才会执行
 *
 * @author shisong
 * @since 2025-11-11
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>(SecurityUtils.getPerms());
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return new ArrayList<>(SecurityUtils.getRoles());
    }
}
