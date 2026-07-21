package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.enums.SortOrderEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.mapper.SysApiMapper;
import com.moyu.boot.system.model.entity.SysApi;
import com.moyu.boot.system.model.param.SysApiParam;
import com.moyu.boot.system.model.vo.SysApiVO;
import com.moyu.boot.system.service.SysApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 接口信息服务实现类
 *
 * @author moyusisi
 * @since 2026-07-20
 */
@Slf4j
@Service
public class SysApiServiceImpl extends ServiceImpl<SysApiMapper, SysApi> implements SysApiService {

    @Override
    public List<SysApiVO> list(SysApiParam param) {
        // 查询条件
        QueryWrapper<SysApi> queryWrapper = Wrappers.query(SysApi.class).checkSqlInjection();
        // 指定name查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getName()), SysApi::getName, param.getName());
        // 指定code查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getCode()), SysApi::getCode, param.getCode());
        // 指定path查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getPath()), SysApi::getPath, param.getPath());
        // 指定apiType查询
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getApiType()), SysApi::getApiType, param.getApiType());
        // 仅查询未删除的
        queryWrapper.lambda().eq(SysApi::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(SortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(SysApi::getUpdateTime);
        }
        // 查询
        List<SysApi> sysApiList = this.list(queryWrapper);
        // 转换为voList
        List<SysApiVO> voList = buildSysApiVOList(sysApiList);
        return voList;
    }

    @Override
    public PageData<SysApiVO> pageList(SysApiParam param) {
        // 查询条件
        QueryWrapper<SysApi> queryWrapper = Wrappers.query(SysApi.class).checkSqlInjection();
        // 指定name查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getName()), SysApi::getName, param.getName());
        // 指定code查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getCode()), SysApi::getCode, param.getCode());
        // 指定path查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getPath()), SysApi::getPath, param.getPath());
        // 指定apiType查询
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getApiType()), SysApi::getApiType, param.getApiType());
        // 仅查询未删除的
        queryWrapper.lambda().eq(SysApi::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(SortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(SysApi::getUpdateTime);
        }
        // 分页查询
        Page<SysApi> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<SysApi> sysApiPage = this.page(page, queryWrapper);
        List<SysApiVO> voList = buildSysApiVOList(sysApiPage.getRecords());
        return new PageData<>(sysApiPage.getTotal(), voList);
    }

    @Override
    public SysApiVO detail(SysApiParam param) {
        // 查询
        SysApi sysApi = this.getById(param.getId());
        if (sysApi == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        SysApiVO vo = BeanUtil.copyProperties(sysApi, SysApiVO.class);
        return vo;
    }

    @Override
    public void add(SysApiParam param) {
        // 属性复制
        SysApi sysApi = BeanUtil.copyProperties(param, SysApi.class);
        // 其他处理
        sysApi.setId(null);
        this.save(sysApi);
    }

    @Override
    public void update(SysApiParam param) {
        // 通过主键id查询原有数据
        SysApi old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        SysApi toUpdate = BeanUtil.copyProperties(param, SysApi.class, BaseEntity.UPDATE_TIME, BaseEntity.UPDATE_BY);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(SysApiParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 删除时先查再删
        List<SysApi> toDelList = this.listByIds(idSet);
        // 要删除的和查询到的进行比对
        if (ObjectUtil.notEqual(idSet.size(), toDelList.size())) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(SysApi.class).in(SysApi::getId, idSet).set(SysApi::getDeleted, 1));
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<SysApiVO> buildSysApiVOList(List<SysApi> entityList) {
        List<SysApiVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (SysApi entity : entityList) {
            SysApiVO vo = BeanUtil.copyProperties(entity, SysApiVO.class);
            voList.add(vo);
        }
        return voList;
    }
}
