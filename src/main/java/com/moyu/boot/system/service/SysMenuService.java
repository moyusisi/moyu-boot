package com.moyu.boot.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysMenu;
import com.moyu.boot.system.model.param.SysMenuParam;
import com.moyu.boot.system.model.vo.SysMenuVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 菜单资源服务类Service
 *
 * @author shisong
 * @since 2024-12-10 21:05:13
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 菜单树,包含按钮(借助hutool的树结构)
     *
     * @param param 查询条件(可指定module)
     * @return 菜单树List集合
     */
    List<Tree<String>> tree(SysMenuParam param);

    /**
     * 获取菜单列表
     */
    List<SysMenuVO> list(SysMenuParam param);

    /**
     * 分页获取菜单列表
     */
    PageData<SysMenuVO> pageList(SysMenuParam param);

    /**
     * 获取菜单详情
     */
    SysMenuVO detail(SysMenuParam param);

    /**
     * 添加菜单
     */
    void add(SysMenuParam param);

    /**
     * 通过ids删除，且不会集联删除
     */
    void deleteByIds(SysMenuParam param);

    /**
     * 通过codes删除，要删除的节点下还有其他子节点则无法删除
     */
    void deleteTree(SysMenuParam param);

    /**
     * 修改菜单
     */
    void update(SysMenuParam param);

    /**
     * 获取菜单树选择器(字段少，不包含按钮)
     *
     * @param param 可指定module
     */
    List<Tree<String>> menuTreeSelector(SysMenuParam param);

}
