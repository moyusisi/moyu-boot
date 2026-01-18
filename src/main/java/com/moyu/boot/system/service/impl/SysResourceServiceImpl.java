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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.enums.RelationTypeEnum;
import com.moyu.boot.system.enums.ResourceTypeEnum;
import com.moyu.boot.system.mapper.SysResourceMapper;
import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.entity.SysResource;
import com.moyu.boot.system.model.param.SysResourceParam;
import com.moyu.boot.system.service.SysRelationService;
import com.moyu.boot.system.service.SysResourceService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 资源权限服务实现类
 *
 * @author shisong
 * @since 2024-12-10 21:05:13
 */
@Service
public class SysResourceServiceImpl extends ServiceImpl<SysResourceMapper, SysResource> implements SysResourceService {

    @Resource
    private SysRelationService sysRelationService;

    @Override
    public List<Tree<String>> tree(SysResourceParam param) {
        // 查询条件(可指定module、status)
        SysResourceParam query = SysResourceParam.builder().module(param.getModule()).build();
        // 查询所有资源
        List<SysResource> resourceList = this.list(query);
        // 构建树中包含记录的所有字段
        String rootId = ObjectUtil.isEmpty(param.getModule()) ? SysConstants.ROOT_NODE_ID : param.getModule();
        return buildTree(resourceList, rootId);
    }

    @Override
    public List<SysResource> list(SysResourceParam param) {
        // 查询条件
        LambdaQueryWrapper<SysResource> queryWrapper = Wrappers.lambdaQuery(SysResource.class);
        // 指定模块
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getModule()), SysResource::getModule, param.getModule());
        // 指定资源类型 resourceType
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getResourceType()), SysResource::getResourceType, param.getResourceType());
        // 指定name查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getName()), SysResource::getName, param.getName());
        // 指定code查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), SysResource::getCode, param.getCode());
        // 指定path查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getPath()), SysResource::getPath, param.getPath());
        // 指定visible查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getVisible()), SysResource::getVisible, param.getVisible());
        // 仅查询未删除的
        queryWrapper.eq(SysResource::getDeleted, 0);
        // 指定排序
        queryWrapper.orderByAsc(SysResource::getSortNum);
        // 查询
        List<SysResource> resourceList = this.list(queryWrapper);
        return resourceList;
    }

    @Override
    public PageData<SysResource> pageList(SysResourceParam param) {
        // 查询条件
        LambdaQueryWrapper<SysResource> queryWrapper = Wrappers.lambdaQuery(SysResource.class);
        // 指定模块
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getModule()), SysResource::getModule, param.getModule());
        // 指定资源类型 resourceType
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getResourceType()), SysResource::getResourceType, param.getResourceType());
        // 指定name查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getName()), SysResource::getName, param.getName());
        // 指定code查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), SysResource::getCode, param.getCode());
        // 指定path查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getPath()), SysResource::getPath, param.getPath());
        // 仅查询未删除的
        queryWrapper.eq(SysResource::getDeleted, 0);
        // 指定排序
        queryWrapper.orderByAsc(SysResource::getSortNum);
        // 分页查询
        Page<SysResource> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<SysResource> menuPage = this.page(page, queryWrapper);
        return new PageData<>(menuPage.getTotal(), menuPage.getRecords());
    }

    @Override
    public SysResource detail(SysResourceParam param) {
        // 查询条件 id、code均为唯一标识
        LambdaQueryWrapper<SysResource> queryWrapper = Wrappers.lambdaQuery(SysResource.class);
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getId()), SysResource::getId, param.getId());
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), SysResource::getCode, param.getCode());
        SysResource sysResource = this.getOne(queryWrapper);
        if (sysResource == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        return sysResource;
    }

    @Override
    public void add(SysResourceParam param) {
        // 若指定了唯一编码code，则必须全局唯一
        if (!Strings.isNullOrEmpty(param.getCode())) {
            // 查询指定code
            SysResource menu = this.getOne(Wrappers.lambdaQuery(SysResource.class)
                    .eq(SysResource::getCode, param.getCode())
                    .eq(SysResource::getDeleted, 0));
            if (menu != null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "唯一编码重复，请更换或留空自动生成");
            }
        }
        // 非root节点的parent必须存在(module为root节点)
        if (!Objects.equals(ResourceTypeEnum.MODULE.getCode(), param.getResourceType())) {
            Assert.notEmpty(param.getParentCode(), "上级菜单parentCode不能为空");
            // 查询所选父节点
            SysResource parentMenu = this.getOne(new LambdaQueryWrapper<SysResource>()
                    .eq(SysResource::getCode, param.getParentCode())
                    .eq(SysResource::getDeleted, 0));
            if (parentMenu == null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "指定的父节点不存在");
            }
            // 若上级菜单指定了module, 则子节点也必须一致
            if (parentMenu.getModule() != null && !parentMenu.getModule().equals(param.getModule())) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "与上级菜单module不一致");
            }
        }
        // 转换
        SysResource menu = buildSysMenu(param);
        // 填充一些默认值
        fillSysMenu(menu);
        menu.setId(null);
        // 若未指定唯一编码code，则自动生成
        if (Strings.isNullOrEmpty(param.getCode())) {
            // 唯一code RandomUtil.randomString(10)、IdUtil.objectId()24位
            menu.setCode(IdUtil.objectId());
        }
        this.save(menu);
    }

    @Override
    public void deleteByIds(SysResourceParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        Set<String> codeSet = listByIds(idSet).stream().map(SysResource::getCode).collect(Collectors.toSet());
        // 物理删除
        this.removeByIds(idSet);
        // 资源删除时,对应的role_has_menu也要删除
        clearRoleMenu(codeSet);
    }

    @Override
    public void deleteTree(SysResourceParam param) {
        // 待删除节点的code集合(要删除的节点下还有其他子节点则无法删除)
        Set<String> codeSet = param.getCodes();
        // 查询codeSet+子节点
        LambdaQueryWrapper<SysResource> queryWrapper = Wrappers.lambdaQuery(SysResource.class);
        // 查询部分字段
        queryWrapper.select(SysResource::getId, SysResource::getCode, SysResource::getParentCode);
        // 指定模块(有模块的情况下要过滤)
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getModule()), SysResource::getModule, param.getModule());
        // 指定codeSet+其子节点
        queryWrapper.and(e -> e.in(SysResource::getCode, codeSet).or().in(SysResource::getParentCode, codeSet));
        queryWrapper.eq(SysResource::getDeleted, 0);
        // 所有的菜单
        List<SysResource> allList = this.list(queryWrapper);
        // 子节点
        Set<String> subCodeSet = allList.stream().map(SysResource::getCode).collect(Collectors.toSet());
        // 移出本次要删除的code，剩下的为本次没删除的子节点
        subCodeSet.removeAll(codeSet);
        if (ObjectUtil.isNotEmpty(subCodeSet)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "要删除节点下还有其他子节点，无法直接删除");
        }
        // 待删除的id集合(先把指定节点加入集合)
        Set<Long> idSet = allList.stream().map(SysResource::getId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(idSet)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败,未查到指定数据");
        }
        // 物理删除
        removeByIds(idSet);
        // 资源删除时,对应的role_has_resource也要删除
        clearRoleMenu(codeSet);
    }

    @Override
    public void update(SysResourceParam param) {
        // 通过主键id查询原有数据
        SysResource old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 转换
        SysResource toUpdate = BeanUtil.copyProperties(param, SysResource.class, BaseEntity.UPDATE_TIME, BaseEntity.UPDATE_BY);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public List<Tree<String>> menuTreeSelector(SysResourceParam param) {
        // 查询所有菜单
        List<SysResource> menuList = this.list(new LambdaQueryWrapper<SysResource>()
                // 查询部分字段
                .select(SysResource::getCode, SysResource::getParentCode, SysResource::getName, SysResource::getSortNum, SysResource::getId)
                // 指定模块
                .eq(ObjectUtil.isNotEmpty(param.getModule()), SysResource::getModule, param.getModule())
                // 不能是按钮
                .ne(SysResource::getResourceType, ResourceTypeEnum.BUTTON.getCode())
                .eq(SysResource::getDeleted, 0)
                .orderByAsc(SysResource::getSortNum)
        );
        // 构建的树中仅包含部分字段
        String rootId = ObjectUtil.isEmpty(param.getModule()) ? SysConstants.ROOT_NODE_ID : param.getModule();
        return buildTree(menuList, rootId);
    }

    /**
     * SysresourceParam -> SysMenu
     */
    private SysResource buildSysMenu(SysResourceParam param) {
        if (param == null) {
            return null;
        }
        SysResource sysResource = new SysResource();
        sysResource.setId(param.getId());
        sysResource.setParentCode(param.getParentCode());
        sysResource.setName(param.getName());
        sysResource.setCode(param.getCode());
        sysResource.setResourceType(param.getResourceType());
        sysResource.setPath(param.getPath());
        sysResource.setComponent(param.getComponent());
        sysResource.setPermission(param.getPermission());
        sysResource.setLink(param.getLink());
        sysResource.setIcon(param.getIcon());
        sysResource.setVisible(param.getVisible());
        sysResource.setModule(param.getModule());
        sysResource.setSortNum(param.getSortNum());
        sysResource.setExtJson(param.getExtJson());
        sysResource.setRemark(param.getRemark());
        return sysResource;
    }

    /**
     * 根据menu的类型为某些字段填充默认值
     */
    private void fillSysMenu(SysResource menu) {
        Assert.notNull(menu, "菜单menu不能为空");
        ResourceTypeEnum resourceType = ResourceTypeEnum.getByCode(menu.getResourceType());
        // 资源类型（字典 1模块 2目录 3菜单 4内链 5外链 6按钮）
        if (!Objects.equals(ResourceTypeEnum.MODULE, resourceType)) {
            // 非模块必须指定parentCode及module
            Assert.notEmpty(menu.getParentCode(), "上级菜单parentCode不能为空");
            Assert.notEmpty(menu.getModule(), "归属模块module不能为空");
        }
        if (Objects.equals(ResourceTypeEnum.MODULE, resourceType)) {
            // 模块要设置布局
            if (StrUtil.isEmpty(menu.getComponent())) {
                menu.setComponent("Layout");
            }
        } else if (Objects.equals(ResourceTypeEnum.DIR, resourceType)) {
            // 目录的组件、权限为空
            Assert.notEmpty(menu.getPath(), "路由地址path不能为空");
        } else if (Objects.equals(ResourceTypeEnum.MENU, resourceType)) {
            Assert.notEmpty(menu.getPath(), "路由地址path不能为空");
            Assert.notEmpty(menu.getComponent(), "组件component不能为空");
        } else if (Objects.equals(ResourceTypeEnum.BUTTON, resourceType)) {
            // 按钮的组件为空
            Assert.notEmpty(menu.getPermission(), "权限标识permission不能为空");
        } else if (Objects.equals(ResourceTypeEnum.IFRAME, resourceType) || Objects.equals(ResourceTypeEnum.LINK, resourceType)) {
            Assert.notEmpty(menu.getPath(), "链接地址path不能为空");
            Assert.isTrue(menu.getPath().startsWith("http"), "链接必须以http(s)开头");
        }
    }

    /**
     * 构建树结构(code, parentCode, children, weight, extra)
     *
     * @param menuList menu的非空字段构会放到树节点中
     * @param rootId   指定的根节点(从树中查找此rootId)
     * @return 返回以rootId为根的树，可能是子树或多棵树
     */
    private List<Tree<String>> buildTree(List<SysResource> menuList, String rootId) {
        // 配置TreeNode使用指定的字段名
        TreeNodeConfig nodeConfig = new TreeNodeConfig();
        nodeConfig.setIdKey("code");
        nodeConfig.setParentIdKey("parentCode");
        // 结构转换
        List<TreeNode<String>> treeNodeList = menuList.stream().map(menu -> {
            TreeNode<String> node = new TreeNode<>(menu.getCode(), menu.getParentCode(), menu.getName(), menu.getSortNum());
            node.setExtra(BeanUtil.beanToMap(menu, false, true));
            return node;
        }).collect(Collectors.toList());
        // 构建树
        return TreeUtil.build(treeNodeList, rootId, nodeConfig, new DefaultNodeParser<>());
    }

    /**
     * 清除关系表中role_has_perm的指定的关系
     *
     * @param codeSet 指定的menu code集合
     */
    private void clearRoleMenu(Set<String> codeSet) {
        if (ObjectUtil.isEmpty(codeSet)) {
            return;
        }
        // 删除指定menuCode 的 ROLE_HAS_MENU
        sysRelationService.remove(Wrappers.lambdaQuery(SysRelation.class)
                .eq(SysRelation::getRelationType, RelationTypeEnum.ROLE_HAS_PERM)
                .in(SysRelation::getTargetId, codeSet));
    }
}




