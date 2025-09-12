package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.security.util.SecurityUtils;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.mapper.SysOrgMapper;
import com.moyu.boot.system.model.entity.SysOrg;
import com.moyu.boot.system.model.param.SysOrgParam;
import com.moyu.boot.system.service.SysOrgService;
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
 * @author shisong
 * @description 针对表【sys_org(组织机构表)】的数据库操作Service实现
 * @createDate 2024-11-26 09:55:33
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {

    /**
     * 组织结构树(本地缓存)
     */
    private Tree<String> rootTree;

    @Override
    public List<String> childrenCodeList(String orgCode) {
        List<String> codeList = new ArrayList<>();
        List<SysOrg> orgList = this.baseMapper.selectChildren(orgCode);
        orgList.forEach(e -> codeList.add(e.getCode()));
//        this.baseMapper.selectAll(Wrappers.lambdaQuery(SysOrg.class).eq(SysOrg::getCode, orgCode));
        return codeList;
    }

    @Override
    public List<SysOrg> list(SysOrgParam orgParam) {
        QueryWrapper<SysOrg> queryWrapper = new QueryWrapper<SysOrg>().checkSqlInjection();
        // 查询条件
        queryWrapper.lambda()
                // 关键词搜索
                .like(StrUtil.isNotBlank(orgParam.getSearchKey()), SysOrg::getName, orgParam.getSearchKey())
                // 指定父节点
                .eq(ObjectUtil.isNotEmpty(orgParam.getParentCode()), SysOrg::getParentCode, orgParam.getParentCode())
                // 指定状态
                .eq(ObjectUtil.isNotEmpty(orgParam.getStatus()), SysOrg::getStatus, orgParam.getStatus())
                .eq(SysOrg::getDeleteFlag, 0)
                .orderByAsc(SysOrg::getSortNum);
        // 查询
        List<SysOrg> orgList = this.list(queryWrapper);
        return orgList;
    }

    /**
     * 获取组织分页
     */
    @Override
    public PageData<SysOrg> pageList(SysOrgParam orgParam) {
        String parentCode = orgParam.getParentCode();
        // 查询条件
        LambdaQueryWrapper<SysOrg> queryWrapper = Wrappers.lambdaQuery(SysOrg.class)
                // 关键词搜索
                .like(StrUtil.isNotBlank(orgParam.getSearchKey()), SysOrg::getName, orgParam.getSearchKey())
                // 指定父节点 = 父节点+直接子节点
                //.and(ObjectUtil.isNotEmpty(parentCode), e -> e.eq(SysOrg::getCode, parentCode).or().eq(SysOrg::getParentCode, parentCode))
                // 指定父节点
                .eq(ObjectUtil.isNotEmpty(parentCode), SysOrg::getParentCode, parentCode)
                // 指定状态
                .eq(ObjectUtil.isNotEmpty(orgParam.getStatus()), SysOrg::getStatus, orgParam.getStatus())
                .eq(SysOrg::getDeleteFlag, 0)
                .orderByAsc(SysOrg::getSortNum);
        // 非ROOT则限制数据权限
        if (!SecurityUtils.isRoot()) {
            // 指定的列名
            Integer dataScope = SecurityUtils.getLoginUser().getDataScope();
            if (DataScopeEnum.SELF.getCode().equals(dataScope)) {
                String username = SecurityUtils.getLoginUser().getUsername();
                queryWrapper.and(e -> e.eq(SysOrg::getCreateBy, username));
            } else if (DataScopeEnum.ORG.getCode().equals(dataScope)) {
                String orgCode = SecurityUtils.getLoginUser().getOrgCode();
                queryWrapper.and(e -> e.eq(SysOrg::getCode, orgCode));
            } else if (DataScopeEnum.ORG_CHILD.getCode().equals(dataScope)) {
                String orgCode = SecurityUtils.getLoginUser().getOrgCode();
                // find_in_set函数比like高效
//                queryWrapper.and(e -> e.eq(SysOrg::getCode, orgCode).or().like(SysOrg::getOrgPath, orgCode));
                queryWrapper.and(e -> e.eq(SysOrg::getCode, orgCode).or().apply("find_in_set('" + orgCode + "', org_path)"));
            } else if (DataScopeEnum.ORG_DEFINE.getCode().equals(dataScope)) {
                Set<String> scopes = SecurityUtils.getLoginUser().getScopes();
                queryWrapper.and(e -> e.in(SysOrg::getCode, scopes));
            }
        }
        // 分页查询
        Page<SysOrg> page = new Page<>(orgParam.getPageNum(), orgParam.getPageSize());
        Page<SysOrg> orgPage = this.page(page, queryWrapper);
        return new PageData<>(orgPage.getTotal(), orgPage.getRecords());
    }

    /**
     * 组织机构(树太大需要加缓存)
     */
    @Override
    public List<Tree<String>> tree() {
        return singleTree().getChildren();
    }

    @Override
    public Tree<String> singleTree() {
//        if (ObjectUtil.isEmpty(rootTree)) {
//            rootTree = loadRootTree();
//        }
        return loadRootTree();
    }

    @Override
    public SysOrg detail(SysOrgParam orgParam) {
        LambdaQueryWrapper<SysOrg> queryWrapper = Wrappers.lambdaQuery(SysOrg.class)
                .eq(ObjectUtil.isNotEmpty(orgParam.getId()), SysOrg::getId, orgParam.getId())
                .eq(ObjectUtil.isNotEmpty(orgParam.getCode()), SysOrg::getCode, orgParam.getCode());
        // id、code均为唯一标识
        SysOrg sysOrg = this.getOne(queryWrapper);
        if (sysOrg == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        return sysOrg;
    }

    @Override
    public void add(SysOrgParam orgParam) {
        // 若指定了唯一编码code，则必须全局唯一
        if (!Strings.isNullOrEmpty(orgParam.getCode())) {
            // 查询指定code
            SysOrg org = this.getOne(new LambdaQueryWrapper<SysOrg>()
                    .eq(SysOrg::getCode, orgParam.getCode())
                    .eq(SysOrg::getDeleteFlag, 0));
            if (org != null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "唯一编码重复，请更换或留空自动生成");
            }
        }
        // 组装SysOrg
        SysOrg org = buildSysOrg(orgParam);
        org.setId(null);
        // 若未指定唯一编码code，则自动生成
        if (Strings.isNullOrEmpty(org.getCode())) {
            // 唯一code RandomUtil.randomString(10)、IdUtil.objectId()24位
            org.setCode(IdUtil.objectId());
        }
        // 组织机构层级路径,逗号分隔,父节点在后(不包含本节点)
        Tree<String> rootTree = singleTree();
        List<String> list = TreeUtil.getParentsId(rootTree.getNode(orgParam.getParentCode()), true);
        org.setOrgPath(SysConstants.COMMA_JOINER.join(list));
        this.save(org);
    }

    @Override
    public void deleteByIds(SysOrgParam orgParam) {
        // 待删除的id集合
        Set<Long> idSet = orgParam.getIds();
        // 逻辑删除
        UpdateWrapper<SysOrg> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idSet).set("delete_flag", 1);
        this.update(updateWrapper);
    }

    @Override
    public void deleteTree(SysOrgParam orgParam) {
        // 要集联删除，子节点也要全部删除
        QueryWrapper<SysOrg> queryWrapper = new QueryWrapper<SysOrg>().checkSqlInjection();
        // 查询所有的记录
        queryWrapper.lambda()
                // 查询部分字段
                .select(SysOrg::getId, SysOrg::getCode, SysOrg::getParentCode)
                .eq(SysOrg::getDeleteFlag, 0);
        // 查询所有记录
        List<SysOrg> orgList = this.list(queryWrapper);
        // 待删除节点的code集合
        Set<String> codeSet = orgParam.getCodes();

        // 待删除的id集合(先把指定节点加入集合)
        Set<Long> idSet = orgList.stream()
                .filter(org -> codeSet.contains(org.getCode()))
                .map(SysOrg::getId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(idSet)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "删除失败,未查到指定数据");
        }

        // 循环查找子节点,并加入到待删除集合
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
        UpdateWrapper<SysOrg> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idSet).set("delete_flag", 1);
        this.update(updateWrapper);
    }

    @Override
    public void edit(SysOrgParam orgParam) {
        SysOrg oldOrg = this.detail(orgParam);
        // 不使用beanCopy是为了效率
        SysOrg updateOrg = buildSysOrg(orgParam);
        updateOrg.setId(oldOrg.getId());
        // 若父节点有变化，则orgPath也要变
        if (ObjectUtil.isEmpty(oldOrg.getOrgPath()) || ObjectUtil.notEqual(oldOrg.getParentCode(), orgParam.getParentCode())) {
            // 组织机构层级路径,逗号分隔,父节点在后(不包含本节点)
            Tree<String> rootTree = singleTree();
            List<String> list = TreeUtil.getParentsId(rootTree.getNode(orgParam.getParentCode()), true);
            updateOrg.setOrgPath(SysConstants.COMMA_JOINER.join(list));
            // 本节点的子节点orgPath也应该改变，待tree更新之后才可以修改 TODO
        }
        this.updateById(updateOrg);
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
     * 从数据库中加载组织机构树
     */
    private Tree<String> loadRootTree() {
        // 查询所有组织结构
        List<SysOrg> orgList = this.list(Wrappers.lambdaQuery(SysOrg.class)
                // 查询部分字段
                .select(SysOrg::getCode, SysOrg::getParentCode, SysOrg::getName, SysOrg::getSortNum, SysOrg::getOrgType)
                .eq(SysOrg::getDeleteFlag, 0)
                .orderByAsc(SysOrg::getSortNum)
        );
        // 构建树
        return buildSingleTree(orgList, SysConstants.ROOT_NODE_ID);
    }
}




