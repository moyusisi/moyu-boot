package com.moyu.boot.auth.service;


import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.enums.StatusEnum;
import com.moyu.boot.system.model.entity.SysGroup;
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
import java.util.*;

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
        if (sysUser == null) {
            log.info("登录用户:{}不存在", username);
            throw new UsernameNotFoundException("用户不存在");
        } else if (sysUser.getDeleted() == 1) {
            log.info("登录用户:{}已被删除", username);
            throw new UsernameNotFoundException("用户不存在");
        } else if (Objects.equals(sysUser.getStatus(), StatusEnum.DISABLE.getCode())) {
            log.info("登录用户:{}已被停用", username);
            throw new UsernameNotFoundException("用户不存在");
        }
        // 创建 UserDetails
        return buildUserDetails(sysUser);
    }

    /**
     * 创建LoginUserDetails
     */
    private LoginUser buildUserDetails(SysUser sysUser) {
        // 用户直接拥有的角色 ROLE_HAS_USER 关系
        Set<String> roleSet = sysRelationService.userRole(sysUser.getAccount());
        // 添加默认角色
        roleSet.add(sysRoleService.defaultRole());
        // 权限标识集合(仅接口,无菜单)
        Set<String> permSet = sysRoleService.rolePerms(roleSet);
        // 组装LoginUser
        LoginUser loginUser = LoginUser.builder()
                .username(sysUser.getAccount())
                .password(sysUser.getPassword())
                .enabled(sysUser.getStatus() == 0)
                // 默认岗位权限(默认角色+直接拥有的角色)
                .roles(roleSet)
                .perms(permSet)
                .orgCode(sysUser.getOrgCode())
                .dataScope(DataScopeEnum.SELF.getCode())
                .build();
        // TODO 应该通过岗位获取权限，无任何岗位则应使用默认岗位(默认角色+直接拥有的角色)
        // 岗位列表
        List<SysGroup> groupList = sysGroupService.userGroupList(sysUser.getAccount());
        SysGroup group = null;
        if (ObjectUtil.isNotEmpty(groupList)) {
            // 优先获取用户本部门的岗位
            Optional<SysGroup> opt = groupList.stream().filter(e -> e.getCode().equals(sysUser.getOrgCode())).findFirst();
            group = opt.orElse(groupList.get(0));
        }
        // 有岗位则覆盖默认的岗位权限
        if (group != null) {
            // 岗位角色 group-role
            loginUser.setRoles(sysRelationService.groupRole(group.getCode()));
            // 岗位权限 权限标识集合(仅接口,无菜单)
            loginUser.setPerms(sysRoleService.rolePerms(roleSet));
            // 当前岗位
            loginUser.setGroupCode(group.getCode());
            // 组织机构随岗位变化
            loginUser.setOrgCode(group.getOrgCode());
            // 岗位设置的数据权限范围
            loginUser.setDataScope(group.getDataScope());
            // 自定义数据权限集合
            if (DataScopeEnum.ORG_DEFINE.getCode().equals(loginUser.getDataScope())) {
                loginUser.setScopes(new HashSet<>(SysConstants.COMMA_SPLITTER.splitToList(group.getScopeSet())));
            }
        }
        // 初始化权限
        loginUser.initAuthorities();
        return loginUser;
    }
}