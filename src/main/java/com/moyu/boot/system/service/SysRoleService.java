package com.moyu.boot.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysRoleParam;

import java.util.List;
import java.util.Set;

/**
 * @author shisong
 * @description 针对表【sys_role(角色信息表)】的数据库操作Service
 * @createDate 2024-12-15 20:49:43
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 获取记录列表
     */
    List<SysRole> list(SysRoleParam roleParam);

    /**
     * 分页获取记录列表
     */
    PageData<SysRole> pageList(SysRoleParam roleParam);

    /**
     * 获取记录详情
     */
    SysRole detail(SysRoleParam roleParam);

    /**
     * 添加记录
     */
    void add(SysRoleParam roleParam);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(SysRoleParam roleParam);

    /**
     * 修改记录
     */
    void update(SysRoleParam roleParam);

    /**
     * 获取菜单树，用于给角色授权时选择(treeNode不包含button)
     *
     * @param roleParam 角色code必须传
     */
    List<Tree<String>> treeForGrant(SysRoleParam roleParam);

    /**
     * 角色授权，ROLE_HAS_MENU
     *
     * @param roleParam 角色code，授权module必须传
     */
    void grantMenu(SysRoleParam roleParam);

    /**
     * 角色内用户列表，仅包含 ROLE_HAS_USER 关系直接指定的用户。
     */
    List<SysUser> roleUserList(SysRoleParam roleParam);

    /**
     * 角色新增用户，ROLE_HAS_USER
     *
     * @param roleParam 角色code，用户集合 codeSet
     */
    void roleAddUser(SysRoleParam roleParam);

    /**
     * 角色删除用户，ROLE_HAS_USER
     *
     * @param roleParam 角色code，用户集合 codeSet
     */
    void roleDeleteUser(SysRoleParam roleParam);

    /**
     * 获取指定用户所有的角色，包括 userRole + userGroupRole
     */
    Set<String> userAllRoles(String account);

    /**
     * 获取指定角色的权限标识
     */
    Set<String> rolePerms(Set<String> roleSet);
}
