package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定name查询
        queryWrapper.like(SysApi::getName, param.getName(), ObjectUtil.isNotEmpty(param.getName()));
        // 指定code查询
        queryWrapper.like(SysApi::getCode, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        // 指定path查询
        queryWrapper.like(SysApi::getPath, param.getPath(), ObjectUtil.isNotEmpty(param.getPath()));
        // 指定apiType查询
        queryWrapper.eq(SysApi::getApiType, param.getApiType(), ObjectUtil.isNotEmpty(param.getApiType()));
        // 仅查询未删除的
        queryWrapper.eq(SysApi::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(SysApi::getUpdateTime, false);
        }
        // 查询
        List<SysApiVO> voList = this.listAs(queryWrapper, SysApiVO.class);
        return voList;
    }

    @Override
    public PageData<SysApiVO> pageList(SysApiParam param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定name查询
        queryWrapper.like(SysApi::getName, param.getName(), ObjectUtil.isNotEmpty(param.getName()));
        // 指定code查询
        queryWrapper.like(SysApi::getCode, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        // 指定path查询
        queryWrapper.like(SysApi::getPath, param.getPath(), ObjectUtil.isNotEmpty(param.getPath()));
        // 指定apiType查询
        queryWrapper.eq(SysApi::getApiType, param.getApiType(), ObjectUtil.isNotEmpty(param.getApiType()));
        // 仅查询未删除的
        queryWrapper.eq(SysApi::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(SysApi::getUpdateTime, false);
        }
        // 分页查询
        Page<SysApiVO> page = Page.of(param.getPageNum(), param.getPageSize());
        Page<SysApiVO> voPage = this.pageAs(page, queryWrapper, SysApiVO.class);
        return new PageData<>(voPage.getTotalRow(), voPage.getRecords());
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
        Long count = this.count(QueryWrapper.create().in(SysApi::getId, idSet));
        // 查到的数量比对
        if (ObjectUtil.notEqual(idSet.size(), count)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        UpdateChain.of(SysApi.class)
                .set(SysApi::getDeleted, 1)
                .where(SysApi::getId).in(idSet)
                .update();
    }
}
