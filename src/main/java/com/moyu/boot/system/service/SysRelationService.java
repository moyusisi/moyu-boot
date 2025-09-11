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
     * 通过(分组-用户、分组-角色)关系查询指定用户的所有角色，即:用户->分组->角色
     */
    Set<String> userGroupRole(String account);

    /**
     * 通过(分组-用户、分组-角色)关系查询指定角色的所有用户，即:角色->分组->用户
     * 特别注意，group与org有关联
     */
    Set<String> roleGroupUser(String roleCode);

    /**
     * 通过(用户-角色)关系查询指定用户的所有角色，即:用户->角色
     * 特别注意，role与org无关
     */
    Set<String> userRole(String account);

    /**
     * 通过(用户-角色)关系查询指定角色的所有用户，即:角色->用户
     * 特别注意，role与org无关
     */
    Set<String> roleUser(String roleCode);

    /**
     * 通过(用户->分组、分组->角色、角色->权限)关系查询 用户->权限
     */
    Set<String> userMenu(String account);

    /**
     * 查询指定角色的所有资源菜单
     */
    Set<String> roleMenu(Set<String> roleSet);

}
