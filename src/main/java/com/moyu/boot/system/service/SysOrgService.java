package com.moyu.boot.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysOrg;
import com.moyu.boot.system.model.param.SysOrgParam;

import java.util.List;

/**
 * @author shisong
 * @description 针对表【sys_org(组织机构表)】的数据库操作Service
 * @createDate 2024-11-26 09:55:33
 */
public interface SysOrgService extends IService<SysOrg> {

    /**
     * 查询指定orgCode的下属组织机构code列表(包含本身)
     */
    List<String> childrenCodeList(String orgCode);

    /**
     * 查询组织列表
     */
    List<SysOrg> list(SysOrgParam sysOrgParam);

    /**
     * 分页获取组织列表
     */
    PageData<SysOrg> pageList(SysOrgParam sysOrgParam);

    /**
     * 组织机构树（会有多颗树）
     *
     * @return 组织机构树List集合
     */
    List<Tree<String>> tree();

    /**
     * 根结点的组织机构树（只有一颗树）最好有缓存
     *
     * @return 组织机构树
     */
    Tree<String> singleTree();

    /**
     * 获取组织机构详情
     */
    SysOrg detail(SysOrgParam orgParam);

    /**
     * 添加组织机构
     */
    void add(SysOrgParam orgParam);

    /**
     * 通过ids删除，且不会集联删除
     */
    void deleteByIds(SysOrgParam orgParam);

    /**
     * 通过codes删除，会集联删除树的所有节点
     */
    void deleteTree(SysOrgParam orgParam);

    /**
     * 修改组织机构
     */
    void update(SysOrgParam orgParam);

}
