package com.moyu.boot.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysRoleParam;
import com.moyu.boot.system.model.vo.SysRoleVO;

import java.util.List;
import java.util.Set;

/**
 * 角色信息服务类Service
 *
 * @author shisong
 * @since 2024-12-15 20:49:43
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<SysRoleVO> list(SysRoleParam param);

    /**
     * 分页获取记录列表
     */
    PageData<SysRoleVO> pageList(SysRoleParam param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
    SysRoleVO detail(SysRoleParam param);

    /**
     * 添加记录
     */
    void add(SysRoleParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(SysRoleParam param);

    /**
     * 修改记录
     */
    void update(SysRoleParam param);

    /**
     * 获取菜单树，用于给角色授权时选择(treeNode不包含button)
     *
     * @param param 角色code必须传
     */
    List<Tree<String>> treeForGrant(SysRoleParam param);

    /**
     * 角色授权，ROLE_HAS_MENU
     *
     * @param param 角色code，授权module必须传
     */
    void grantMenu(SysRoleParam param);

    /**
     * 角色内用户列表，仅包含 ROLE_HAS_USER 关系直接指定的用户。
     */
    List<SysUser> roleUserList(SysRoleParam param);

    /**
     * 角色新增用户，ROLE_HAS_USER
     *
     * @param param 角色code，用户集合 codeSet
     */
    void roleAddUser(SysRoleParam param);

    /**
     * 角色删除用户，ROLE_HAS_USER
     *
     * @param param 角色code，用户集合 codeSet
     */
    void roleDeleteUser(SysRoleParam param);

    /**
     * 获取指定用户所有的角色，包括 userRole + userGroupRole
     */
    Set<String> userAllRoles(String account);

    /**
     * 获取指定角色的权限标识
     */
    Set<String> rolePerms(Set<String> roleSet);
}
