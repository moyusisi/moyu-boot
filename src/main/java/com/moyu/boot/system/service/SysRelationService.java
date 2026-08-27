package com.moyu.boot.system.service;

import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.param.SysRelationParam;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 用户角色权限关系服务类Service
 *
 * @author shisong
 * @since 2024-12-16 21:15:35
 */
public interface SysRelationService extends IService<SysRelation> {

    /**
     * 获取记录列表
     */
    List<SysRelation> list(SysRelationParam param);

    /**
     * USER_HAS_ROLE 关系, user查role
     */
    Set<String> userRole(String username);

    /**
     * USER_HAS_ROLE 关系, role查user
     */
    Set<String> roleUser(String roleCode);

    /**
     * ROLE_HAS_PERM关系, role查perm
     */
    Set<String> rolePerm(String roleCode);

    /**
     * ROLE_HAS_PERM关系, roleSet查perm
     */
    Set<String> rolePerm(Set<String> roleSet);

    /**
     * USER_HAS_GROUP关系, user查询group
     */
    Set<String> userGroup(String username);

    /**
     * USER_HAS_GROUP关系, group查询user
     */
    Set<String> groupUser(String groupCode);

    /**
     * GROUP_HAS_ROLE关系, group查询role
     */
    Set<String> groupRole(String groupCode);

    /**
     * GROUP_HAS_ROLE关系, role查询group
     */
    Set<String> roleGroup(String roleCode);

}
