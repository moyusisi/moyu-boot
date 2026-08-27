package com.moyu.boot.system.service.impl;

import cn.dev33.satoken.SaManager;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.DefaultNodeParser;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moyu.boot.common.authZ.util.LoginUserUtils;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.mybatis.util.DataScopeHelper;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.enums.OrgTypeEnum;
import com.moyu.boot.system.mapper.SysOrgMapper;
import com.moyu.boot.system.model.entity.SysOrg;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.param.SysOrgParam;
import com.moyu.boot.system.model.vo.SysOrgVO;
import com.moyu.boot.system.service.SysOrgService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 组织机构服务类Service实现类
 *
 * @author shisong
 * @since 2024-11-26 09:55:33
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {

    @Override
    public List<SysOrgVO> list(SysOrgParam param) {
        String parentCode = param.getParentCode();
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定parentCode查询(包括本身和直接子节点)
        queryWrapper.and(qw -> qw.where(SysOrg::getCode).eq(parentCode).or(SysOrg::getParentCode).eq(parentCode), ObjectUtil.isNotEmpty(parentCode));
        // 指定name查询
        queryWrapper.like(SysOrg::getName, param.getName(), ObjectUtil.isNotEmpty(param.getName()));
        // 指定code查询
        queryWrapper.eq(SysOrg::getCode, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        // 指定orgType查询
        queryWrapper.eq(SysOrg::getOrgType, param.getOrgType(), ObjectUtil.isNotEmpty(param.getOrgType()));
        // 指定orgLevel查询
        queryWrapper.eq(SysOrg::getOrgLevel, param.getOrgLevel(), ObjectUtil.isNotEmpty(param.getOrgLevel()));
        // 指定status查询
        queryWrapper.eq(SysOrg::getStatus, param.getStatus(), ObjectUtil.isNotEmpty(param.getStatus()));
        // 仅查询未删除的
        queryWrapper.eq(SysOrg::getDeleted, 0);
        // 指定排序
        queryWrapper.orderBy(SysRole::getSortNum, true);
        // 查询
        List<SysOrgVO> voList = this.listAs(queryWrapper, SysOrgVO.class);
        return voList;
    }

    /**
     * 获取组织分页
     */
    @Override
    public PageData<SysOrgVO> pageList(SysOrgParam param) {
        String parentCode = param.getParentCode();
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定parentCode查询(包括本身和直接子节点)
        queryWrapper.and(qw -> qw.where(SysOrg::getCode).eq(parentCode).or(SysOrg::getParentCode).eq(parentCode), ObjectUtil.isNotEmpty(parentCode));
        // 指定name查询
        queryWrapper.like(SysOrg::getName, param.getName(), ObjectUtil.isNotEmpty(param.getName()));
        // 指定code查询
        queryWrapper.eq(SysOrg::getCode, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        // 指定orgType查询
        queryWrapper.eq(SysOrg::getOrgType, param.getOrgType(), ObjectUtil.isNotEmpty(param.getOrgType()));
        // 指定orgLevel查询
        queryWrapper.eq(SysOrg::getOrgLevel, param.getOrgLevel(), ObjectUtil.isNotEmpty(param.getOrgLevel()));
        // 指定status查询
        queryWrapper.eq(SysOrg::getStatus, param.getStatus(), ObjectUtil.isNotEmpty(param.getStatus()));
        // 仅查询未删除的
        queryWrapper.eq(SysOrg::getDeleted, 0);
        // 指定排序
        queryWrapper.orderBy(SysRole::getSortNum, true);
        // 非ROOT则限制数据权限
        DataScopeHelper.dataScopeFilter(queryWrapper, SysOrg::getName, SysOrg::getCode);
        DataScopeHelper.dataScopeFilter(com.mybatisflex.core.query.QueryWrapper.create(), SysOrg::getName, SysOrg::getCode);
        // 分页查询
        Page<SysOrgVO> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<SysOrgVO> voPage = this.pageAs(page, queryWrapper, SysOrgVO.class);
        return new PageData<>(voPage.getTotalRow(), voPage.getRecords());
    }

    @Override
    public SysOrgVO detail(SysOrgParam param) {
        // 查询条件 id、code均为唯一标识
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(SysOrg::getId, param.getId(), ObjectUtil.isNotEmpty(param.getId()));
        queryWrapper.eq(SysOrg::getCode, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        SysOrgVO vo = this.getOneAs(queryWrapper, SysOrgVO.class);
        if (vo == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        return vo;
    }

    @Override
    public void add(SysOrgParam param) {
        // 若指定了唯一编码code，则必须全局唯一
        if (!Strings.isNullOrEmpty(param.getCode())) {
            // 查询指定code
            boolean exist = this.exists(QueryWrapper.create().eq(SysOrg::getCode, param.getCode()));
            if (exist) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "唯一编码重复，请更换或留空自动生成");
            }
        }
        // 组装SysOrg
        SysOrg org = buildSysOrg(param);
        org.setId(null);
        // 若未指定唯一编码code，则自动生成
        if (Strings.isNullOrEmpty(org.getCode())) {
            // 唯一code RandomUtil.randomString(10)、IdUtil.objectId()24位
            org.setCode(IdUtil.objectId());
        }
        // 组织机构层级路径,逗号分隔,父节点在后(不包含本节点)
        Tree<String> rootTree = singleTree();
        List<String> list = TreeUtil.getParentsId(rootTree.getNode(param.getParentCode()), true);
        org.setOrgPath(SysConstants.COMMA_JOINER.join(list));
        this.save(org);
    }

    @Override
    public void deleteByIds(SysOrgParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 删除时先查再删
        Long count = this.count(QueryWrapper.create().in(SysOrg::getId, idSet));
        // 查到的数量比对
        if (ObjectUtil.notEqual(idSet.size(), count)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 逻辑删除
        UpdateChain.of(SysOrg.class)
                .set(SysOrg::getDeleted, 1)
                .where(SysOrg::getId).in(idSet)
                .update();
    }

    @Override
    public List<String> childrenCodeList(String orgCode) {
        List<String> codeList = new ArrayList<>();
        List<SysOrg> orgList = this.getMapper().selectChildren(orgCode);
        orgList.forEach(e -> codeList.add(e.getCode()));
        return codeList;
    }

    @Override
    public String orgCompany(String orgCode, Tree<String> rootTree) {
        rootTree = ObjectUtil.defaultIfNull(rootTree, singleTree());
        // 获取组织结构树
        Tree<String> node = rootTree.getNode(orgCode);
        String companyCode = orgCode;
        while (null != node) {
            if (OrgTypeEnum.COMPANY.getCode().equals(node.get("orgType"))) {
                companyCode = node.getId();
                break;
            }
            node = node.getParent();
        }
        return companyCode;
    }

    /**
     * 组织机构(树太大需要加缓存)
     */
    @Override
    public List<Tree<String>> tree() {
        Tree<String> rootTree = singleTree();
        if (LoginUserUtils.isRoot()) {
            return rootTree.getChildren();
        }
        // 默认返回所属的公司树
        String orgCode = getUserCompanyCode(rootTree, LoginUserUtils.getOrgCode());
        // 用户直属公司orgTree
        Tree<String> orgTree = rootTree.getNode(orgCode);
        // 用户公司树列表
        return Lists.newArrayList(orgTree);
    }

    @Override
    public Tree<String> singleTree() {
        // tree太大需要有缓存
        return loadRootTree();
    }

    @Override
    public void deleteTree(SysOrgParam orgParam) {
        // 查询所有记录(部分字段)
        List<SysOrg> orgList = this.list(QueryWrapper.create().select(SysOrg::getId, SysOrg::getCode, SysOrg::getParentCode)
                .eq(SysOrg::getDeleted, 0));
        // 待删除节点的code集合
        Set<String> codeSet = orgParam.getCodes();

        // 待删除的id集合(先把指定节点加入集合)
        Set<Long> idSet = orgList.stream()
                .filter(org -> codeSet.contains(org.getCode()))
                .map(SysOrg::getId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(idSet)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败,未查到指定数据");
        }

        // 集联删除，子节点也要删除。循环查找子节点,并加入到待删除集合
        while (!CollectionUtils.isEmpty(codeSet)) {
            Set<String> childrenSet = new HashSet<>();
            orgList.forEach(org -> {
                if (codeSet.contains(org.getParentCode())) {
                    childrenSet.add(org.getCode());
                    idSet.add(org.getId());
                }
            });
            // 子节点将变为新的父节点
            codeSet.clear();
            codeSet.addAll(childrenSet);
        }
        // 逻辑删除
        UpdateChain.of(SysOrg.class)
                .set(SysOrg::getDeleted, 1)
                .where(SysOrg::getId).in(idSet)
                .update();
    }

    @Override
    public void update(SysOrgParam param) {
        // 通过主键id查询原有数据
        SysOrg old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        SysOrg toUpdate = BeanUtil.copyProperties(param, SysOrg.class);
        toUpdate.setId(old.getId());
        // 若父节点有变化，则orgPath也要变
        if (ObjectUtil.isEmpty(old.getOrgPath()) || ObjectUtil.notEqual(old.getParentCode(), param.getParentCode())) {
            // 组织机构层级路径,逗号分隔,父节点在后(不包含本节点)
            Tree<String> rootTree = singleTree();
            List<String> list = TreeUtil.getParentsId(rootTree.getNode(param.getParentCode()), true);
            toUpdate.setOrgPath(SysConstants.COMMA_JOINER.join(list));
            // 本节点的子节点orgPath也应该改变，待tree更新之后才可以修改 TODO
        }
        this.updateById(toUpdate);
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

    /**
     * SysOrgParam -> SysOrg
     */
    SysOrg buildSysOrg(SysOrgParam orgParam) {
        if (orgParam == null) {
            return null;
        }
        SysOrg sysOrg = new SysOrg();
        sysOrg.setId(orgParam.getId());
        sysOrg.setParentCode(orgParam.getParentCode());
        sysOrg.setName(orgParam.getName());
        sysOrg.setCode(orgParam.getCode());
        sysOrg.setOrgType(orgParam.getOrgType());
        sysOrg.setOrgLevel(orgParam.getOrgLevel());
        sysOrg.setSortNum(orgParam.getSortNum());
        sysOrg.setStatus(orgParam.getStatus());
        sysOrg.setExtJson(orgParam.getExtJson());
        sysOrg.setRemark(orgParam.getRemark());
        return sysOrg;
    }

    /**
     * 构建树结构(code, parentCode, children, weight, extra)
     */
    private Tree<String> buildSingleTree(List<SysOrg> orgList, String rootId) {
        // 配置TreeNode使用指定的字段名
        TreeNodeConfig nodeConfig = new TreeNodeConfig();
        nodeConfig.setIdKey("code");
        nodeConfig.setParentIdKey("parentCode");
        // 结构转换
        List<TreeNode<String>> treeNodeList = orgList.stream()
                .map(org -> {
                    TreeNode<String> node = new TreeNode<>(org.getCode(), org.getParentCode(), org.getName(), org.getSortNum());
                    node.setExtra(BeanUtil.beanToMap(org, false, true));
                    return node;
                }).collect(Collectors.toList());
        // 构建树
        return TreeUtil.buildSingle(treeNodeList, rootId, nodeConfig, new DefaultNodeParser<>());
    }

    /**
     * 加载组织机构树
     */
    private Tree<String> loadRootTree() {
        // 查询所有组织结构
        Gson gson = new Gson();
        // 1. 先从缓存中load
        String jsonString = SaManager.getSaTokenDao().get("org:tree:orgList");
        List<SysOrg> orgList = gson.fromJson(jsonString, new TypeToken<List<SysOrg>>() {
        }.getType());
        // 2. 缓存无则查db并写入缓存
        if (CollectionUtils.isEmpty(orgList)) {
            // 查db，只需查部分字段
            orgList = this.list(QueryWrapper.create().select(SysOrg::getCode, SysOrg::getParentCode, SysOrg::getName, SysOrg::getSortNum, SysOrg::getOrgType)
                    .eq(SysOrg::getDeleted, 0)
            );
            jsonString = gson.toJson(orgList);
            // 写入缓存，并设定存活时间 (单位: 秒)
            SaManager.getSaTokenDao().set("org:tree:orgList", jsonString, 60 * 10L);
        }
        // 3. 构建树
        return buildSingleTree(orgList, SysConstants.ROOT_NODE_ID);
    }
}




