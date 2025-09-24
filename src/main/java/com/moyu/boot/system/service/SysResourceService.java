package com.moyu.boot.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysResource;
import com.moyu.boot.system.model.param.SysResourceParam;

import java.util.List;

/**
 * @author shisong
 * @description 针对表【sys_resource(资源权限表)】的数据库操作Service
 * @createDate 2024-12-10 21:05:13
 */
public interface SysResourceService extends IService<SysResource> {

    /**
     * 菜单树,包含按钮(借助hutool的树结构)
     *
     * @param resourceParam 查询条件(可指定module、status)
     * @return 菜单树List集合
     */
    List<Tree<String>> tree(SysResourceParam resourceParam);

    /**
     * 获取菜单列表
     */
    List<SysResource> list(SysResourceParam resourceParam);

    /**
     * 分页获取菜单列表
     */
    PageData<SysResource> pageList(SysResourceParam resourceParam);

    /**
     * 获取菜单详情
     */
    SysResource detail(SysResourceParam resourceParam);

    /**
     * 添加菜单
     */
    void add(SysResourceParam resourceParam);

    /**
     * 通过ids删除，且不会集联删除
     */
    void deleteByIds(SysResourceParam resourceParam);

    /**
     * 通过codes删除，会集联删除树的所有节点
     */
    void deleteTree(SysResourceParam resourceParam);

    /**
     * 修改菜单
     */
    void update(SysResourceParam resourceParam);

    /**
     * 获取菜单树选择器(字段少，不包含按钮)
     *
     * @param resourceParam 可指定module
     */
    List<Tree<String>> menuTreeSelector(SysResourceParam resourceParam);

}
