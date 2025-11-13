package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.DefaultNodeParser;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.security.constant.SecurityConstants;
import com.moyu.boot.common.security.util.SecurityUtils;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.enums.RelationTypeEnum;
import com.moyu.boot.system.enums.ResourceTypeEnum;
import com.moyu.boot.system.mapper.SysRoleMapper;
import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.entity.SysResource;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysRelationParam;
import com.moyu.boot.system.model.param.SysResourceParam;
import com.moyu.boot.system.model.param.SysRoleParam;
import com.moyu.boot.system.model.param.SysUserParam;
import com.moyu.boot.system.model.vo.SysRoleVO;
import com.moyu.boot.system.service.SysRelationService;
import com.moyu.boot.system.service.SysResourceService;
import com.moyu.boot.system.service.SysRoleService;
import com.moyu.boot.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色信息服务实现类
 *
 * @author shisong
 * @since 2024-12-15 20:49:43
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private SysRelationService sysRelationService;

    @Resource
    private SysResourceService sysResourceService;

    @Resource
    private SysUserService sysUserService;

    @Override
    public List<SysRoleVO> list(SysRoleParam param) {
        // 查询条件
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.lambdaQuery(SysRole.class);
        // 指定name查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getName()), SysRole::getName, param.getName());
        // 指定code查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), SysRole::getCode, param.getCode());
        // 指定codeSet集合查询
        queryWrapper.in(ObjectUtil.isNotEmpty(param.getCodeSet()), SysRole::getCode, param.getCodeSet());
        // 指定指定状态
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getStatus()), SysRole::getStatus, param.getStatus());
        // 非 ROOT 不可见ROOT
        queryWrapper.ne(!SecurityUtils.isRoot(), SysRole::getCode, SecurityConstants.ROOT_ROLE);
        // 仅查询未删除的
        queryWrapper.eq(SysRole::getDeleted, 0);
        // 排序
        queryWrapper.orderByAsc(SysRole::getSortNum);
        // 查询
        List<SysRole> roleList = this.list(queryWrapper);
        // 转换为voList
        List<SysRoleVO> voList = buildSysRoleVOList(roleList);
        return voList;
    }

    @Override
    public PageData<SysRoleVO> pageList(SysRoleParam param) {
        // 查询条件
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.lambdaQuery(SysRole.class);
        // 指定name查询条件
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getName()), SysRole::getName, param.getName());
        // 指定code查询条件
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), SysRole::getCode, param.getCode());
        // 指定codeSet集合
        queryWrapper.in(ObjectUtil.isNotEmpty(param.getCodeSet()), SysRole::getCode, param.getCodeSet());
        // 指定指定状态
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getStatus()), SysRole::getStatus, param.getStatus());
        // 非 ROOT 不可见ROOT
        queryWrapper.ne(!SecurityUtils.isRoot(), SysRole::getCode, SecurityConstants.ROOT_ROLE);
        // 仅查询未删除的
        queryWrapper.eq(SysRole::getDeleted, 0);
        // 排序
        queryWrapper.orderByAsc(SysRole::getSortNum);
        // 分页查询
        Page<SysRole> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<SysRole> rolePage = this.page(page, queryWrapper);
        List<SysRoleVO> voList = buildSysRoleVOList(rolePage.getRecords());
        return new PageData<>(rolePage.getTotal(), voList);
    }

    @Override
    public SysRoleVO detail(SysRoleParam roleParam) {
        LambdaQueryWrapper<SysRole> queryWrapper = new QueryWrapper<SysRole>().checkSqlInjection().lambda()
                .eq(ObjectUtil.isNotEmpty(roleParam.getId()), SysRole::getId, roleParam.getId())
                .eq(ObjectUtil.isNotEmpty(roleParam.getCode()), SysRole::getCode, roleParam.getCode());
        // id、code均为唯一标识
        SysRole sysRole = this.getOne(queryWrapper);
        if (sysRole == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        SysRoleVO vo = BeanUtil.copyProperties(sysRole, SysRoleVO.class);
        return vo;
    }

    @Override
    public void add(SysRoleParam param) {
        // 若指定了唯一编码code，则必须全局唯一
        if (!Strings.isNullOrEmpty(param.getCode())) {
            // 查询指定code
            SysRole role = this.getOne(new LambdaQueryWrapper<SysRole>()
                    .eq(SysRole::getCode, param.getCode())
                    .eq(SysRole::getDeleted, 0));
            if (role != null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "唯一编码重复，请更换或留空自动生成");
            }
        }
        // 属性复制
        SysRole role = BeanUtil.copyProperties(param, SysRole.class);
        role.setId(null);
        // 若未指定唯一编码code，则自动生成
        if (Strings.isNullOrEmpty(role.getCode())) {
            // 唯一code RandomUtil.randomString(10)、IdUtil.objectId()24位、IdUtil.getSnowflakeNextId()19位
            role.setCode(SysConstants.ROLE_PREFIX + IdUtil.objectId());
        }
        this.save(role);
    }

    @Override
    public void deleteByIds(SysRoleParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 逻辑删除
        UpdateWrapper<SysRole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idSet).set("deleted", 1);
        this.update(updateWrapper);
    }

    @Override
    public void update(SysRoleParam param) {
        // 通过主键id查询原有数据
        SysRole old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        SysRole toUpdate = BeanUtil.copyProperties(param, SysRole.class);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public List<Tree<String>> treeForGrant(SysRoleParam roleParam) {
        // 模块编码
        SysResourceParam query = SysResourceParam.builder().module(roleParam.getModule()).build();
        // 查询所有资源(包括菜单按钮)
        List<SysResource> menuList = sysResourceService.list(query);

        // role已经拥有的资源权限
        Set<String> permSet = sysRelationService.rolePerm(roleParam.getCode());

        // 过滤出button，转为 parentCode->button 格式的的 multimap
        Multimap<String, SysResource> allButtonMap = ArrayListMultimap.create();
        Multimap<String, String> grantButtonMap = HashMultimap.create();
        menuList.stream().filter(e -> ResourceTypeEnum.BUTTON.getCode().equals(e.getResourceType()))
                .forEach(e -> {
                    allButtonMap.put(e.getParentCode(), e);
                    if (permSet.contains(e.getCode())) {
                        grantButtonMap.put(e.getParentCode(), e.getCode());
                    }
                });

        // 过滤出menu转为treeNode
        List<TreeNode<String>> nodeList = new ArrayList<>();
        menuList.stream()
                .filter(e -> !ResourceTypeEnum.BUTTON.getCode().equals(e.getResourceType()))
                .forEach(e -> {
                    TreeNode<String> node = new TreeNode<>(e.getCode(), e.getParentCode(), e.getName(), e.getSortNum());
                    Map<String, Object> extMap = new HashMap<>();
                    if (ResourceTypeEnum.MODULE.getCode().equals(e.getResourceType())) {
                        // 模块只放图标
                        extMap.put("icon", e.getIcon());
                    } else {
                        extMap.put("resourceType", e.getResourceType());
                        // rm关系中存在，表示有权限
                        extMap.put("checked", permSet.contains(e.getCode()));
                        // 将把包含的按钮加进来
                        extMap.put("allButtonList", allButtonMap.get(e.getCode()));
                        extMap.put("grantButtonList", grantButtonMap.get(e.getCode()));
                    }
                    node.setExtra(extMap);
                    nodeList.add(node);
                });

        // 配置TreeNode使用指定的字段名
        TreeNodeConfig nodeConfig = new TreeNodeConfig();
        nodeConfig.setIdKey("code");
        nodeConfig.setParentIdKey("parentCode");
        // 指定rootId
        String rootId = ObjectUtil.isEmpty(roleParam.getModule()) ? SysConstants.ROOT_NODE_ID : roleParam.getModule();
        // 构建树
        return TreeUtil.build(nodeList, rootId, nodeConfig, new DefaultNodeParser<>());
    }

    @Override
    public void grantMenu(SysRoleParam roleParam) {
        // 查询指定模块的所有可授权内容(菜单、按钮、链接)
        List<SysResource> menuList = sysResourceService.list(Wrappers.lambdaQuery(SysResource.class)
                .select(SysResource::getCode)
                // 指定模块
                .eq(SysResource::getModule, roleParam.getModule())
                // 指定菜单类型
                .in(SysResource::getResourceType, ResourceTypeEnum.MENU.getCode(), ResourceTypeEnum.IFRAME.getCode(), ResourceTypeEnum.LINK.getCode(), ResourceTypeEnum.BUTTON.getCode())
                .eq(SysResource::getDeleted, 0));
        // 本模块的所有权限
        List<String> allMenuCode = menuList.stream().map(SysResource::getCode).collect(Collectors.toList());
        // 如果本模块无任何可用资源，则不用授权
        if (ObjectUtil.isEmpty(allMenuCode)) {
            return;
        }
        // 本次授权内容
        Set<String> grantMenuSet = roleParam.getGrantMenuList();
        // 本次授权内容中，仅保留可授权部分(目录不可授权)
        grantMenuSet.retainAll(allMenuCode);

        // 删除旧权限和添加新权限放在一个事务中，有异常会自动回滚(使用模板事物精确控制粒度)
        transactionTemplate.execute((transactionStatus) -> {
            // TransactionCallbackWithoutResult 有异常则会自动回滚

            // 清空角色在本模块的所有权限
            sysRelationService.remove(Wrappers.lambdaQuery(SysRelation.class)
                    .eq(SysRelation::getObjectId, roleParam.getCode()).in(SysRelation::getTargetId, allMenuCode));
            // 非空则新加权限
            if (ObjectUtil.isNotEmpty(grantMenuSet)) {
                List<SysRelation> addList = new ArrayList<>();
                grantMenuSet.forEach(code -> {
                    SysRelation relation = new SysRelation();
                    relation.setObjectId(roleParam.getCode());
                    relation.setTargetId(code);
                    relation.setRelationType(RelationTypeEnum.ROLE_HAS_PERM.getCode());
                    addList.add(relation);
                });
                sysRelationService.saveBatch(addList);
            }
            return null;
        });
    }

    @Override
    public void roleAddUser(SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色code不能为空");
        // 待授权的用户集合
        Set<String> userSet = roleParam.getCodeSet();
        if (ObjectUtil.isEmpty(userSet)) {
            return;
        }
        // 查询该角色已拥有的用户
        Set<String> oldUserSet = new HashSet<>();
        sysRelationService.list(new LambdaQueryWrapper<SysRelation>()
                // 只查询user的code
                .select(SysRelation::getTargetId)
                // 关系类型
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_USER.getCode())
                // 指定role
                .eq(SysRelation::getObjectId, roleParam.getCode())
        ).forEach(e -> oldUserSet.add(e.getTargetId()));

        // 去除已有角色的用户
        userSet.removeAll(oldUserSet);
        // 无需新添加则返回
        if (ObjectUtil.isEmpty(userSet)) {
            return;
        }
        // 添加 ROLE_HAS_USER 关系
        List<SysRelation> addList = new ArrayList<>();
        userSet.forEach(code -> {
            SysRelation entity = new SysRelation();
            entity.setObjectId(roleParam.getCode());
            entity.setTargetId(code);
            entity.setRelationType(RelationTypeEnum.ROLE_HAS_USER.getCode());
            addList.add(entity);
        });
        sysRelationService.saveBatch(addList);
    }

    @Override
    public void roleDeleteUser(SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色coe不能为空");
        // 待撤销授权的用户集合
        Set<String> userSet = roleParam.getCodeSet();
        if (ObjectUtil.isEmpty(userSet)) {
            return;
        }
        // 要删除的ids
        Set<Long> ids = new HashSet<>();
        // 查询指定userSet中已存在的于指定role的user，加入ids待删
        sysRelationService.list(SysRelationParam.builder().objectId(roleParam.getCode()).targetSet(userSet)
                .relationType(RelationTypeEnum.ROLE_HAS_USER.getCode()).build()
        ).forEach(e -> ids.add(e.getId()));
        // 删除
        if (ObjectUtil.isNotEmpty(ids)) {
            sysRelationService.removeByIds(ids);
        }
    }

    @Override
    public List<SysUser> roleUserList(SysRoleParam roleParam) {
        // 查询指定role的所有user
        Set<String> userSet = sysRelationService.roleUser(roleParam.getCode());
        if (ObjectUtil.isEmpty(userSet)) {
            return new ArrayList<>();
        }
        // 查询用户(可指定搜索词)
        List<SysUser> userList = sysUserService.list(SysUserParam.builder()
                .name(roleParam.getSearchKey())
                .orgCode(roleParam.getOrgCode())
                .codeSet(userSet).build());
        return userList;
    }

    @Override
    public Set<String> userRoles(String username) {
        // 用户直接拥有的角色 ROLE_HAS_USER 关系
        Set<String> roleSet = sysRelationService.userRole(username);
        // 添加默认角色
        roleSet.add(defaultRole());
        return roleSet;
    }

    @Override
    public Set<String> rolePerms(Set<String> roleSet) {
        // 权限标识集合
        Set<String> permSet = new HashSet<>();
        if (ObjectUtil.isEmpty(roleSet)) {
            return permSet;
        }
        // 全部资源集
        Set<String> menuSet = sysRelationService.rolePerm(roleSet);
        if (ObjectUtil.isEmpty(menuSet)) {
            return permSet;
        }
        // 获取资源上的权限标识
        sysResourceService.list(Wrappers.lambdaQuery(SysResource.class).in(SysResource::getCode, menuSet)).forEach(e -> {
            if (ObjectUtil.isNotEmpty(e.getPermission())) {
                permSet.add(e.getPermission());
            }
        });
        return permSet;
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<SysRoleVO> buildSysRoleVOList(List<SysRole> entityList) {
        List<SysRoleVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (SysRole entity : entityList) {
            SysRoleVO vo = BeanUtil.copyProperties(entity, SysRoleVO.class);
            voList.add(vo);
        }
        return voList;
    }
}




