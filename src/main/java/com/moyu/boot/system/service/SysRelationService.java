package com.moyu.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.param.SysRelationParam;

import java.util.List;
import java.util.Set;

/**
 * @author shisong
 * @description 针对表【sys_relation(用户角色权限关系表)】的数据库操作Service
 * @createDate 2024-12-16 21:15:35
 */
public interface SysRelationService extends IService<SysRelation> {

    /**
     * 获取记录列表
     */
    List<SysRelation> list(SysRelationParam param);

    /**
     * ROLE_HAS_USER 关系, role查user
     */
    Set<String> roleUser(String roleCode);

    /**
     * ROLE_HAS_USER 关系, user查role
     */
    Set<String> userRole(String username);

    /**
     * ROLE_HAS_PERM关系, role查perm
     */
    Set<String> rolePerm(String roleCode);

    /**
     * ROLE_HAS_PERM关系, roleSet查perm
     */
    Set<String> rolePerm(Set<String> roleSet);

    /**
     * GROUP_HAS_USER关系, group查询user
     */
    Set<String> groupUser(String groupCode);

    /**
     * GROUP_HAS_USER关系, user查询group
     */
    Set<String> userGroup(String username);

    /**
     * GROUP_HAS_ROLE关系, group查询role
     */
    Set<String> groupRole(String groupCode);

    /**
     * GROUP_HAS_ROLE关系, role查询group
     */
    Set<String> roleGroup(String roleCode);

}
