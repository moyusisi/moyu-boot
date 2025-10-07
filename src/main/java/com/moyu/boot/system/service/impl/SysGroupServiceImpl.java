package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.security.util.SecurityUtils;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.enums.RelationTypeEnum;
import com.moyu.boot.system.mapper.SysGroupMapper;
import com.moyu.boot.system.model.entity.SysGroup;
import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysGroupParam;
import com.moyu.boot.system.model.param.SysRelationParam;
import com.moyu.boot.system.model.param.SysRoleParam;
import com.moyu.boot.system.model.param.SysUserParam;
import com.moyu.boot.system.model.vo.SysGroupVO;
import com.moyu.boot.system.model.vo.SysRoleVO;
import com.moyu.boot.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色组服务实现类
 *
 * @author shisong
 * @since 2024-12-20 14:29:15
 */
@Slf4j
@Service
public class SysGroupServiceImpl extends ServiceImpl<SysGroupMapper, SysGroup> implements SysGroupService {

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private SysRelationService sysRelationService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysUserService sysUserService;

    @Override
    public List<SysGroup> list(SysGroupParam param) {
        // 查询条件
        LambdaQueryWrapper<SysGroup> queryWrapper = Wrappers.lambdaQuery(SysGroup.class);
        // 指定name查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getName()), SysGroup::getName, param.getName());
        // 指定code查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), SysGroup::getCode, param.getCode());
        // 指定orgCode查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getOrgCode()), SysGroup::getOrgCode, param.getOrgCode());
        // 指定status查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getStatus()), SysGroup::getStatus, param.getStatus());
        // 仅查询未删除的
        queryWrapper.eq(SysGroup::getDeleted, 0);
        // 指定排序
        queryWrapper.orderByAsc(SysGroup::getSortNum);
        // 查询
        List<SysGroup> groupList = this.list(queryWrapper);
        return groupList;
    }

    @Override
    public PageData<SysGroupVO> pageList(SysGroupParam param) {
        // 查询条件
        LambdaQueryWrapper<SysGroup> queryWrapper = Wrappers.lambdaQuery(SysGroup.class);
        // 指定name查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getName()), SysGroup::getName, param.getName());
        // 指定code查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), SysGroup::getCode, param.getCode());
        // 指定orgCode查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getOrgCode()), SysGroup::getOrgCode, param.getOrgCode());
        // 指定status查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getStatus()), SysGroup::getStatus, param.getStatus());
        // 仅查询未删除的
        queryWrapper.eq(SysGroup::getDeleted, 0);
        // 指定排序
        queryWrapper.orderByAsc(SysGroup::getSortNum);
        // 非ROOT则限制数据权限
        if (!SecurityUtils.isRoot()) {
            // 指定的列名
            Integer dataScope = SecurityUtils.getDataScope();
            if (DataScopeEnum.SELF.getCode().equals(dataScope)) {
                String username = SecurityUtils.getUsername();
                queryWrapper.and(e -> e.eq(SysGroup::getCreateBy, username));
            } else if (DataScopeEnum.ORG.getCode().equals(dataScope)) {
                String orgCode = SecurityUtils.getOrgCode();
                queryWrapper.and(e -> e.eq(SysGroup::getOrgCode, orgCode));
            } else if (DataScopeEnum.ORG_CHILD.getCode().equals(dataScope)) {
                String orgCode = SecurityUtils.getOrgCode();
                // find_in_set函数比like高效
//                queryWrapper.and(e -> e.eq(SysGroup::getOrgCode, orgCode).or().like(SysGroup::getOrgPath, orgCode));
                queryWrapper.and(e -> e.eq(SysGroup::getOrgCode, orgCode).or().apply("find_in_set('" + orgCode + "', org_path)"));
            } else if (DataScopeEnum.ORG_DEFINE.getCode().equals(dataScope)) {
                Set<String> scopes = SecurityUtils.getScopes();
                queryWrapper.and(e -> e.in(SysGroup::getOrgCode, scopes));
            }
            log.debug("数据权限为:{}, 已追加过滤条件", DataScopeEnum.getByCode(dataScope));
        }
        // 分页查询
        Page<SysGroup> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<SysGroup> groupPage = this.page(page, queryWrapper);
        List<SysGroupVO> voList = buildGroupVOList(groupPage.getRecords());
        return new PageData<>(groupPage.getTotal(), voList);
    }

    @Override
    public SysGroupVO detail(SysGroupParam param) {
        // 查询条件 id、code均为唯一标识
        LambdaQueryWrapper<SysGroup> queryWrapper = Wrappers.lambdaQuery(SysGroup.class);
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getId()), SysGroup::getId, param.getId());
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), SysGroup::getCode, param.getCode());
        SysGroup sysGroup = this.getOne(queryWrapper);
        if (sysGroup == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        // 转换为vo
        SysGroupVO vo = BeanUtil.copyProperties(sysGroup, SysGroupVO.class);
        return vo;
    }

    @Override
    public void add(SysGroupParam param) {
        // 属性复制
        SysGroup group = BeanUtil.copyProperties(param, SysGroup.class);
        group.setId(null);
        // 若未指定唯一编码code，则自动生成
        if (Strings.isNullOrEmpty(group.getCode())) {
            // 唯一code RandomUtil.randomString(10)、IdUtil.objectId()24位、IdUtil.getSnowflakeNextId()19位
            group.setCode(SysConstants.GROUP_PREFIX + IdUtil.getSnowflakeNextId());
        }
        // 若指定了直属组织，则设置所属组织
        if (ObjectUtil.isNotEmpty(group.getOrgCode())) {
            // 获取组织结构树
            Tree<String> rootTree = sysOrgService.singleTree();
            Tree<String> orgNode = rootTree.getNode(group.getOrgCode());
            // 设置直属机构名称
            group.setOrgName(orgNode.getName().toString());
            // 组织机构层级路径,逗号分隔,父节点在后
            List<String> list = TreeUtil.getParentsId(orgNode, true);
            group.setOrgPath(SysConstants.COMMA_JOINER.join(list));
        }
        // 若是自定义数据范围,需要处理
        if (ObjectUtil.equal(param.getDataScope(), DataScopeEnum.ORG_DEFINE.getCode())) {
            Assert.notEmpty(param.getScopeSet(), "自定义数据范围时, scopeSet不能为空");
            group.setScopeSet(param.getScopeSet());
        }
        this.save(group);
    }

    @Override
    public void deleteByIds(SysGroupParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 逻辑删除
        LambdaUpdateWrapper<SysGroup> updateWrapper = Wrappers.lambdaUpdate(SysGroup.class);
        updateWrapper.in(SysGroup::getId, idSet).set(SysGroup::getDeleted, 1);
        this.update(updateWrapper);
    }

    @Override
    public void update(SysGroupParam param) {
        // 通过主键id查询原有数据
        SysGroup oldGroup = this.getById(param.getId());
        if (oldGroup == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "更新失败，未查到原数据");
        }
        // 属性复制
        SysGroup updateGroup = BeanUtil.copyProperties(param, SysGroup.class);
        updateGroup.setId(oldGroup.getId());
        // 若新指定了直属组织，则设置组织名
        if (ObjectUtil.notEqual(oldGroup.getOrgCode(), updateGroup.getOrgCode()) && ObjectUtil.isNotEmpty(updateGroup.getOrgCode())) {
            // 获取组织结构树
            Tree<String> rootTree = sysOrgService.singleTree();
            Tree<String> orgNode = rootTree.getNode(updateGroup.getOrgCode());
            // 设置直属机构名称
            updateGroup.setOrgName(orgNode.getName().toString());
            // 组织机构层级路径,逗号分隔,父节点在后
            List<String> list = TreeUtil.getParentsId(orgNode, true);
            updateGroup.setOrgPath(SysConstants.COMMA_JOINER.join(list));
        }
        // 若是自定义数据范围,需要处理
        if (ObjectUtil.equal(param.getDataScope(), DataScopeEnum.ORG_DEFINE.getCode())) {
            Assert.notEmpty(param.getScopeSet(), "自定义数据范围时, scopeSet不能为空");
            updateGroup.setScopeSet(param.getScopeSet());
        }
        this.updateById(updateGroup);
    }

    @Override
    public List<SysRoleVO> groupRoleList(SysGroupParam param) {
        // 查询指定group的所有role
        List<SysRelation> list = sysRelationService.list(SysRelationParam.builder()
                .relationType(RelationTypeEnum.GROUP_HAS_ROLE.getCode()).objectId(param.getCode()).build());
        if (ObjectUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        // roleSet
        Set<String> roleSet = list.stream().map(SysRelation::getTargetId).collect(Collectors.toSet());
        // 查询角色(可指定搜索词)
        List<SysRoleVO> roleList = sysRoleService.list(SysRoleParam.builder().name(param.getSearchKey()).codeSet(roleSet).build());
        return roleList;
    }

    @Override
    public List<SysUser> groupUserList(SysGroupParam param) {
        // 查询指定group的所有user
        List<SysRelation> list = sysRelationService.list(SysRelationParam.builder()
                .relationType(RelationTypeEnum.GROUP_HAS_USER.getCode()).objectId(param.getCode()).build());
        if (ObjectUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        // userSet
        Set<String> userSet = list.stream().map(SysRelation::getTargetId).collect(Collectors.toSet());
        // 查询用户(可指定搜索词)
        List<SysUser> userList = sysUserService.list(SysUserParam.builder()
                .name(param.getSearchKey())
                .orgCode(param.getOrgCode())
                .codeSet(userSet).build());
        return userList;
    }

    @Override
    public List<SysGroup> userGroupList(String username) {
        // 查询指定user的所有group
        List<SysRelation> list = sysRelationService.list(SysRelationParam.builder()
                .relationType(RelationTypeEnum.GROUP_HAS_USER.getCode()).targetId(username).build());
        if (ObjectUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        // groupSet
        Set<String> groupSet = list.stream().map(SysRelation::getObjectId).collect(Collectors.toSet());
        // 查询岗位分组
        List<SysGroup> groupList = this.list(Wrappers.lambdaQuery(SysGroup.class)
                .in(SysGroup::getCode, groupSet)
                .eq(SysGroup::getStatus, 0)
                .eq(SysGroup::getDeleted, 0)
        );
        return groupList;
    }

    @Override
    public void groupAddRole(SysGroupParam param) {
        String objectId = param.getCode();
        Set<String> targetSet = param.getCodeSet();
        if (ObjectUtil.isEmpty(targetSet)) {
            return;
        }
        Set<String> oldSet = new HashSet<>();
        // 查询指定group包含的role，放入oldSet
        sysRelationService.list(SysRelationParam.builder().objectId(objectId).targetSet(targetSet)
                .relationType(RelationTypeEnum.GROUP_HAS_ROLE.getCode()).build()
        ).forEach(e -> oldSet.add(e.getTargetId()));
        // 从target中删除已经存在的
        targetSet.removeAll(oldSet);
        // 再次判断要新增的内容为空则返回
        if (ObjectUtil.isEmpty(targetSet)) {
            return;
        }
        List<SysRelation> addList = new ArrayList<>();
        targetSet.forEach(code -> {
            SysRelation entity = new SysRelation();
            entity.setObjectId(objectId);
            entity.setTargetId(code);
            entity.setRelationType(RelationTypeEnum.GROUP_HAS_ROLE.getCode());
            addList.add(entity);
        });
        sysRelationService.saveBatch(addList);
    }

    @Override
    public void groupDeleteRole(SysGroupParam param) {
        if (ObjectUtil.isEmpty(param.getCodeSet())) {
            return;
        }
        // 要删除的ids
        Set<Long> ids = new HashSet<>();
        // 查询指定group中存在的role，加入ids待删
        sysRelationService.list(SysRelationParam.builder().objectId(param.getCode()).targetSet(param.getCodeSet())
                .relationType(RelationTypeEnum.GROUP_HAS_ROLE.getCode()).build()
        ).forEach(e -> ids.add(e.getId()));
        // 删除
        if (ObjectUtil.isNotEmpty(ids)) {
            sysRelationService.removeByIds(ids);
        }
    }

    @Override
    public void groupAddUser(SysGroupParam param) {
        String objectId = param.getCode();
        Set<String> userSet = param.getCodeSet();
        if (ObjectUtil.isEmpty(userSet)) {
            return;
        }
        // 已加入分组的用户
        Set<String> oldUserSet = new HashSet<>();
        Set<String> otherGroupUserSet = new HashSet<>();
        // 查询指定group包含的user，放入oldSet
        sysRelationService.list(Wrappers.lambdaQuery(SysRelation.class)
                .in(SysRelation::getTargetId, userSet)
                .eq(SysRelation::getRelationType, RelationTypeEnum.GROUP_HAS_USER.getCode())
        ).forEach(e -> {
            if (objectId.equals(e.getObjectId())) {
                oldUserSet.add(e.getTargetId());
            } else {
                otherGroupUserSet.add(e.getTargetId());
            }
        });
        // 限制用户只允许加入一个分组
//        if (ObjectUtil.isNotEmpty(otherGroupUserSet)) {
//            String message = String.format("用户%s已加入其他分组，不可重复添加", otherGroupUserSet);
//            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER.getCode(), message);
//        }
        // 从target中删除已经存在的
        userSet.removeAll(oldUserSet);
        // 再次判断要新增的内容为空则返回
        if (ObjectUtil.isEmpty(userSet)) {
            return;
        }
        List<SysRelation> addList = new ArrayList<>();
        userSet.forEach(code -> {
            SysRelation entity = new SysRelation();
            entity.setObjectId(objectId);
            entity.setTargetId(code);
            entity.setRelationType(RelationTypeEnum.GROUP_HAS_USER.getCode());
            addList.add(entity);
        });
        sysRelationService.saveBatch(addList);
    }

    @Override
    public void groupDeleteUser(SysGroupParam param) {
        if (ObjectUtil.isEmpty(param.getCodeSet())) {
            return;
        }
        // 要删除的ids
        Set<Long> ids = new HashSet<>();
        // 查询指定group中存在的user，加入ids待删
        sysRelationService.list(SysRelationParam.builder().objectId(param.getCode()).targetSet(param.getCodeSet())
                .relationType(RelationTypeEnum.GROUP_HAS_USER.getCode()).build()
        ).forEach(e -> ids.add(e.getId()));
        // 删除
        if (ObjectUtil.isNotEmpty(ids)) {
            sysRelationService.removeByIds(ids);
        }
    }

    @Override
    public Set<String> groupDataScopes(String groupCode) {
        // 通过唯一标识code查询group
        SysGroup group = this.getOne(Wrappers.lambdaQuery(SysGroup.class).eq(SysGroup::getCode, groupCode));
        if (group == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        Set<String> scopes = new HashSet<>();

        if (ObjectUtil.equal(group.getDataScope(), DataScopeEnum.ORG.getCode())) {
            scopes.add(group.getOrgCode());
        } else if (ObjectUtil.equal(group.getDataScope(), DataScopeEnum.ORG_DEFINE.getCode())) {
            List<String> list = SysConstants.COMMA_SPLITTER.splitToList(group.getScopeSet());
            scopes.addAll(list);
        } else if (ObjectUtil.equal(group.getDataScope(), DataScopeEnum.ORG_CHILD.getCode())) {
            // 添加org
            scopes.add(group.getOrgCode());
            // 从rootTree中获取所有child（有缓存时）
            Tree<String> orgTree = sysOrgService.singleTree().getNode(group.getOrgCode());
            orgTree.walk(node -> scopes.add(node.getId()));
            // 从数据库中获取所有child（无缓存时）
//                List<String> childList = sysOrgService.childrenCodeList(e.getOrgCode());
//                scopes.addAll(childList);
        }
        return scopes;
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<SysGroupVO> buildGroupVOList(List<SysGroup> entityList) {
        List<SysGroupVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (SysGroup entity : entityList) {
            SysGroupVO vo = BeanUtil.copyProperties(entity, SysGroupVO.class);
            voList.add(vo);
        }
        return voList;
    }
}




