package com.moyu.boot.system.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.DefaultNodeParser;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import com.moyu.boot.common.security.util.SecurityUtils;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.enums.OrgTypeEnum;
import com.moyu.boot.system.enums.ResourceTypeEnum;
import com.moyu.boot.system.enums.StatusEnum;
import com.moyu.boot.system.model.entity.SysGroup;
import com.moyu.boot.system.model.entity.SysResource;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysGroupParam;
import com.moyu.boot.system.model.param.SysRoleParam;
import com.moyu.boot.system.model.param.SysUserParam;
import com.moyu.boot.system.model.vo.GroupInfo;
import com.moyu.boot.system.model.vo.Meta;
import com.moyu.boot.system.model.vo.UserInfo;
import com.moyu.boot.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public UserInfo currentUserInfo(String username) {
        // 查询用户entity
        SysUser user = sysUserService.detail(SysUserParam.builder().account(username).build());
        // 当前登陆用户
        Optional<LoginUser> optUser = SecurityUtils.getLoginUser();
        if (!optUser.isPresent()) {
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, "用户未登录");
        }
        LoginUser loginUser = optUser.get();
        // 构造用户信息视图对象
        UserInfo userInfo = UserInfo.builder().account(username)
                .name(user.getName()).nickName(user.getNickName()).avatar(user.getAvatar())
                .perms(loginUser.getPerms()).roles(loginUser.getRoles())
                .orgCode(loginUser.getOrgCode()).groupCode(loginUser.getGroupCode())
                .dataScope(loginUser.getDataScope()).scopes(loginUser.getScopes())
                .build();
        // 岗位列表
        List<SysGroup> groupList = sysGroupService.userGroupList(username);
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
        // 用户有权限的资源code集合(含按钮)
        Set<String> permSet = sysRelationService.userMenu(username);

        // 查询所有可用的菜单(不含按钮)
        List<SysResource> allMenuList = sysResourceService.list(new LambdaQueryWrapper<SysResource>()
                // 不能已停用
                .ne(SysResource::getStatus, StatusEnum.DISABLE.getCode())
                // 不能是按钮
                .ne(SysResource::getResourceType, ResourceTypeEnum.BUTTON.getCode())
                .eq(SysResource::getDeleted, 0)
                .orderByAsc(SysResource::getSortNum)
        );
        // 用户有权限的菜单(不含按钮) + 所有模块、目录
        List<SysResource> userMenuList = CollectionUtil.newArrayList();
        allMenuList.forEach(sysMenu -> {
            if (ResourceTypeEnum.MODULE.getCode().equals(sysMenu.getResourceType())) {
                // path为空则设置为随机字符串
                if (ObjectUtil.isEmpty(sysMenu.getPath())) {
                    sysMenu.setPath(StrUtil.SLASH + RandomUtil.randomString(10));
                }
                userMenuList.add(sysMenu);
            } else if (ResourceTypeEnum.DIR.getCode().equals(sysMenu.getResourceType())) {
                userMenuList.add(sysMenu);
            } else {
                // 菜单，有权限才添加
                if (permSet.contains(sysMenu.getCode())) {
                    userMenuList.add(sysMenu);
                }
            }
        });
        // 构建菜单路由树结构
        Tree<String> singleTree = buildMenuTree(userMenuList, SysConstants.ROOT_NODE_ID);

        // 移除空目录(只要有符合条件的子节点就保留)
        singleTree.filter(tree -> {
            // id=0或parentId=0均不符合要求(排除根和模块)
            if (SysConstants.ROOT_NODE_ID.equals(tree.getId()) || SysConstants.ROOT_NODE_ID.equals(tree.getParentId())) {
                return false;
            }
            if (ObjectUtil.isNotEmpty(tree.get("meta"))) {
                Meta meta = (Meta) tree.get("meta");
                String metaType = meta.getType();
                // 不是目录
                boolean notDir = !ResourceTypeEnum.DIR.name().equalsIgnoreCase(metaType) && !ResourceTypeEnum.MODULE.name().equalsIgnoreCase(metaType);
                // 有权限的菜单叶子节点才符合要求
                return notDir && permSet.contains(tree.getId());
            } else {
                return false;
            }
        });
        return singleTree.getChildren();
    }

    @Override
    public List<Tree<String>> userOrgTree(String username) {
        if (SecurityUtils.isRoot()) {
            return sysOrgService.tree();
        }
        // 获取全部树
        Tree<String> rootTree = sysOrgService.singleTree();
        // 查询用户当前岗位所属公司
        String orgCode = getUserCompanyCode(rootTree, SecurityUtils.getOrgCode());
        // 用户直属公司orgTree
        Tree<String> orgTree = rootTree.getNode(orgCode);
        // 用户公司树列表
        return Lists.newArrayList(orgTree);
    }

    @Override
    public List<SysRole> userRoleList(String username, String searchKey) {
        // 查询用户所有的角色列表
        Set<String> codeSet = sysRoleService.userAllRoles(username);
        return sysRoleService.list(SysRoleParam.builder().codeSet(codeSet).searchKey(searchKey).build());
    }

    @Override
    public String switchUserGroup(String groupCode) {
        String username = SecurityUtils.getUsername();
        SysGroup group = sysGroupService.detail(SysGroupParam.builder().code(groupCode).build());
        // 角色集
        Set<String> roleSet = sysRoleService.userAllRoles(username);
        // 添加group中的角色
        sysGroupService.groupRoleList(SysGroupParam.builder().code(group.getCode()).build())
                .forEach(e -> roleSet.add(e.getCode()));
        // 权限集
        Set<String> permSet = sysRoleService.rolePerms(roleSet);
        // 组装LoginUser
        LoginUser loginUser = LoginUser.builder().enabled(true)
                .username(username).roles(roleSet).perms(permSet)
                .orgCode(group.getOrgCode()).groupCode(group.getCode())
                .dataScope(group.getDataScope()).build();
        // 自定义数据权限集合
        if (DataScopeEnum.ORG_DEFINE.getCode().equals(group.getDataScope())) {
            loginUser.setScopes(new HashSet<>(SysConstants.COMMA_SPLITTER.splitToList(group.getScopeSet())));
        }
        return TokenService.generateToken(loginUser);
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

    /**
     * 获取指定部门所属公司的orgCode
     */
    private String getUserCompanyCode(Tree<String> tree, String deptCode) {
        // 通过用户的orgPath获取用户的组织链接
        List<String> orgPathList = TreeUtil.getParentsId(tree.getNode(deptCode), true);
        // 从前往后遍历，因组织链有顺序，所以遍历顺序不能变
        String orgCode = orgPathList.stream()
                .filter(code -> ObjectUtil.equal(OrgTypeEnum.COMPANY.getCode(), tree.getNode(code).get("orgType")))
                .findFirst().orElse(deptCode);
        return orgCode;
    }


}
