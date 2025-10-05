package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.mapper.ScopeMapper;
import com.moyu.boot.system.model.entity.Scope;
import com.moyu.boot.system.model.param.ScopeParam;
import com.moyu.boot.system.model.vo.ScopeVO;
import com.moyu.boot.system.service.ScopeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 分组服务实现类
 *
 * @author moyusisi
 * @since 2025-10-05
 */
@Slf4j
@Service
public class ScopeServiceImpl extends ServiceImpl<ScopeMapper, Scope> implements ScopeService {

    @Override
    public List<ScopeVO> list(ScopeParam param) {
        // 查询条件
        LambdaQueryWrapper<Scope> queryWrapper = Wrappers.lambdaQuery(Scope.class);
        // 指定name查询条件
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getName()), Scope::getName, param.getName());
        // 指定code查询条件
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), Scope::getCode, param.getCode());
        // 指定orgName查询条件
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getOrgName()), Scope::getOrgName, param.getOrgName());
        // 仅查询未删除的
        queryWrapper.eq(Scope::getDeleted, 0);
        // 指定createTime查询条件
        if (param.getCreateTimeRange() != null && param.getCreateTimeRange().size() > 1 && ObjectUtil.isAllNotEmpty(param.getCreateTimeRange().get(0), param.getCreateTimeRange().get(1))) {
            queryWrapper.between(Scope::getCreateTime, param.getCreateTimeRange().get(0), param.getCreateTimeRange().get(1));
        }
        // TODO 是否需要排序
        queryWrapper.orderByDesc(Scope::getUpdateTime);
        // 查询
        List<Scope> scopeList = this.list(queryWrapper);
        // 转换为voList
        List<ScopeVO> voList = buildScopeVOList(scopeList);
        return voList;
    }

    @Override
    public PageData<ScopeVO> pageList(ScopeParam param) {
        // 查询条件
        LambdaQueryWrapper<Scope> queryWrapper = Wrappers.lambdaQuery(Scope.class);
        // 指定name查询条件
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getName()), Scope::getName, param.getName());
        // 指定code查询条件
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), Scope::getCode, param.getCode());
        // 指定orgName查询条件
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getOrgName()), Scope::getOrgName, param.getOrgName());
        // 仅查询未删除的
        queryWrapper.eq(Scope::getDeleted, 0);
        // 指定createTime查询条件
        if (param.getCreateTimeRange() != null && param.getCreateTimeRange().size() > 1 && ObjectUtil.isAllNotEmpty(param.getCreateTimeRange().get(0), param.getCreateTimeRange().get(1))) {
            queryWrapper.between(Scope::getCreateTime, param.getCreateTimeRange().get(0), param.getCreateTimeRange().get(1));
        }
        // TODO 是否需要排序
        queryWrapper.orderByDesc(Scope::getUpdateTime);
        // 分页查询
        Page<Scope> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<Scope> scopePage = this.page(page, queryWrapper);
        List<ScopeVO> voList = buildScopeVOList(scopePage.getRecords());
        return new PageData<>(scopePage.getTotal(), voList);
    }

    @Override
    public ScopeVO detail(ScopeParam param) {
        // 查询
        Scope scope = this.getById(param.getId());
        if (scope == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        // 转换为vo
        ScopeVO vo = BeanUtil.copyProperties(scope, ScopeVO.class);
        return vo;
    }

    @Override
    public void add(ScopeParam param) {
        // 属性复制
        Scope scope = BeanUtil.copyProperties(param, Scope.class);
        // 其他处理
        scope.setId(null);
        this.save(scope);
    }

    @Override
    public void update(ScopeParam param) {
        // 通过主键id查询原有数据
        Scope old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "更新失败，未查到原数据");
        }
        // 属性复制
        Scope toUpdate = BeanUtil.copyProperties(param, Scope.class);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(ScopeParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(Scope.class).in(Scope::getId, idSet).set(Scope::getDeleted, 1));
    }

    /**
     * 实体对象生成展示对象 entity -> vo
     */
    private ScopeVO buildScopeVO(Scope entity) {
        if (entity == null) {
            return null;
        }
        ScopeVO vo = new ScopeVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setCode(entity.getCode());
        vo.setOrgCode(entity.getOrgCode());
        vo.setOrgName(entity.getOrgName());
        vo.setDataScope(entity.getDataScope());
        vo.setScopeSet(entity.getScopeSet());
        vo.setOrgPath(entity.getOrgPath());
        vo.setSortNum(entity.getSortNum());
        vo.setStatus(entity.getStatus());
        vo.setExtJson(entity.getExtJson());
        vo.setRemark(entity.getRemark());
        vo.setDeleted(entity.getDeleted());
        vo.setCreateTime(entity.getCreateTime());
        vo.setCreateBy(entity.getCreateBy());
        vo.setUpdateTime(entity.getUpdateTime());
        vo.setUpdateBy(entity.getUpdateBy());
        return vo;
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<ScopeVO> buildScopeVOList(List<Scope> entityList) {
        List<ScopeVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (Scope entity : entityList) {
            voList.add(buildScopeVO(entity));
        }
        return voList;
    }
}
