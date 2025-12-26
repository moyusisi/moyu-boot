package com.moyu.boot.auth.service;


import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysUserParam;
import com.moyu.boot.system.service.SysGroupService;
import com.moyu.boot.system.service.SysRelationService;
import com.moyu.boot.system.service.SysRoleService;
import com.moyu.boot.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 用户信息加载服务的自定义实现类
 * Spring Security权限认证时(AuthenticationProvider.authenticate)会调用UserDetailsService.loadUserByUsername
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

    @Resource
    private SysRelationService sysRelationService;

    /**
     * SpringSecurity权限认证时(AuthenticationProvider#authenticate)会调用此方法
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("加载{}的用户信息", username);
        // 如果auth与user属于不同的服务，则这里应该通过远程调用获取用户信息
        SysUser sysUser = sysUserService.detail(SysUserParam.builder().account(username).build());
//        SysUser sysUser = sysUserService.getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getAccount, username));
        if (sysUser == null) {
            log.info("登录用户:{}不存在", username);
            throw new UsernameNotFoundException("用户账号不存在");
        }
        // 创建 UserDetails
        return buildUserDetails(sysUser);
    }

    /**
     * 创建LoginUserDetails
     */
    private LoginUser buildUserDetails(SysUser sysUser) {
        // 用户直接拥有的角色 ROLE_HAS_USER 关系
        Set<String> roleSet = sysRoleService.userRoles(sysUser.getAccount());
        // 组装LoginUser
        LoginUser loginUser = LoginUser.builder()
                .username(sysUser.getAccount())
                .name(sysUser.getName())
                .orgCode(sysUser.getOrgCode())
                .password(sysUser.getPassword())
                .enabled(sysUser.getStatus() == 0)
                // 角色集合(默认角色+直接拥有的角色)
                .roles(roleSet)
                // 权限标识集合(仅接口,无菜单)
                .perms(sysRoleService.rolePerms(roleSet))
                // 接口权限的数据范围
                .permScopeMap(sysRoleService.rolePermScopeMap(roleSet, sysUser.getOrgCode()))
                // 默认岗位
                .groupCode(sysGroupService.defaultGroup())
                .groupOrgCode(sysUser.getOrgCode())
                .build();
        // 初始化权限
        loginUser.initAuthorities();
        return loginUser;
    }
}