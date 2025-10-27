package com.moyu.boot.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
        LambdaQueryWrapper<SysRelation> queryWrapper = Wrappers.lambdaQuery(SysRelation.class);
        // 指定code查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getObjectId()), SysRelation::getObjectId, param.getObjectId());
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getTargetId()), SysRelation::getTargetId, param.getTargetId());
        // 指定codeSet查询
        queryWrapper.in(ObjectUtil.isNotEmpty(param.getObjectSet()), SysRelation::getObjectId, param.getObjectSet());
        queryWrapper.in(ObjectUtil.isNotEmpty(param.getTargetSet()), SysRelation::getTargetId, param.getTargetSet());
        // 指定relationType查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getRelationType()), SysRelation::getRelationType, param.getRelationType());
        // 查询
        List<SysRelation> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public Set<String> roleUser(String roleCode) {
        // 用户角色集合
        Set<String> userSet = new HashSet<>();
        // role查user
        this.list(Wrappers.lambdaQuery(SysRelation.class)
                .select(SysRelation::getTargetId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_USER.getCode())
                .eq(SysRelation::getObjectId, roleCode)
        ).forEach(e -> userSet.add(e.getTargetId()));
        return userSet;
    }

    @Override
    public Set<String> userRole(String username) {
        Set<String> roleSet = new HashSet<>();
        // user查role
        this.list(Wrappers.lambdaQuery(SysRelation.class)
                .select(SysRelation::getObjectId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_USER.getCode())
                .eq(SysRelation::getTargetId, username)
        ).forEach(e -> roleSet.add(e.getObjectId()));
        return roleSet;
    }

    @Override
    public Set<String> rolePerm(String roleCode) {
        Set<String> permSet = new HashSet<>();
        // role查perm
        this.list(Wrappers.lambdaQuery(SysRelation.class)
                .select(SysRelation::getTargetId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_PERM.getCode())
                .eq(SysRelation::getObjectId, roleCode)
        ).forEach(e -> permSet.add(e.getTargetId()));
        return permSet;
    }

    @Override
    public Set<String> rolePerm(Set<String> roleSet) {
        Set<String> permSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(roleSet)) {
            // role查perm
            this.list(Wrappers.lambdaQuery(SysRelation.class)
                    .select(SysRelation::getTargetId)
                    .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_PERM.getCode())
                    .in(SysRelation::getObjectId, roleSet)
            ).forEach(e -> permSet.add(e.getTargetId()));
        }
        return permSet;
    }

    @Override
    public Set<String> groupUser(String groupCode) {
        Set<String> userSet = new HashSet<>();
        // group查user
        list(Wrappers.lambdaQuery(SysRelation.class)
                .select(SysRelation::getTargetId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_USER.getCode())
                .eq(SysRelation::getObjectId, groupCode)
        ).forEach(e -> userSet.add(e.getTargetId()));
        return userSet;
    }

    @Override
    public Set<String> userGroup(String username) {
        Set<String> groupSet = new HashSet<>();
        // role查group
        list(Wrappers.lambdaQuery(SysRelation.class)
                .select(SysRelation::getObjectId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_USER.getCode())
                .eq(SysRelation::getTargetId, username)
        ).forEach(e -> groupSet.add(e.getObjectId()));
        return groupSet;
    }

    @Override
    public Set<String> groupRole(String groupCode) {
        Set<String> roleSet = new HashSet<>();
        // group查role
        list(Wrappers.lambdaQuery(SysRelation.class)
                .select(SysRelation::getTargetId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_ROLE.getCode())
                .eq(SysRelation::getObjectId, groupCode)
        ).forEach(e -> roleSet.add(e.getTargetId()));
        return roleSet;
    }

    @Override
    public Set<String> roleGroup(String roleCode) {
        Set<String> groupSet = new HashSet<>();
        // role查group
        list(Wrappers.lambdaQuery(SysRelation.class)
                .select(SysRelation::getObjectId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_ROLE.getCode())
                .eq(SysRelation::getTargetId, roleCode)
        ).forEach(e -> groupSet.add(e.getObjectId()));
        return groupSet;
    }

}




