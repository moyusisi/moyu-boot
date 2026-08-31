package com.moyu.boot.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.system.enums.RelationTypeEnum;
import com.moyu.boot.system.mapper.SysRelationMapper;
import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.param.SysRelationParam;
import com.moyu.boot.system.service.SysRelationService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户角色权限关系Service实现类
 *
 * @author shisong
 * @since 2024-12-16 21:15:35
 */
@Service
public class SysRelationServiceImpl extends ServiceImpl<SysRelationMapper, SysRelation> implements SysRelationService {

    @Override
    public List<SysRelation> list(SysRelationParam param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定code查询
        queryWrapper.eq(SysRelation::getObjectId, param.getObjectId(), ObjectUtil.isNotEmpty(param.getObjectId()));
        queryWrapper.eq(SysRelation::getTargetId, param.getTargetId(), ObjectUtil.isNotEmpty(param.getTargetId()));
        // 指定codeSet查询
        queryWrapper.in(SysRelation::getObjectId, param.getObjectSet(), ObjectUtil.isNotEmpty(param.getObjectSet()));
        queryWrapper.in(SysRelation::getTargetId, param.getTargetSet(), ObjectUtil.isNotEmpty(param.getTargetSet()));
        // 指定relationType查询
        queryWrapper.eq(SysRelation::getRelationType, param.getRelationType(), ObjectUtil.isNotEmpty(param.getRelationType()));
        // 查询
        List<SysRelation> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public Set<String> userRole(String username) {
        // user查role
        List<String> list = this.objListAs(QueryWrapper.create().select(SysRelation::getTargetId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.USER_HAS_ROLE.getCode())
                .eq(SysRelation::getObjectId, username), String.class);
        return new HashSet<>(list);
    }

    @Override
    public Set<String> roleUser(String roleCode) {
        // role查user
        List<String> list = this.objListAs(QueryWrapper.create().select(SysRelation::getObjectId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.USER_HAS_ROLE.getCode())
                .eq(SysRelation::getTargetId, roleCode), String.class);
        return new HashSet<>(list);
    }

    @Override
    public Set<String> rolePerm(String roleCode) {
        // role查perm
        List<String> list = this.objListAs(QueryWrapper.create().select(SysRelation::getTargetId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_PERM.getCode())
                .eq(SysRelation::getObjectId, roleCode), String.class);
        return new HashSet<>(list);
    }

    @Override
    public Set<String> rolePerm(Set<String> roleSet) {
        List<String> list = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(roleSet)) {
            // role查perm
            list = this.objListAs(QueryWrapper.create().select(SysRelation::getTargetId)
                    .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_PERM.getCode())
                    .in(SysRelation::getObjectId, roleSet), String.class);
        }
        return new HashSet<>(list);
    }

    @Override
    public Set<String> userGroup(String username) {
        // user查group
        List<String> list = this.objListAs(QueryWrapper.create().select(SysRelation::getTargetId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.USER_HAS_GROUP.getCode())
                .eq(SysRelation::getObjectId, username), String.class);
        return new HashSet<>(list);
    }

    @Override
    public Set<String> groupUser(String groupCode) {
        // group查user
        List<String> list = this.objListAs(QueryWrapper.create().select(SysRelation::getObjectId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.USER_HAS_GROUP.getCode())
                .eq(SysRelation::getTargetId, groupCode), String.class);
        return new HashSet<>(list);
    }

    @Override
    public Set<String> groupRole(String groupCode) {
        // group查role
        List<String> list = this.objListAs(QueryWrapper.create().select(SysRelation::getTargetId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_ROLE.getCode())
                .eq(SysRelation::getObjectId, groupCode), String.class);
        return new HashSet<>(list);
    }

    @Override
    public Set<String> roleGroup(String roleCode) {
        // role查group
        List<String> list = this.objListAs(QueryWrapper.create().select(SysRelation::getObjectId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_ROLE.getCode())
                .eq(SysRelation::getTargetId, roleCode), String.class);
        return new HashSet<>(list);
    }

}




