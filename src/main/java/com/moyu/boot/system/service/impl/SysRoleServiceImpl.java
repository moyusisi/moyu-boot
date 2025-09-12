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
import com.moyu.boot.system.enums.StatusEnum;
import com.moyu.boot.system.mapper.SysRoleMapper;
import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.entity.SysResource;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysRelationParam;
import com.moyu.boot.system.model.param.SysResourceParam;
import com.moyu.boot.system.model.param.SysRoleParam;
import com.moyu.boot.system.model.param.SysUserParam;
import com.moyu.boot.system.service.SysRelationService;
import com.moyu.boot.system.service.SysResourceService;
import com.moyu.boot.system.service.SysRoleService;
import com.moyu.boot.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shisong
 * @description 针对表【sys_role(角色信息表)】的数据库操作Service实现
 * @createDate 2024-12-15 20:49:43
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
    public List<SysRole> list(SysRoleParam roleParam) {
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.lambdaQuery(SysRole.class)
                // 关键词搜索
                .like(StrUtil.isNotBlank(roleParam.getSearchKey()), SysRole::getName, roleParam.getSearchKey())
                // 指定code集合
                .in(ObjectUtil.isNotEmpty(roleParam.getCodeSet()), SysRole::getCode, roleParam.getCodeSet())
                // 指定状态
                .eq(ObjectUtil.isNotEmpty(roleParam.getStatus()), SysRole::getStatus, roleParam.getStatus())
                // 非 ROOT 则排除
                .ne(!SecurityUtils.isRoot(), SysRole::getCode, SecurityConstants.ROOT_ROLE)
                .eq(SysRole::getDeleteFlag, 0)
                .orderByAsc(SysRole::getSortNum);
        // 查询
        List<SysRole> roleList = this.list(queryWrapper);
        return roleList;
    }

    @Override
    public PageData<SysRole> pageList(SysRoleParam roleParam) {
        // 查询条件
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.lambdaQuery(SysRole.class)
                // 关键词搜索
                .like(StrUtil.isNotBlank(roleParam.getSearchKey()), SysRole::getName, roleParam.getSearchKey())
                // 指定状态
                .eq(ObjectUtil.isNotEmpty(roleParam.getStatus()), SysRole::getStatus, roleParam.getStatus())
                // 非 ROOT 则排除
                .ne(!SecurityUtils.isRoot(), SysRole::getCode, SecurityConstants.ROOT_ROLE)
                .eq(SysRole::getDeleteFlag, 0)
                .orderByAsc(SysRole::getSortNum);
        // 分页查询
        Page<SysRole> page = new Page<>(roleParam.getPageNum(), roleParam.getPageSize());
        Page<SysRole> rolePage = this.page(page, queryWrapper);
        return new PageData<>(rolePage.getTotal(), rolePage.getRecords());
    }

    @Override
    public SysRole detail(SysRoleParam roleParam) {
        LambdaQueryWrapper<SysRole> queryWrapper = new QueryWrapper<SysRole>().checkSqlInjection().lambda()
                .eq(ObjectUtil.isNotEmpty(roleParam.getId()), SysRole::getId, roleParam.getId())
                .eq(ObjectUtil.isNotEmpty(roleParam.getCode()), SysRole::getCode, roleParam.getCode());
        // id、code均为唯一标识
        SysRole sysRole = this.getOne(queryWrapper);
        if (sysRole == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        return sysRole;
    }

    @Override
    public void add(SysRoleParam roleParam) {
        // 若指定了唯一编码code，则必须全局唯一
        if (!Strings.isNullOrEmpty(roleParam.getCode())) {
            // 查询指定code
            SysRole role = this.getOne(new LambdaQueryWrapper<SysRole>()
                    .eq(SysRole::getCode, roleParam.getCode())
                    .eq(SysRole::getDeleteFlag, 0));
            if (role != null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "唯一编码重复，请更换或留空自动生成");
            }
        }
        // 属性复制
        SysRole role = BeanUtil.copyProperties(roleParam, SysRole.class);
        role.setId(null);
        // 若未指定唯一编码code，则自动生成
        if (Strings.isNullOrEmpty(role.getCode())) {
            // 唯一code RandomUtil.randomString(10)、IdUtil.objectId()24位、IdUtil.getSnowflakeNextId()19位
            role.setCode(SysConstants.ROLE_PREFIX + IdUtil.objectId());
        }
        this.save(role);
    }

    @Override
    public void deleteByIds(SysRoleParam roleParam) {
        // 待删除的id集合
        Set<Long> idSet = roleParam.getIds();
        // 逻辑删除
        UpdateWrapper<SysRole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idSet).set("delete_flag", 1);
        this.update(updateWrapper);
    }

    @Override
    public void edit(SysRoleParam roleParam) {
        SysRole oldRole = this.detail(roleParam);
        // 属性复制
        SysRole updateOrg = BeanUtil.copyProperties(roleParam, SysRole.class);
        updateOrg.setId(oldRole.getId());
        this.updateById(updateOrg);
    }

    @Override
    public List<Tree<String>> treeForGrant(SysRoleParam roleParam) {
        // 模块编码
        SysResourceParam query = SysResourceParam.builder().module(roleParam.getModule()).status(StatusEnum.ENABLE.getCode()).build();
        // 查询所有菜单
        List<SysResource> menuList = sysResourceService.list(query);

        // 所有的role-menu关系(menu.code->menu)
        Map<String, SysRelation> rmMap = new HashMap<>();
        sysRelationService.list(Wrappers.lambdaQuery(SysRelation.class)
                        // 指定关系类型
                        .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_PERM.getCode())
                        // 指定哪个role
                        .eq(SysRelation::getObjectId, roleParam.getCode()))
                .forEach(e -> rmMap.put(e.getTargetId(), e));

        // 过滤出button，转为 parentCode->button 格式的的 multimap
        Multimap<String, SysResource> allButtonMap = ArrayListMultimap.create();
        Multimap<String, String> grantButtonMap = HashMultimap.create();
        menuList.stream().filter(e -> ResourceTypeEnum.BUTTON.getCode().equals(e.getResourceType()))
                .forEach(e -> {
                    allButtonMap.put(e.getParentCode(), e);
                    if (rmMap.containsKey(e.getCode())) {
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
                        extMap.put("checked", rmMap.containsKey(e.getCode()));
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
                .eq(SysResource::getDeleteFlag, 0));
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
                .searchKey(roleParam.getSearchKey())
                .orgCode(roleParam.getOrgCode())
                .codeSet(userSet).build());
        return userList;
    }

    @Override
    public Set<String> userAllRoles(String account) {
        Set<String> roleSet = new HashSet<>();
        // 直接授权的角色
        Set<String> userRoleSet = sysRelationService.userRole(account);
        // 分组授权的角色
        Set<String> groupRoleSet = sysRelationService.userGroupRole(account);
        // 全部角色
        roleSet.addAll(userRoleSet);
        roleSet.addAll(groupRoleSet);
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
        Set<String> menuSet = sysRelationService.roleMenu(roleSet);
        // 获取资源上的权限标识
        sysResourceService.list(Wrappers.lambdaQuery(SysResource.class).in(SysResource::getCode, menuSet)).forEach(e -> {
            if (ObjectUtil.isNotEmpty(e.getPermission())) {
                permSet.add(e.getPermission());
            }
        });
        return permSet;
    }
}




