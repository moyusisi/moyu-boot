package com.moyu.boot.authN.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.moyu.boot.authN.service.UserDetailsService;
import com.moyu.boot.common.authZ.model.LoginUser;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.service.SysGroupService;
import com.moyu.boot.system.service.SysRoleService;
import com.moyu.boot.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 用户信息加载服务的自定义实现类
 *
 * @author shisong
 * @since 2024-12-27
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysGroupService sysGroupService;

    /**
     * 通过username加载登录用户信息
     */
    @Override
    public SysUser loadUserByUsername(String username) {
        log.info("加载{}的用户信息", username);
        // 如果auth与user属于不同的服务，则这里应该通过远程调用获取用户信息
        SysUser sysUser = sysUserService.getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getAccount, username));
        if (sysUser == null) {
            log.info("登录用户:{}不存在", username);
            throw new BusinessException(ResultCodeEnum.USER_ACCOUNT_NOT_EXIST);
        }
        // 创建 UserDetails
        return sysUser;
    }

    /**
     * 创建LoginUser
     */
    @Override
    public LoginUser buildUserDetails(SysUser sysUser) {
        // 用户直接拥有的角色 ROLE_HAS_USER 关系
        Set<String> roleSet = sysRoleService.userRoles(sysUser.getAccount());
        // 组装LoginUser
        LoginUser loginUser = LoginUser.builder()
                .userId(sysUser.getUserId())
                .username(sysUser.getAccount())
                .name(sysUser.getName())
                .orgCode(sysUser.getOrgCode())
                // 角色集合(默认角色+直接拥有的角色)
                .roles(roleSet)
                // 权限标识集合(仅接口,无菜单)
                .perms(sysRoleService.rolePerms(roleSet))
                // 接口权限的数据范围
                .permScopeMap(sysRoleService.rolePermScopeMap(roleSet, sysUser.getOrgCode()))
                // 数据范围默认本人数据，真正的数据范围在PreDataScope切面中赋值
                .dataScope(DataScopeEnum.SELF.getCode())
                // 默认岗位
                .groupCode(sysGroupService.defaultGroup())
                .build();
        return loginUser;
    }
}