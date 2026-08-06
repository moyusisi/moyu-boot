package com.moyu.boot.plugin.thirdPartyApi.service.impl;

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
import com.moyu.boot.plugin.thirdPartyApi.mapper.ThirdPartyApiMapper;
import com.moyu.boot.plugin.thirdPartyApi.model.entity.ThirdPartyApi;
import com.moyu.boot.plugin.thirdPartyApi.model.param.ThirdPartyApiParam;
import com.moyu.boot.plugin.thirdPartyApi.model.vo.ThirdPartyApiVO;
import com.moyu.boot.plugin.thirdPartyApi.service.ThirdPartyApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 三方集成接口服务实现类
 *
 * @author moyusisi
 * @since 2026-08-06
 */
@Slf4j
@Service
public class ThirdPartyApiServiceImpl extends ServiceImpl<ThirdPartyApiMapper, ThirdPartyApi> implements ThirdPartyApiService {

    @Override
    public List<ThirdPartyApiVO> list(ThirdPartyApiParam param) {
        // 查询条件
        QueryWrapper<ThirdPartyApi> queryWrapper = Wrappers.query(ThirdPartyApi.class).checkSqlInjection();
        // 指定code查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getCode()), ThirdPartyApi::getCode, param.getCode());
        // 指定name查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getName()), ThirdPartyApi::getName, param.getName());
        // 指定url查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getUrl()), ThirdPartyApi::getUrl, param.getUrl());
        // 指定thirdAppName查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getThirdAppName()), ThirdPartyApi::getThirdAppName, param.getThirdAppName());
        // 仅查询未删除的
        queryWrapper.lambda().eq(ThirdPartyApi::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(SortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(ThirdPartyApi::getUpdateTime);
        }
        // 查询
        List<ThirdPartyApi> thirdPartyApiList = this.list(queryWrapper);
        // 转换为voList
        List<ThirdPartyApiVO> voList = buildThirdPartyApiVOList(thirdPartyApiList);
        return voList;
    }

    @Override
    public PageData<ThirdPartyApiVO> pageList(ThirdPartyApiParam param) {
        // 查询条件
        QueryWrapper<ThirdPartyApi> queryWrapper = Wrappers.query(ThirdPartyApi.class).checkSqlInjection();
        // 指定code查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getCode()), ThirdPartyApi::getCode, param.getCode());
        // 指定name查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getName()), ThirdPartyApi::getName, param.getName());
        // 指定url查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getUrl()), ThirdPartyApi::getUrl, param.getUrl());
        // 指定thirdAppName查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getThirdAppName()), ThirdPartyApi::getThirdAppName, param.getThirdAppName());
        // 仅查询未删除的
        queryWrapper.lambda().eq(ThirdPartyApi::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(SortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(ThirdPartyApi::getUpdateTime);
        }
        // 分页查询
        Page<ThirdPartyApi> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<ThirdPartyApi> thirdPartyApiPage = this.page(page, queryWrapper);
        List<ThirdPartyApiVO> voList = buildThirdPartyApiVOList(thirdPartyApiPage.getRecords());
        return new PageData<>(thirdPartyApiPage.getTotal(), voList);
    }

    @Override
    public ThirdPartyApiVO detail(ThirdPartyApiParam param) {
        // 查询
        ThirdPartyApi thirdPartyApi = this.getById(param.getId());
        if (thirdPartyApi == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        ThirdPartyApiVO vo = BeanUtil.copyProperties(thirdPartyApi, ThirdPartyApiVO.class);
        return vo;
    }

    @Override
    public void add(ThirdPartyApiParam param) {
        // 属性复制
        ThirdPartyApi thirdPartyApi = BeanUtil.copyProperties(param, ThirdPartyApi.class);
        // 其他处理
        thirdPartyApi.setId(null);
        this.save(thirdPartyApi);
    }

    @Override
    public void update(ThirdPartyApiParam param) {
        // 通过主键id查询原有数据
        ThirdPartyApi old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        ThirdPartyApi toUpdate = BeanUtil.copyProperties(param, ThirdPartyApi.class, BaseEntity.UPDATE_TIME, BaseEntity.UPDATE_BY);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(ThirdPartyApiParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 删除时先查再删
        List<ThirdPartyApi> toDelList = this.listByIds(idSet);
        // 要删除的和查询到的进行比对
        if (ObjectUtil.notEqual(idSet.size(), toDelList.size())) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(ThirdPartyApi.class).in(ThirdPartyApi::getId, idSet).set(ThirdPartyApi::getDeleted, 1));
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<ThirdPartyApiVO> buildThirdPartyApiVOList(List<ThirdPartyApi> entityList) {
        List<ThirdPartyApiVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (ThirdPartyApi entity : entityList) {
            ThirdPartyApiVO vo = BeanUtil.copyProperties(entity, ThirdPartyApiVO.class);
            voList.add(vo);
        }
        return voList;
    }
}
