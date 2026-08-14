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
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.enums.SortOrderEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.enums.RelationTypeEnum;
import com.moyu.boot.system.enums.MenuTypeEnum;
import com.moyu.boot.system.mapper.SysMenuMapper;
import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.entity.SysMenu;
import com.moyu.boot.system.model.entity.ext.MenuExt;
import com.moyu.boot.system.model.param.SysMenuParam;
import com.moyu.boot.system.model.vo.SysMenuVO;
import com.moyu.boot.system.service.SysRelationService;
import com.moyu.boot.system.service.SysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单资源服务实现类
 *
 * @author shisong
 * @since 2024-12-10 21:05:13
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private static final Gson gson = new GsonBuilder().create();

    @Resource
    private SysRelationService sysRelationService;

    @Override
    public List<Tree<String>> tree(SysMenuParam param) {
        // 查询所有资源(可指定module)
        List<SysMenu> resourceList = this.list(Wrappers.lambdaQuery(SysMenu.class)
                .eq(ObjectUtil.isNotEmpty(param.getModule()), SysMenu::getModule, param.getModule()));
        // 构建树中包含记录的所有字段
        String rootId = ObjectUtil.isEmpty(param.getModule()) ? SysConstants.ROOT_NODE_ID : param.getModule();
        return buildTree(resourceList, rootId);
    }

    @Override
    public List<SysMenuVO> list(SysMenuParam param) {
        // 查询条件
        QueryWrapper<SysMenu> queryWrapper = Wrappers.query(SysMenu.class).checkSqlInjection();
        // 指定模块
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getModule()), SysMenu::getModule, param.getModule());
        // 指定菜单类型 menuType
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getMenuType()), SysMenu::getMenuType, param.getMenuType());
        // 指定code查询
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getCode()), SysMenu::getCode, param.getCode());
        // 指定name查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getName()), SysMenu::getName, param.getName());
        // 指定path查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getPath()), SysMenu::getPath, param.getPath());
        // 指定component查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getComponent()), SysMenu::getComponent, param.getComponent());
        // 指定permission查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getPermission()), SysMenu::getPermission, param.getPermission());
        // 仅查询未删除的
        queryWrapper.lambda().eq(SysMenu::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(SortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(SysMenu::getSortNum);
        }
        // 查询
        List<SysMenu> resourceList = this.list(queryWrapper);
        // 转换为voList
        List<SysMenuVO> voList = buildSysResourceVOList(resourceList);
        return voList;
    }

    @Override
    public PageData<SysMenuVO> pageList(SysMenuParam param) {
        // 查询条件
        QueryWrapper<SysMenu> queryWrapper = Wrappers.query(SysMenu.class).checkSqlInjection();
        // 指定模块
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getModule()), SysMenu::getModule, param.getModule());
        // 指定菜单类型 menuType
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getMenuType()), SysMenu::getMenuType, param.getMenuType());
        // 指定code查询
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getCode()), SysMenu::getCode, param.getCode());
        // 指定name查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getName()), SysMenu::getName, param.getName());
        // 指定path查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getPath()), SysMenu::getPath, param.getPath());
        // 指定component查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getComponent()), SysMenu::getComponent, param.getComponent());
        // 指定permission查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getPermission()), SysMenu::getPermission, param.getPermission());
        // 仅查询未删除的
        queryWrapper.lambda().eq(SysMenu::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(SortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(SysMenu::getSortNum);
        }
        // 分页查询
        Page<SysMenu> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<SysMenu> resourcePage = this.page(page, queryWrapper);
        List<SysMenuVO> voList = buildSysResourceVOList(resourcePage.getRecords());
        return new PageData<>(resourcePage.getTotal(), voList);
    }

    @Override
    public SysMenuVO detail(SysMenuParam param) {
        // 查询条件 id、code均为唯一标识
        LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.lambdaQuery(SysMenu.class);
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getId()), SysMenu::getId, param.getId());
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), SysMenu::getCode, param.getCode());
        SysMenu entity = this.getOne(queryWrapper);
        if (entity == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        SysMenuVO vo = BeanUtil.copyProperties(entity, SysMenuVO.class);
        fillFromExtJson(vo, entity.getExtJson());
        return vo;
    }

    @Override
    public void add(SysMenuParam param) {
        // 若指定了唯一编码code，则必须全局唯一
        if (!Strings.isNullOrEmpty(param.getCode())) {
            // 查询指定code
            SysMenu menu = this.getOne(Wrappers.lambdaQuery(SysMenu.class)
                    .eq(SysMenu::getCode, param.getCode())
                    .eq(SysMenu::getDeleted, 0));
            if (menu != null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "唯一编码重复，请更换或留空自动生成");
            }
        }
        // 非module必须有parent存在(module为root节点)
        if (!Objects.equals(MenuTypeEnum.MODULE.getCode(), param.getMenuType())) {
            Assert.notEmpty(param.getParentCode(), "上级菜单parentCode不能为空");
            // 查询所选父节点
            SysMenu parentMenu = this.getOne(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getCode, param.getParentCode())
                    .eq(SysMenu::getDeleted, 0));
            if (parentMenu == null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "指定的父节点不存在");
            }
            // 若上级菜单指定了module, 则子节点也必须一致
            if (parentMenu.getModule() != null && !parentMenu.getModule().equals(param.getModule())) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "与上级菜单module不一致");
            }
        }
        // 转换
        SysMenu menu = buildSysMenu(param);
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
    public void deleteByIds(SysMenuParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        Set<String> codeSet = this.listByIds(idSet).stream().map(SysMenu::getCode).collect(Collectors.toSet());
        // 要删除的和查询到的进行比对
        if (ObjectUtil.notEqual(idSet.size(), codeSet.size())) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 物理删除
        this.removeByIds(idSet);
        // 资源删除时,对应的role_has_menu也要删除
        clearRoleMenu(codeSet);
    }

    @Override
    public void deleteTree(SysMenuParam param) {
        // 待删除节点的code集合(要删除的节点下还有其他子节点则无法删除)
        Set<String> codeSet = param.getCodes();
        // 查询codeSet+子节点
        LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.lambdaQuery(SysMenu.class);
        // 查询部分字段
        queryWrapper.select(SysMenu::getId, SysMenu::getCode, SysMenu::getParentCode);
        // 指定模块(有模块的情况下要过滤)
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getModule()), SysMenu::getModule, param.getModule());
        // 指定codeSet+其子节点
        queryWrapper.and(e -> e.in(SysMenu::getCode, codeSet).or().in(SysMenu::getParentCode, codeSet));
        queryWrapper.eq(SysMenu::getDeleted, 0);
        // 所有的菜单
        List<SysMenu> allList = this.list(queryWrapper);
        // 子节点
        Set<String> subCodeSet = allList.stream().map(SysMenu::getCode).collect(Collectors.toSet());
        // 移出本次要删除的code，剩下的为本次没删除的子节点
        subCodeSet.removeAll(codeSet);
        if (ObjectUtil.isNotEmpty(subCodeSet)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "要删除节点下还有其他子节点，无法直接删除");
        }
        // 待删除的id集合(先把指定节点加入集合)
        Set<Long> idSet = allList.stream().map(SysMenu::getId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(idSet)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败,未查到指定数据");
        }
        // 物理删除
        removeByIds(idSet);
        // 资源删除时,对应的role_has_resource也要删除
        clearRoleMenu(codeSet);
    }

    @Override
    public void update(SysMenuParam param) {
        // 通过主键id查询原有数据
        SysMenu old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 转换
        SysMenu toUpdate = BeanUtil.copyProperties(param, SysMenu.class, BaseEntity.UPDATE_TIME, BaseEntity.UPDATE_BY);
        // 其他处理
        toUpdate.setId(param.getId());
        // extJson
        toUpdate.setExtJson(buildExtJson(param));
        this.updateById(toUpdate);
    }

    @Override
    public List<Tree<String>> menuTreeSelector(SysMenuParam param) {
        // 查询所有菜单
        List<SysMenu> menuList = this.list(new LambdaQueryWrapper<SysMenu>()
                // 查询部分字段
                .select(SysMenu::getCode, SysMenu::getParentCode, SysMenu::getName, SysMenu::getSortNum, SysMenu::getId)
                // 指定模块
                .eq(ObjectUtil.isNotEmpty(param.getModule()), SysMenu::getModule, param.getModule())
                // 不能是按钮
                .ne(SysMenu::getMenuType, MenuTypeEnum.BUTTON.getCode())
                .eq(SysMenu::getDeleted, 0)
                .orderByAsc(SysMenu::getSortNum)
        );
        // 构建的树中仅包含部分字段
        String rootId = ObjectUtil.isEmpty(param.getModule()) ? SysConstants.ROOT_NODE_ID : param.getModule();
        return buildTree(menuList, rootId);
    }

    /**
     * SysresourceParam -> SysMenu
     */
    private SysMenu buildSysMenu(SysMenuParam param) {
        if (param == null) {
            return null;
        }
        SysMenu sysMenu = new SysMenu();
        sysMenu.setId(param.getId());
        sysMenu.setParentCode(param.getParentCode());
        sysMenu.setName(param.getName());
        sysMenu.setCode(param.getCode());
        sysMenu.setMenuType(param.getMenuType());
        sysMenu.setPath(param.getPath());
        sysMenu.setComponent(param.getComponent());
        sysMenu.setPermission(param.getPermission());
        sysMenu.setIcon(param.getIcon());
        sysMenu.setVisible(param.getVisible());
        sysMenu.setModule(param.getModule());
        sysMenu.setSortNum(param.getSortNum());
        sysMenu.setRemark(param.getRemark());
        sysMenu.setExtJson(buildExtJson(param));
        return sysMenu;
    }

    /**
     * 构造Resource的extJson
     */
    private String buildExtJson(SysMenuParam param) {
        if (param == null) {
            return null;
        }
        MenuExt.MetaExt extObj = new MenuExt.MetaExt();
        extObj.setBrief(param.getBrief());
        extObj.setAffix(param.getAffix());
        extObj.setKeepAlive(param.getKeepAlive());
        // 扩展信息
        return new Gson().toJson(extObj);
    }

    /**
     * 根据menu的类型为某些字段填充默认值
     */
    private void fillSysMenu(SysMenu menu) {
        Assert.notNull(menu, "菜单menu不能为空");
        MenuTypeEnum menuType = MenuTypeEnum.getByCode(menu.getMenuType());
        // 菜单类型（字典 1模块 2目录 3菜单 4内链 5外链 6按钮）
        if (!Objects.equals(MenuTypeEnum.MODULE, menuType)) {
            // 非模块必须指定parentCode及module
            Assert.notEmpty(menu.getParentCode(), "上级菜单parentCode不能为空");
            Assert.notEmpty(menu.getModule(), "归属模块module不能为空");
        }
        if (Objects.equals(MenuTypeEnum.MODULE, menuType)) {
            Assert.notEmpty(menu.getCode(), "模块编码code不能为空");
            // 模块要设置布局
            if (StrUtil.isEmpty(menu.getComponent())) {
                menu.setComponent("Layout");
            }
        } else if (Objects.equals(MenuTypeEnum.DIR, menuType)) {
            // 目录的组件、权限为空
            Assert.notEmpty(menu.getPath(), "路由地址path不能为空");
        } else if (Objects.equals(MenuTypeEnum.MENU, menuType)) {
            Assert.notEmpty(menu.getPath(), "路由地址path不能为空");
            Assert.notEmpty(menu.getComponent(), "组件component不能为空");
        } else if (Objects.equals(MenuTypeEnum.BUTTON, menuType)) {
            // 按钮的组件为空
            Assert.notEmpty(menu.getPermission(), "权限标识permission不能为空");
        } else if (Objects.equals(MenuTypeEnum.IFRAME, menuType) || Objects.equals(MenuTypeEnum.LINK, menuType)) {
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
    private List<Tree<String>> buildTree(List<SysMenu> menuList, String rootId) {
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

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<SysMenuVO> buildSysResourceVOList(List<SysMenu> entityList) {
        List<SysMenuVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (SysMenu entity : entityList) {
            SysMenuVO vo = BeanUtil.copyProperties(entity, SysMenuVO.class);
            MenuExt.MetaExt ext = gson.fromJson(entity.getExtJson(), MenuExt.MetaExt.class);
            if (ObjectUtil.isNotEmpty(ext)) {
                vo.setBrief(ext.getBrief());
                vo.setAffix(ext.getAffix());
                vo.setKeepAlive(ext.getKeepAlive());
            }
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 根据extJson填充vo对象
     */
    private void fillFromExtJson(SysMenuVO vo, String extJson) {
        MenuExt.MetaExt ext = gson.fromJson(extJson, MenuExt.MetaExt.class);
        if (ObjectUtil.isNotEmpty(ext)) {
            vo.setBrief(ext.getBrief());
            vo.setAffix(ext.getAffix());
            vo.setKeepAlive(ext.getKeepAlive());
        }
    }
}




