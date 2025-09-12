package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.security.util.SecurityUtils;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.enums.RelationTypeEnum;
import com.moyu.boot.system.mapper.SysScopeMapper;
import com.moyu.boot.system.model.entity.SysRelation;
import com.moyu.boot.system.model.entity.SysScope;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysRelationParam;
import com.moyu.boot.system.model.param.SysScopeParam;
import com.moyu.boot.system.model.param.SysUserParam;
import com.moyu.boot.system.service.SysOrgService;
import com.moyu.boot.system.service.SysRelationService;
import com.moyu.boot.system.service.SysScopeService;
import com.moyu.boot.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shisong
 * @description 针对表【sys_scope(数据权限分组表)】的数据库操作Service实现
 * @createDate 2025-02-27 10:19:59
 */
@Slf4j
@Service
public class SysScopeServiceImpl extends ServiceImpl<SysScopeMapper, SysScope> implements SysScopeService {

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private SysRelationService sysRelationService;

    @Resource
    private SysUserService sysUserService;

    @Override
    public PageData<SysScope> pageList(SysScopeParam scopeParam) {
        // 查询条件
        LambdaQueryWrapper<SysScope> queryWrapper = Wrappers.lambdaQuery(SysScope.class)
                // 关键词搜索
                .like(StrUtil.isNotBlank(scopeParam.getSearchKey()), SysScope::getName, scopeParam.getSearchKey())
                // 指定orgCode
                .eq(StrUtil.isNotBlank(scopeParam.getOrgCode()), SysScope::getOrgCode, scopeParam.getOrgCode())
                // 指定状态
                .eq(ObjectUtil.isNotEmpty(scopeParam.getStatus()), SysScope::getStatus, scopeParam.getStatus())
                .eq(SysScope::getDeleteFlag, 0)
                .orderByAsc(SysScope::getSortNum);
        // 非ROOT则限制数据权限
        if (!SecurityUtils.isRoot()) {
            // 指定的列名
            Integer dataScope = SecurityUtils.getLoginUser().getDataScope();
            if (DataScopeEnum.SELF.getCode().equals(dataScope)) {
                String username = SecurityUtils.getLoginUser().getUsername();
                queryWrapper.and(e -> e.eq(SysScope::getCreateBy, username));
            } else if (DataScopeEnum.ORG.getCode().equals(dataScope)) {
                String orgCode = SecurityUtils.getLoginUser().getOrgCode();
                queryWrapper.and(e -> e.eq(SysScope::getOrgCode, orgCode));
            } else if (DataScopeEnum.ORG_CHILD.getCode().equals(dataScope)) {
                String orgCode = SecurityUtils.getLoginUser().getOrgCode();
                // find_in_set函数比like高效
//                queryWrapper.and(e -> e.eq(SysScope::getOrgCode, orgCode).or().like(SysScope::getOrgPath, orgCode));
                queryWrapper.and(e -> e.eq(SysScope::getOrgCode, orgCode).or().apply("find_in_set('" + orgCode + "', org_path)"));
            } else if (DataScopeEnum.ORG_DEFINE.getCode().equals(dataScope)) {
                Set<String> scopes = SecurityUtils.getLoginUser().getScopes();
                queryWrapper.and(e -> e.in(SysScope::getOrgCode, scopes));
            }
            log.debug("数据权限为:{}, 已追加过滤条件", DataScopeEnum.getByCode(dataScope));
        }
        // 分页查询
        Page<SysScope> page = new Page<>(scopeParam.getPageNum(), scopeParam.getPageSize());
        Page<SysScope> scopePage = this.page(page, queryWrapper);
        return new PageData<>(scopePage.getTotal(), scopePage.getRecords());
    }

    @Override
    public SysScope detail(SysScopeParam scopeParam) {
        LambdaQueryWrapper<SysScope> queryWrapper = Wrappers.lambdaQuery(SysScope.class)
                .eq(ObjectUtil.isNotEmpty(scopeParam.getId()), SysScope::getId, scopeParam.getId())
                .eq(ObjectUtil.isNotEmpty(scopeParam.getCode()), SysScope::getCode, scopeParam.getCode());
        // id、code均为唯一标识
        SysScope sysScope = this.getOne(queryWrapper);
        if (sysScope == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        return sysScope;
    }

    @Override
    public void add(SysScopeParam scopeParam) {
        // 属性复制
        SysScope scope = BeanUtil.copyProperties(scopeParam, SysScope.class);
        scope.setId(null);
        // 若未指定唯一编码code，则自动生成
        if (Strings.isNullOrEmpty(scope.getCode())) {
            // 唯一code RandomUtil.randomString(10)、IdUtil.objectId()24位、IdUtil.getSnowflakeNextId()19位
            scope.setCode(SysConstants.SCOPE_PREFIX + IdUtil.getSnowflakeNextId());
        }
        // 若指定了直属组织，则设置所属组织名称
        if (ObjectUtil.isNotEmpty(scope.getOrgCode())) {
            // 获取组织结构树
            Tree<String> rootTree = sysOrgService.singleTree();
            Tree<String> orgNode = rootTree.getNode(scope.getOrgCode());
            // 设置直属机构名称
            scope.setOrgName(orgNode.getName().toString());
            // 组织机构层级路径,逗号分隔,父节点在后
            List<String> list = TreeUtil.getParentsId(orgNode, true);
            scope.setOrgPath(SysConstants.COMMA_JOINER.join(list));
        }
        // 若是自定义范围,需要处理
        if (ObjectUtil.equal(scopeParam.getScopeType(), DataScopeEnum.ORG_DEFINE.getCode())) {
            Assert.notEmpty(scopeParam.getScopeSet(), "自定义数据范围时, scopeSet不能为空");
            scope.setScopeSet(scopeParam.getScopeSet());
        }
        this.save(scope);
    }

    @Override
    public void deleteByIds(SysScopeParam scopeParam) {
        // 待删除的id集合
        Set<Long> idSet = scopeParam.getIds();
        // 逻辑删除
        UpdateWrapper<SysScope> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idSet).set("delete_flag", 1);
        this.update(updateWrapper);
    }

    @Override
    public void edit(SysScopeParam scopeParam) {
        SysScope oldScope = this.detail(scopeParam);
        // 属性复制
        SysScope updateScope = BeanUtil.copyProperties(scopeParam, SysScope.class);
        updateScope.setId(oldScope.getId());
        // 若新指定了直属组织，则设置组织名
        if (ObjectUtil.notEqual(oldScope.getOrgCode(), updateScope.getOrgCode()) && ObjectUtil.isNotEmpty(updateScope.getOrgCode())) {
            // 获取组织结构树
            Tree<String> rootTree = sysOrgService.singleTree();
            Tree<String> orgNode = rootTree.getNode(updateScope.getOrgCode());
            // 设置直属机构名称
            updateScope.setOrgName(orgNode.getName().toString());
            // 组织机构层级路径,逗号分隔,父节点在后
            List<String> list = TreeUtil.getParentsId(orgNode, true);
            updateScope.setOrgPath(SysConstants.COMMA_JOINER.join(list));
        }
        // 若是自定义范围,需要处理
        if (ObjectUtil.equal(scopeParam.getScopeType(), DataScopeEnum.ORG_DEFINE.getCode())) {
            Assert.notEmpty(scopeParam.getScopeSet(), "自定义数据范围时, scopeSet不能为空");
            updateScope.setScopeSet(scopeParam.getScopeSet());
        }
        this.updateById(updateScope);
    }

    @Override
    public List<SysUser> scopeUserList(SysScopeParam scopeParam) {
        // 查询指定scope的所有user
        List<SysRelation> list = sysRelationService.list(SysRelationParam.builder().objectId(scopeParam.getCode())
                .relationType(RelationTypeEnum.SCOPE_HAS_USER.getCode()).build());
        if (ObjectUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        // userSet
        Set<String> userSet = list.stream().map(SysRelation::getTargetId).collect(Collectors.toSet());
        // 查询用户(可指定搜索词)
        List<SysUser> userList = sysUserService.list(SysUserParam.builder()
                .searchKey(scopeParam.getSearchKey())
                .orgCode(scopeParam.getOrgCode())
                .codeSet(userSet).build());
        return userList;
    }

    @Override
    public void scopeAddUser(SysScopeParam scopeParam) {
        String objectId = scopeParam.getCode();
        Set<String> targetSet = scopeParam.getCodeSet();
        if (ObjectUtil.isEmpty(targetSet)) {
            return;
        }
        Set<String> oldSet = new HashSet<>();
        // 查询指定scope包含的user，放入oldSet
        sysRelationService.list(SysRelationParam.builder().objectId(objectId).targetSet(targetSet)
                        .relationType(RelationTypeEnum.SCOPE_HAS_USER.getCode()).build())
                .forEach(e -> oldSet.add(e.getTargetId()));
        // 从target中删除已经存在的
        targetSet.removeAll(oldSet);
        // 再次判断要新增的内容为空则返回
        if (ObjectUtil.isEmpty(targetSet)) {
            return;
        }
        List<SysRelation> addList = new ArrayList<>();
        targetSet.forEach(code -> {
            SysRelation entity = new SysRelation();
            entity.setObjectId(objectId);
            entity.setTargetId(code);
            entity.setRelationType(RelationTypeEnum.SCOPE_HAS_USER.getCode());
            addList.add(entity);
        });
        sysRelationService.saveBatch(addList);
    }

    @Override
    public void scopeDeleteUser(SysScopeParam scopeParam) {
        if (ObjectUtil.isEmpty(scopeParam.getCodeSet())) {
            return;
        }
        // 要删除的ids
        Set<Long> ids = new HashSet<>();
        // 查询指定scope中存在的user，加入ids待删
        sysRelationService.list(SysRelationParam.builder().objectId(scopeParam.getCode()).targetSet(scopeParam.getCodeSet())
                        .relationType(RelationTypeEnum.SCOPE_HAS_USER.getCode()).build())
                .forEach(e -> ids.add(e.getId()));
        // 删除
        if (ObjectUtil.isNotEmpty(ids)) {
            sysRelationService.removeByIds(ids);
        }
    }

    @Override
    public Set<String> userDataScopes(String userId) {
        Set<String> scopes = new HashSet<>();
        // 查关系
        LambdaQueryWrapper<SysRelation> queryRelationWrapper = Wrappers.lambdaQuery(SysRelation.class)
                .eq(SysRelation::getTargetId, userId)
                .eq(SysRelation::getRelationType, RelationTypeEnum.SCOPE_HAS_USER.getCode());
        // 通过SCOPE_HAS_USER查关系
        Set<String> scopeSet = new HashSet<>();
        sysRelationService.list(queryRelationWrapper).forEach(e -> scopeSet.add(e.getObjectId()));
        if (ObjectUtil.isEmpty(scopeSet)) {
            return scopes;
        }
        // 查数据范围分组
        LambdaQueryWrapper<SysScope> queryScopeWrapper = Wrappers.lambdaQuery(SysScope.class)
                .in(SysScope::getCode, scopeSet)
                .eq(SysScope::getStatus, 0)
                .eq(SysScope::getDeleteFlag, 0);
        List<SysScope> scopeList = this.list(queryScopeWrapper);
        if (ObjectUtil.isEmpty(scopeList)) {
            return scopes;
        }
        // 根据scopeType计算最终的scopes
        scopeList.forEach(e -> {
            if (ObjectUtil.equal(e.getScopeType(), DataScopeEnum.ORG.getCode())) {
                scopes.add(e.getOrgCode());
            } else if (ObjectUtil.equal(e.getScopeType(), DataScopeEnum.ORG_DEFINE.getCode())) {
                List<String> list = SysConstants.COMMA_SPLITTER.splitToList(e.getScopeSet());
                scopes.addAll(list);
            } else if (ObjectUtil.equal(e.getScopeType(), DataScopeEnum.ORG_CHILD.getCode())) {
                // 添加org
                scopes.add(e.getOrgCode());
                // 从rootTree中获取所有child（有缓存时）
                Tree<String> orgTree = sysOrgService.singleTree().getNode(e.getOrgCode());
                orgTree.walk(node -> scopes.add(node.getId()));
                // 从数据库中获取所有child（无缓存时）
//                List<String> childList = sysOrgService.childrenCodeList(e.getOrgCode());
//                scopes.addAll(childList);
            }
        });
        return scopes;
    }
}




