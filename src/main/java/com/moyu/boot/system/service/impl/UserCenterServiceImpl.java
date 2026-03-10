package com.moyu.boot.system.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.DefaultNodeParser;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.authZ.model.LoginUser;
import com.moyu.boot.common.authZ.service.TokenService;
import com.moyu.boot.common.authZ.util.LoginUserUtils;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.enums.ResourceTypeEnum;
import com.moyu.boot.system.model.entity.SysGroup;
import com.moyu.boot.system.model.entity.SysResource;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.entity.ext.ResourceExt;
import com.moyu.boot.system.model.param.SysRoleParam;
import com.moyu.boot.system.model.vo.GroupInfo;
import com.moyu.boot.system.model.vo.Meta;
import com.moyu.boot.system.model.vo.SysRoleVO;
import com.moyu.boot.system.model.vo.UserInfo;
import com.moyu.boot.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shisong
 * @since 2025-01-07
 */
@Slf4j
@Service
public class UserCenterServiceImpl implements UserCenterService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysResourceService sysResourceService;

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysGroupService sysGroupService;

    @Resource
    private SysRelationService sysRelationService;

    @Resource
    private TokenService tokenService;

    @Override
    public UserInfo currentUserInfo(String username) {
        // 当前登陆用户
        Optional<LoginUser> optUser = LoginUserUtils.getLoginUser();
        if (!optUser.isPresent()) {
            throw new BusinessException(ResultCodeEnum.USER_LOGIN_CHECK_ERROR);
        }
        LoginUser loginUser = optUser.get();
        // 查询用户
        SysUser user = sysUserService.getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getAccount, username));
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 构造用户信息视图对象
        UserInfo userInfo = UserInfo.builder().account(username).orgCode(loginUser.getOrgCode())
                .name(user.getName()).nickName(user.getNickName()).avatar(user.getAvatar())
                .perms(loginUser.getPerms()).roles(loginUser.getRoles())
                .groupCode(loginUser.getGroupCode())
                .build();
        // 岗位列表
        List<SysGroup> groupList = sysGroupService.userGroupList(username);
        SysGroup defaultGroup = sysGroupService.userDefaultGroup(username);
        groupList.add(defaultGroup);
        if (ObjectUtil.isNotEmpty(groupList)) {
            Tree<String> orgTree = sysOrgService.singleTree();
            List<GroupInfo> groupInfoList = new ArrayList<>();
            groupList.forEach(e -> {
                List<CharSequence> nameList = TreeUtil.getParentsName(orgTree.getNode(e.getOrgCode()), true);
                // nameList进行反转
                Collections.reverse(nameList);
                String fullName = Joiner.on("-").skipNulls().join(nameList);
                GroupInfo groupInfo = GroupInfo.builder().code(e.getCode()).name(e.getName())
                        .orgCode(e.getOrgCode()).orgName(e.getOrgName()).orgFullName(fullName).build();
                groupInfoList.add(groupInfo);
            });
            userInfo.setGroupInfoList(groupInfoList);
        }
        return userInfo;
    }

    @Override
    public List<Tree<String>> userMenu(String username) {
        Optional<LoginUser> optUser = LoginUserUtils.getLoginUser();
        if (!optUser.isPresent()) {
            throw new BusinessException(ResultCodeEnum.USER_LOGIN_CHECK_ERROR);
        }
        Set<String> roleSet = LoginUserUtils.getRoles();
        // 用户有权限的资源code集合(含按钮)
        Set<String> permSet = sysRelationService.rolePerm(roleSet);
        //  无任何权限直接返回
        if (CollectionUtils.isEmpty(permSet) && !LoginUserUtils.isRoot()) {
            return Lists.newArrayList();
        }
        // 查询所有的菜单(不含按钮)
        List<SysResource> menuList = sysResourceService.list(Wrappers.lambdaQuery(SysResource.class)
                // 不能是按钮
                .ne(SysResource::getResourceType, ResourceTypeEnum.BUTTON.getCode())
                .eq(SysResource::getDeleted, 0)
                .orderByAsc(SysResource::getSortNum)
        );
        // 用户有权限的菜单(不含按钮) + 所有模块、目录
        List<SysResource> userMenuList = CollectionUtil.newArrayList();
        menuList.forEach(sysMenu -> {
            if (ResourceTypeEnum.MODULE.getCode().equals(sysMenu.getResourceType())) {
                // 前端的路由对象path不能为空
                if (ObjectUtil.isEmpty(sysMenu.getPath())) {
                    sysMenu.setPath(StrUtil.SLASH + sysMenu.getCode());
                }
                userMenuList.add(sysMenu);
            } else if (ResourceTypeEnum.DIR.getCode().equals(sysMenu.getResourceType())) {
                userMenuList.add(sysMenu);
            } else {
                // 有权限才添加(菜单、内链、外链等)
                if (LoginUserUtils.isRoot() || permSet.contains(sysMenu.getCode())) {
                    userMenuList.add(sysMenu);
                }
            }
        });
        // 构建菜单路由树结构
        Tree<String> singleTree = buildMenuTree(userMenuList, SysConstants.ROOT_NODE_ID);

        // 移除空目录(本节点或子节点满足条件，则保留当前节点及其所有子节点)
        singleTree.filter(tree -> {
            // id=0或parentId=0均不符合要求(排除根和模块)
            if (SysConstants.ROOT_NODE_ID.equals(tree.getId()) || SysConstants.ROOT_NODE_ID.equals(tree.getParentId())) {
                return false;
            }
            if (ObjectUtil.isNotEmpty(tree.get("meta"))) {
                Meta meta = (Meta) tree.get("meta");
                String metaType = meta.getType();
                // 结点不是目录则保留
                boolean notDir = !ResourceTypeEnum.DIR.name().equalsIgnoreCase(metaType) && !ResourceTypeEnum.MODULE.name().equalsIgnoreCase(metaType);
                return notDir;
            } else {
                return false;
            }
        });
        return singleTree.getChildren();
    }

    @Override
    public List<SysRoleVO> userRoleList(String roleName) {
        // 当前登陆用户
        if (!LoginUserUtils.getLoginUser().isPresent()) {
            throw new BusinessException(ResultCodeEnum.USER_LOGIN_CHECK_ERROR);
        }
        if (LoginUserUtils.isRoot()) {
            // root拥有所有角色
            return sysRoleService.list(SysRoleParam.builder().name(roleName).build());
        }
        // 当前用户的角色
        Set<String> roleSet = LoginUserUtils.getRoles();
        return sysRoleService.list(SysRoleParam.builder().codeSet(roleSet).name(roleName).build());
    }

    @Override
    public void switchUserGroup(String groupCode) {
        LoginUser loginUser = LoginUserUtils.getLoginUser().orElse(null);
        if (loginUser == null) {
            throw new BusinessException(ResultCodeEnum.USER_LOGIN_CHECK_ERROR);
        }
        String username = loginUser.getUsername();
        SysGroup group = null;
        Set<String> roleSet = null;
        if (sysGroupService.defaultGroup().equals(groupCode)) {
            // 默认岗位
            group = sysGroupService.userDefaultGroup(username);
            // 直接角色 role-user
            roleSet = sysRoleService.userRoles(username);
        } else {
            // 通过唯一标识code查询group
            group = sysGroupService.getOne(Wrappers.lambdaQuery(SysGroup.class).eq(SysGroup::getCode, groupCode).eq(SysGroup::getDeleted, 0));
            if (group == null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "切换失败，未查到岗位数据");
            }
            // 岗位角色 group-role
            roleSet = sysRelationService.groupRole(group.getCode());
        }
        // 根据切换的岗位更新loginUser
        loginUser.setOrgCode(group.getOrgCode());
        loginUser.setGroupCode(group.getCode());
        // 岗位关联的角色
        loginUser.setRoles(roleSet);
        // 岗位权限 权限标识集合(仅接口,无菜单)
        loginUser.setPerms(sysRoleService.rolePerms(roleSet));
        // 接口权限的数据范围
        loginUser.setPermScopeMap(sysRoleService.rolePermScopeMap(roleSet, group.getOrgCode()));
        // 数据范围默认本人数据，真正的数据范围在PreDataScope切面中赋值
        loginUser.setDataScope(DataScopeEnum.SELF.getCode());
        tokenService.switchUser(loginUser);
    }

    /**
     * 构建菜单路由树结构(code, parentCode, children, weight, extra)
     *
     * @param menuList menu的非空字段构会放到树节点中
     * @param rootId   指定的根节点(从树中查找此rootId)
     * @return 返回以rootId为根的树，可能是子树或多棵树
     */
    private Tree<String> buildMenuTree(List<SysResource> menuList, String rootId) {
        // 配置TreeNode使用指定的字段名
        TreeNodeConfig nodeConfig = new TreeNodeConfig();
        nodeConfig.setIdKey("code");
        nodeConfig.setParentIdKey("parentCode");
        Gson gson = new GsonBuilder().create();
        // 结构转换
        List<TreeNode<String>> treeNodeList = menuList.stream()
                .map(menu -> {
                    ResourceTypeEnum resourceType = ResourceTypeEnum.getByCode(menu.getResourceType());
                    TreeNode<String> node = new TreeNode<>(menu.getCode(), menu.getParentCode(), menu.getName(), menu.getSortNum());
                    // path、name、component、redirect、hidden
                    Map<String, Object> extra = new HashMap<>();//BeanUtil.beanToMap(menu, false, true);
                    extra.put("path", menu.getPath());
                    extra.put("component", menu.getComponent());
                    if (ResourceTypeEnum.DIR.equals(resourceType)) {
                        extra.put("redirect", menu.getLink());
                    } else if (ResourceTypeEnum.MODULE.equals(resourceType)) {
                        extra.put("redirect", menu.getLink());
                    }
                    Meta meta = new Meta();
                    meta.setIcon(menu.getIcon());
                    meta.setTitle(menu.getName());
                    // metaType 使用字符串
                    meta.setType(resourceType.name().toLowerCase());
                    // 如果设置了不可见，那么设置hidden
                    if (ObjectUtil.equal(menu.getVisible(), 0)) {
                        meta.setHidden(true);
                    }
                    // 扩展字段
                    ResourceExt.MetaExt ext = gson.fromJson(menu.getExtJson(), ResourceExt.MetaExt.class);
                    if (ObjectUtil.isNotEmpty(ext)) {
                        meta.setBrief(ObjectUtil.equal(ext.getBrief(), 1));
                        meta.setAffix(ObjectUtil.equal(ext.getAffix(), 1));
                        meta.setKeepAlive(ObjectUtil.equal(ext.getKeepAlive(), 1));
                    }
                    // 如果是内链或者外链，设置url
                    if (ResourceTypeEnum.IFRAME.equals(resourceType) || ResourceTypeEnum.LINK.equals(resourceType)) {
                        meta.setUrl(menu.getPath());
                    }
                    extra.put("meta", meta);
                    node.setExtra(extra);
                    return node;
                }).collect(Collectors.toList());
        // 构建树
        return TreeUtil.buildSingle(treeNodeList, rootId, nodeConfig, new DefaultNodeParser<>());
    }

}
