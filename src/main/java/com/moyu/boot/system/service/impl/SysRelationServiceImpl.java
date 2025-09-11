package com.moyu.boot.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.system.enums.RelationTypeEnum;
import com.moyu.boot.system.mapper.SysRelationMapper;
import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.param.SysRelationParam;
import com.moyu.boot.system.service.SysRelationService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author shisong
 * @description 针对表【sys_relation(用户角色权限关系表)】的数据库操作Service实现
 * @createDate 2024-12-16 21:15:35
 */
@Service
public class SysRelationServiceImpl extends ServiceImpl<SysRelationMapper, SysRelation> implements SysRelationService {

    @Override
    public List<SysRelation> list(SysRelationParam param) {
        QueryWrapper<SysRelation> queryWrapper = new QueryWrapper<SysRelation>().checkSqlInjection();
        queryWrapper.lambda()
                .eq(ObjectUtil.isNotEmpty(param.getObjectId()), SysRelation::getObjectId, param.getObjectId())
                .eq(ObjectUtil.isNotEmpty(param.getTargetId()), SysRelation::getTargetId, param.getTargetId())
                .in(ObjectUtil.isNotEmpty(param.getObjectSet()), SysRelation::getObjectId, param.getObjectSet())
                .in(ObjectUtil.isNotEmpty(param.getTargetSet()), SysRelation::getTargetId, param.getTargetSet())
                .eq(ObjectUtil.isNotEmpty(param.getRelationType()), SysRelation::getRelationType, param.getRelationType());
        // 查询
        List<SysRelation> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public Set<String> userGroupRole(String account) {
        // 用户所属分组
        Set<String> groupSet = new HashSet<>();
        // 查询用户归属的所有分组
        list(new LambdaQueryWrapper<SysRelation>()
                // 关系类型
                .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_USER.getCode())
                // 查询group
                .select(SysRelation::getObjectId)
                // 指定用户
                .eq(SysRelation::getTargetId, account)
        ).forEach(e -> groupSet.add(e.getObjectId()));
        // 角色集
        Set<String> roleSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(groupSet)) {
            // 查询分组的所有角色
            list(new LambdaQueryWrapper<SysRelation>()
                    // 关系类型
                    .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_ROLE.getCode())
                    // 查询role
                    .select(SysRelation::getTargetId)
                    // 指定group
                    .in(SysRelation::getObjectId, groupSet)
            ).forEach(e -> roleSet.add(e.getTargetId()));
        }
        return roleSet;
    }

    @Override
    public Set<String> roleGroupUser(String roleCode) {
        // 角色所属分组
        Set<String> groupSet = new HashSet<>();
        // 查询用户归属的所有分组
        list(new LambdaQueryWrapper<SysRelation>()
                // 查询group
                .select(SysRelation::getObjectId)
                // 关系类型
                .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_ROLE.getCode())
                // 指定用户
                .eq(SysRelation::getTargetId, roleCode)
        ).forEach(e -> groupSet.add(e.getObjectId()));
        // 用户集
        Set<String> userSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(groupSet)) {
            // 查询分组的所有角色
            list(new LambdaQueryWrapper<SysRelation>()
                    // 查询user
                    .select(SysRelation::getTargetId)
                    // 关系类型
                    .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_USER.getCode())
                    // 指定group
                    .in(SysRelation::getObjectId, groupSet)
            ).forEach(e -> userSet.add(e.getTargetId()));
        }
        return userSet;
    }

    @Override
    public Set<String> userRole(String account) {
        // 用户角色集合
        Set<String> roleSet = new HashSet<>();
        // 查询用户归属的所有分组
        list(new LambdaQueryWrapper<SysRelation>()
                // 查询role
                .select(SysRelation::getObjectId)
                // 关系类型
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_USER.getCode())
                // 指定用户
                .eq(SysRelation::getTargetId, account)
        ).forEach(e -> roleSet.add(e.getObjectId()));
        return roleSet;
    }

    @Override
    public Set<String> roleUser(String roleCode) {
        // 用户角色集合
        Set<String> userSet = new HashSet<>();
        // 查询用户归属的所有分组
        list(new LambdaQueryWrapper<SysRelation>()
                // 查询user
                .select(SysRelation::getTargetId)
                // 关系类型
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_USER.getCode())
                // 指定角色
                .eq(SysRelation::getObjectId, roleCode)
        ).forEach(e -> userSet.add(e.getTargetId()));
        return userSet;
    }

    @Override
    public Set<String> userMenu(String account) {
        // 用户的角色集
        Set<String> roleSet = userRole(account);
        Set<String> groupRoleSet = userGroupRole(account);
        // 两种方式的role集合放在一起
        roleSet.addAll(groupRoleSet);
        return roleMenu(roleSet);
    }

    @Override
    public Set<String> roleMenu(Set<String> roleSet) {
        // 资源集
        Set<String> menuSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(roleSet)) {
            // 查询角色的所有资源菜单
            list(new LambdaQueryWrapper<SysRelation>()
                    // 关系类型
                    .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_PERM.getCode())
                    // 查询menu
                    .select(SysRelation::getTargetId)
                    // 指定role
                    .in(SysRelation::getObjectId, roleSet)
            ).forEach(e -> menuSet.add(e.getTargetId()));
        }
        return menuSet;
    }
}




