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
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moyu.boot.common.authZ.constant.AuthConstants;
import com.moyu.boot.common.authZ.model.LoginUser;
import com.moyu.boot.common.authZ.util.LoginUserUtils;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.enums.RelationTypeEnum;
import com.moyu.boot.system.enums.ResourceTypeEnum;
import com.moyu.boot.system.mapper.SysRoleMapper;
import com.moyu.boot.system.model.entity.SysApi;
import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.entity.SysResource;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.entity.ext.RelationExt;
import com.moyu.boot.system.model.param.SysRoleParam;
import com.moyu.boot.system.model.param.SysUserParam;
import com.moyu.boot.system.model.vo.PermScopeInfo;
import com.moyu.boot.system.model.vo.SysRoleVO;
import com.moyu.boot.system.model.vo.SysUserVO;
import com.moyu.boot.system.service.*;
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
    @Resource
    private SysOrgService sysOrgService;

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
        queryWrapper.ne(!LoginUserUtils.isRoot(), SysRole::getCode, AuthConstants.ROOT_ROLE);
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
        queryWrapper.ne(!LoginUserUtils.isRoot(), SysRole::getCode, AuthConstants.ROOT_ROLE);
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
        // 删除时先查再删
        List<SysRole> roleList = this.listByIds(idSet);
        // 要删除的和查询到的进行比对
        if (ObjectUtil.notEqual(idSet.size(), roleList.size())) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(SysRole.class).in(SysRole::getId, idSet).set(SysRole::getDeleted, 1));
    }

    @Override
    public void update(SysRoleParam param) {
        // 通过主键id查询原有数据
        SysRole old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        SysRole toUpdate = BeanUtil.copyProperties(param, SysRole.class, BaseEntity.UPDATE_TIME, BaseEntity.UPDATE_BY);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public List<Tree<String>> menuTree(SysRoleParam param) {
        Set<String> roleSet = new HashSet<>();
        if (StrUtil.isNotBlank(param.getCode())) {
            roleSet.add(param.getCode());
        } else {
            Assert.notEmpty(param.getCodeSet(), "codeSet不能为空");
            roleSet.addAll(param.getCodeSet());
        }
        // role拥有的资源权限
        Set<String> permSet = sysRelationService.rolePerm(roleSet);
        // 查询所有模块的所有菜单(不含按钮)
        List<SysResource> menuList = sysResourceService.list(Wrappers.lambdaQuery(SysResource.class)
                .ne(SysResource::getResourceType, ResourceTypeEnum.BUTTON.getCode())
                .eq(ObjectUtil.isNotEmpty(param.getModule()), SysResource::getModule, param.getModule()));

        // 过滤出role有权限的菜单转为treeNode
        List<TreeNode<String>> nodeList = new ArrayList<>();
        menuList.forEach(menu -> {
            TreeNode<String> node = new TreeNode<>(menu.getCode(), menu.getParentCode(), menu.getName(), menu.getSortNum());
            Map<String, Object> extMap = new HashMap<>();
            extMap.put("menuType", menu.getResourceType());
            if (StrUtil.isNotBlank(menu.getIcon())) {
                // 图标
                extMap.put("icon", menu.getIcon());
            }
            node.setExtra(extMap);
            // 目录都包含，叶子结点有权限才包含
            if (ResourceTypeEnum.MODULE.getCode().equals(menu.getResourceType()) || ResourceTypeEnum.DIR.getCode().equals(menu.getResourceType())) {
                nodeList.add(node);
            } else if (permSet.contains(menu.getCode())) {
                nodeList.add(node);
            }
        });

        // 配置TreeNode使用指定的字段名
        TreeNodeConfig nodeConfig = new TreeNodeConfig();
        nodeConfig.setIdKey("code");
        nodeConfig.setParentIdKey("parentCode");
        // 构建树
        Tree<String> singleTree = TreeUtil.buildSingle(nodeList, SysConstants.ROOT_NODE_ID, nodeConfig, new DefaultNodeParser<>());

        // 剪枝,移除空目录(本节点或子节点满足条件，则保留)
        singleTree.filter(tree -> {
            // 排除根
            if (SysConstants.ROOT_NODE_ID.equals(tree.getId())) {
                return false;
            }
            Integer menuType = (Integer) tree.get("menuType");
            // 不是目录则返回true
            boolean notDir = !ResourceTypeEnum.DIR.getCode().equals(menuType) && !ResourceTypeEnum.MODULE.getCode().equals(menuType);
            return notDir;
        });
        return singleTree.getChildren();
    }

    @Override
    public List<Tree<String>> menuTreeForGrant(SysRoleParam param) {
        // 查询模块所有资源(包括菜单按钮)
        List<SysResource> menuList = sysResourceService.list(Wrappers.lambdaQuery(SysResource.class)
                .eq(ObjectUtil.isNotEmpty(param.getModule()), SysResource::getModule, param.getModule()));

        // role已经拥有的资源权限
        Set<String> permSet = sysRelationService.rolePerm(param.getCode());

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
        String rootId = ObjectUtil.isEmpty(param.getModule()) ? SysConstants.ROOT_NODE_ID : param.getModule();
        // 构建树
        return TreeUtil.build(nodeList, rootId, nodeConfig, new DefaultNodeParser<>());
    }

    @Override
    public List<PermScopeInfo> permScopeListForGrant(SysRoleParam param) {
        List<PermScopeInfo> permScopeList = new ArrayList<>();

        // role已经拥有的资源权限 permCode -> Relation
        Map<String, SysRelation> permMap = new HashMap<>();
        Db.list(Wrappers.lambdaQuery(SysRelation.class)
                .eq(SysRelation::getObjectId, param.getCode())
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_PERM.getCode())
                .eq(SysRelation::getDeleted, 0)
        ).forEach(e -> {
            permMap.put(e.getTargetId(), e);
        });

        // role拥有的所有按钮 code -> SysResource
        Map<String, SysResource> btnMap = new HashMap<>();
        Set<String> btnPermSet = new HashSet<>();
        // 查询模块所有按钮
        Db.list(Wrappers.lambdaQuery(SysResource.class)
                        .eq(SysResource::getResourceType, ResourceTypeEnum.BUTTON.getCode())
                        .eq(ObjectUtil.isNotEmpty(param.getModule()), SysResource::getModule, param.getModule())
                ).stream()
                // 过滤出role有权限的按钮
                .filter(btn -> permMap.containsKey(btn.getCode()))
                .forEach(btn -> {
                    btnMap.put(btn.getCode(), btn);
                    btnPermSet.add(btn.getPermission());
                });
        // role无按钮则返回
        if (CollectionUtils.isEmpty(btnMap)) {
            return permScopeList;
        }

        // 按钮关联的接口(必须有数据范围) perm -> SysApi
        Map<String, SysApi> apiMap = new HashMap<>();
        // 查询接口列表
        Db.list(Wrappers.lambdaQuery(SysApi.class)
                // 只要有数据范围的接口
                .eq(SysApi::getHasScope, 1)
                // 指定name查询
                .like(ObjectUtil.isNotEmpty(param.getName()), SysApi::getName, param.getName())
                // 指定path查询
                .like(ObjectUtil.isNotEmpty(param.getSearchKey()), SysApi::getPath, param.getSearchKey())
                // 权限标识
                .in(SysApi::getCode, btnPermSet)
        ).forEach(api -> {
            apiMap.put(api.getCode(), api);
        });

        Gson gson = new GsonBuilder().create();
        // 从btnList中找到已授权的部分
        btnMap.forEach((code, btn) -> {
            SysApi api = apiMap.get(btn.getPermission());
            // apiMap中仅包含有数据范围的接口
            if (ObjectUtil.isNotNull(api)) {
                PermScopeInfo vo = PermScopeInfo.builder()
                        .code(code)
                        .btnName(btn.getName())
                        .name(api.getName())
                        .path(api.getPath())
                        .permission(api.getCode())
                        .build();
                SysRelation relation = permMap.get(code);
                RelationExt.ScopeExt ext = gson.fromJson(relation.getExtJson(), RelationExt.ScopeExt.class);
                if (ext != null) {
                    vo.setDataScope(ext.getDataScope());
                    vo.setScopeList(ext.getScopeList());
                }
                permScopeList.add(vo);
            }
        });
        return permScopeList;
    }

    @Override
    public void grantMenu(SysRoleParam roleParam) {
        // 本模块所有可授权内容(菜单、按钮、链接)
        List<SysResource> moduleMenuList = sysResourceService.list(Wrappers.lambdaQuery(SysResource.class)
                .select(SysResource::getCode)
                // 指定模块
                .eq(SysResource::getModule, roleParam.getModule())
                // 指定菜单类型
                .in(SysResource::getResourceType, ResourceTypeEnum.MENU.getCode(), ResourceTypeEnum.IFRAME.getCode(), ResourceTypeEnum.LINK.getCode(), ResourceTypeEnum.BUTTON.getCode())
                .eq(SysResource::getDeleted, 0));
        // 本模块的所有权限
        List<String> moduleMenuCodeList = moduleMenuList.stream().map(SysResource::getCode).collect(Collectors.toList());
        // 如果本模块无任何可用资源，则不用授权
        if (ObjectUtil.isEmpty(moduleMenuCodeList)) {
            return;
        }
        // 本次授权内容
        Set<String> grantMenuSet = roleParam.getGrantMenuList();
        // 本次授权内容中，仅保留本模块可授权部分(排除目录和非本模块内容)
        grantMenuSet.retainAll(moduleMenuCodeList);
        // role原来已有的权限
        Set<String> oldPermSet = sysRelationService.rolePerm(roleParam.getCode());
        // 仅保留本模块中的老权限
        oldPermSet.retainAll(moduleMenuCodeList);
        // grantMenuSet 和 oldPermSet 都限定在本模块内
        // 要移除的权限 = 老权限 - 新权限
        Set<String> toDeleteSet = new HashSet<>(oldPermSet);
        toDeleteSet.removeAll(grantMenuSet);
        // 本次要新增的权限 = 新权限 - 老权限
        grantMenuSet.removeAll(oldPermSet);

        // 删除旧权限和添加新权限放在一个事务中，有异常会自动回滚(使用模板事物精确控制粒度)
        transactionTemplate.execute((transactionStatus) -> {
            // TransactionCallbackWithoutResult 有异常则会自动回滚

            // 移除本次删除的权限
            if (ObjectUtil.isNotEmpty(toDeleteSet)) {
                sysRelationService.remove(Wrappers.lambdaQuery(SysRelation.class)
                        .eq(SysRelation::getObjectId, roleParam.getCode())
                        .in(SysRelation::getTargetId, toDeleteSet));
            }
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
    public void grantScope(SysRoleParam param) {
        Gson gson = new GsonBuilder().create();
        List<PermScopeInfo> permScopeList = param.getGrantScopeList();
        // 如果无数据则不授权
        if (ObjectUtil.isEmpty(permScopeList)) {
            return;
        }
        Map<String, PermScopeInfo> scopeMap = new HashMap<>();
        permScopeList.forEach(e -> scopeMap.put(e.getCode(), e));
        // 查询角色在本模块的已有权限(role+permCodeSet)
        List<SysRelation> relationList = sysRelationService.list(Wrappers.lambdaQuery(SysRelation.class)
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_PERM.getCode())
                .eq(SysRelation::getObjectId, param.getCode())
                .in(SysRelation::getTargetId, scopeMap.keySet()));
        if (ObjectUtil.isEmpty(relationList)) {
            return;
        }
        Date date = new Date();
        relationList.forEach(relation -> {
            PermScopeInfo info = scopeMap.get(relation.getTargetId());
            RelationExt.ScopeExt scopeExt = new RelationExt.ScopeExt();
            if (info.getDataScope() != null) {
                scopeExt.setDataScope(info.getDataScope());
                // 若是自定义数据范围,需要处理
                if (ObjectUtil.equal(info.getDataScope(), DataScopeEnum.ORG_DEFINE.getCode())) {
                    Assert.notEmpty(info.getScopeList(), "自定义数据范围时, scopeList不能为空");
                    scopeExt.setScopeList(info.getScopeList());
                } else {
                    scopeExt.setScopeList(null);
                }
                relation.setExtJson(gson.toJson(scopeExt));
            } else {
                relation.setExtJson(null);
            }
            // 这个relation为原数据，字段有值不更新，设置为null会自动更新
            relation.setUpdateBy(null);
            relation.setUpdateTime(date);
        });
        sysRelationService.updateBatchById(relationList);
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
        Set<String> oldUserSet = sysRelationService.roleUser(roleParam.getCode());
        // 去除已有角色的用户
        userSet.removeAll(oldUserSet);
        // 无需新添加则返回
        if (ObjectUtil.isEmpty(userSet)) {
            return;
        }
        // 添加 USER_HAS_ROLE 关系
        List<SysRelation> addList = new ArrayList<>();
        userSet.forEach(code -> {
            SysRelation entity = new SysRelation();
            entity.setObjectId(code);
            entity.setTargetId(roleParam.getCode());
            entity.setRelationType(RelationTypeEnum.USER_HAS_ROLE.getCode());
            addList.add(entity);
        });
        sysRelationService.saveBatch(addList);
    }

    @Override
    public void roleDeleteUser(SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色code不能为空");
        // 待撤销授权的用户集合
        Set<String> userSet = roleParam.getCodeSet();
        if (ObjectUtil.isEmpty(userSet)) {
            return;
        }
        // 要删除的ids
        Set<Long> ids = new HashSet<>();
        // 查询指定role中已存在的user，加入ids待删
        Db.list(Wrappers.lambdaQuery(SysRelation.class)
                .eq(SysRelation::getRelationType, RelationTypeEnum.USER_HAS_ROLE.getCode())
                .in(SysRelation::getObjectId, userSet)
                .eq(SysRelation::getTargetId, roleParam.getCode())
        ).forEach(e -> ids.add(e.getId()));
        // 物理删除
        if (ObjectUtil.isNotEmpty(ids)) {
            sysRelationService.removeByIds(ids);
        }
    }

    @Override
    public List<SysUserVO> roleUserList(SysRoleParam param) {
        // 查询指定role的所有user
        Set<String> userSet = sysRelationService.roleUser(param.getCode());
        if (ObjectUtil.isEmpty(userSet)) {
            return new ArrayList<>();
        }
        // 查询用户(可指定搜索词)
        List<SysUserVO> voList = sysUserService.list(SysUserParam.builder()
                .name(param.getSearchKey())
                .orgCode(param.getOrgCode())
                .codeSet(userSet).build());
        return voList;
    }

    @Override
    public Set<String> userRoles(String username) {
        // 用户直接拥有的角色 USER_HAS_ROLE 关系
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
        sysResourceService.list(Wrappers.lambdaQuery(SysResource.class)
                .eq(SysResource::getResourceType, ResourceTypeEnum.BUTTON.getCode())
                .in(SysResource::getCode, menuSet)).forEach(e -> {
            if (ObjectUtil.isNotEmpty(e.getPermission())) {
                permSet.add(e.getPermission());
            }
        });
        return permSet;
    }

    @Override
    public Map<String, LoginUser.DataScopeInfo> roleDataScopeMap(Set<String> roleSet, String orgCode) {
        // 权限标识集合
        Map<String, LoginUser.DataScopeInfo> apiScopeMap = new HashMap<>();
        if (ObjectUtil.isEmpty(roleSet)) {
            return apiScopeMap;
        }
        // roleSet拥有的Relation(包含了菜单+按钮): permCode->SysRelation
        Map<String, SysRelation> allPermMap = new HashMap<>();
        Db.list(Wrappers.lambdaQuery(SysRelation.class)
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_PERM.getCode())
                .in(SysRelation::getObjectId, roleSet)
        ).forEach(e -> allPermMap.put(e.getTargetId(), e));
        if (ObjectUtil.isEmpty(allPermMap)) {
            return apiScopeMap;
        }
        // roleSet拥有的所有按钮 code -> SysResource
        Map<String, SysResource> btnMap = new HashMap<>();
        Set<String> btnPermSet = new HashSet<>();
        // 查询模块所有按钮
        Db.list(Wrappers.lambdaQuery(SysResource.class)
                .eq(SysResource::getResourceType, ResourceTypeEnum.BUTTON.getCode())
                .in(SysResource::getCode, allPermMap.keySet())
                .eq(SysResource::getDeleted, 0)
        ).forEach(btn -> {
            btnMap.put(btn.getCode(), btn);
            btnPermSet.add(btn.getPermission());
        });
        // role无按钮则返回
        if (CollectionUtils.isEmpty(btnMap)) {
            return apiScopeMap;
        }

        // 按钮关联的接口(必须有数据范围)Map:perm -> SysApi
        Map<String, SysApi> apiMap = new HashMap<>();
        // 查询接口列表
        Db.list(Wrappers.lambdaQuery(SysApi.class)
                // 只要有数据范围的接口
                .eq(SysApi::getHasScope, 1)
                // 权限标识
                .in(SysApi::getCode, btnPermSet)
        ).forEach(api -> {
            apiMap.put(api.getCode(), api);
        });

        Gson gson = new GsonBuilder().create();
        // 接口数据范围组装
        btnMap.forEach((code, btn) -> {
            SysApi api = apiMap.get(btn.getPermission());
            if (api != null) {
                // 不同btn可能有不同数据范围
                SysRelation relation = allPermMap.get(btn.getCode());
                RelationExt.ScopeExt scopeExt = gson.fromJson(relation.getExtJson(), RelationExt.ScopeExt.class);
                if (scopeExt != null && scopeExt.getDataScope() != null) {
                    LoginUser.DataScopeInfo info = buildDataScopeInfo(orgCode, scopeExt);
                    if (apiScopeMap.containsKey(api.getPath())) {
                        // 已有重复的，则要合并数据范围
                        LoginUser.DataScopeInfo mergedInfo = mergeDataScope(apiScopeMap.get(api.getPath()), info);
                        apiScopeMap.put(api.getPath(), mergedInfo);
                    } else {
                        // 不重复直接添加
                        apiScopeMap.put(api.getPath(), info);
                    }
                }
            }
        });
        // 对于不限制(DataScopeEnum.ALL)数据范围的接口，为了减少缓存大小，将其移出（即无数据权限时不限制）
        //apiScopeMap.entrySet().removeIf(entry -> DataScopeEnum.ALL.getCode().equals(entry.getValue().getDataScope()));
        return apiScopeMap;
    }

    /**
     * 数据权限范围合并(字典 0无限制 1仅本人数据 2仅本机构 3本机构及以下 4自定义)
     * 合并时优先级为： 1仅本人数据 < 2仅本机构 < 3本机构及以下 < 4本公司及以下 < 5自定义 < 0无限制
     * 1.有无限制则最终为无限制
     * 2.有自定义则最终为自定义，只是需要两项范围合并
     * 3.其他按照优先级返回大的
     */
    private LoginUser.DataScopeInfo mergeDataScope(LoginUser.DataScopeInfo scope1, LoginUser.DataScopeInfo scope2) {
        // 1.有无限制直接返回
        if (scope1.getDataScope() == null || DataScopeEnum.ALL.getCode().equals(scope1.getDataScope())) {
            return scope1;
        }
        if (scope2.getDataScope() == null || DataScopeEnum.ALL.getCode().equals(scope2.getDataScope())) {
            return scope2;
        }
        // 按照 1仅本人数据 < 2仅本机构 < 3本机构及以下 < 4本公司及以下 < 5自定义 排序
        LoginUser.DataScopeInfo max = scope1.getDataScope() > scope2.getDataScope() ? scope1 : scope2;
        LoginUser.DataScopeInfo min = scope1.getDataScope() < scope2.getDataScope() ? scope1 : scope2;

        // 2.有自定义，则把min的范围加入到max然后返回自定义。
        if (max.getDataScope().equals(DataScopeEnum.ORG_DEFINE.getCode())) {
            max.getScopeSet().addAll(min.getScopeSet());
        }
        // 3.其他情况返回max
        return max;
    }

    private LoginUser.DataScopeInfo buildDataScopeInfo(String orgCode, RelationExt.ScopeExt scopeExt) {
        // 未设置过数据范围直接返回
        if (scopeExt == null || scopeExt.getDataScope() == null) {
            return null;
        }
        LoginUser.DataScopeInfo info = new LoginUser.DataScopeInfo();
        // 不限制时设置值，防止null
        info.setDataScope(scopeExt.getDataScope());
        Set<String> scopeSet = new HashSet<>();
        info.setScopeSet(scopeSet);
        if (DataScopeEnum.ORG_CHILD.getCode().equals(info.getDataScope())) {
            // 本机构及以下
            scopeSet.add(orgCode);
            // 从rootTree中获取所有child（有缓存时）
            Tree<String> orgTree = sysOrgService.singleTree().getNode(orgCode);
            orgTree.walk(node -> scopeSet.add(node.getId()));
        } else if (DataScopeEnum.COMPANY.getCode().equals(info.getDataScope())) {
            // 本公司及以下
            Tree<String> rootTree = sysOrgService.singleTree();
            String companyCode = sysOrgService.orgCompany(orgCode, rootTree);
            scopeSet.add(companyCode);
            // 获取所有child
            Tree<String> orgTree = rootTree.getNode(companyCode);
            orgTree.walk(node -> scopeSet.add(node.getId()));
        } else if (DataScopeEnum.ORG_DEFINE.getCode().equals(info.getDataScope())) {
            // 自定义
            scopeSet.addAll(ObjectUtil.defaultIfNull(scopeExt.getScopeList(), new ArrayList<>()));
        }
        return info;
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




