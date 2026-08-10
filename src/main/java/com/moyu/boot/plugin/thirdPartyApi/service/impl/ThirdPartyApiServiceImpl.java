package com.moyu.boot.plugin.thirdPartyApi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.http.ForestResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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

import java.net.ConnectException;
import java.util.*;

/**
 * 三方集成接口服务实现类
 *
 * @author moyusisi
 * @since 2026-08-06
 */
@Slf4j
@Service
public class ThirdPartyApiServiceImpl extends ServiceImpl<ThirdPartyApiMapper, ThirdPartyApi> implements ThirdPartyApiService {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
        // 指定debugStatus查询
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getDebugStatus()), ThirdPartyApi::getDebugStatus, param.getDebugStatus());
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
        // 指定debugStatus查询
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getDebugStatus()), ThirdPartyApi::getDebugStatus, param.getDebugStatus());
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

    @Override
    public void debugApi(ThirdPartyApiParam param) {
        // 查询原有数据
        ThirdPartyApi old = Db.getOne(Wrappers.lambdaQuery(ThirdPartyApi.class)
                .eq(ThirdPartyApi::getCode, param.getCode())
                .eq(ThirdPartyApi::getDeleted, 0)
        );
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未找到指定接口");
        }

        // 组装请求参数
        String url = old.getUrl();
        String method = old.getRequestMethod();
        String header = param.getRequestHeader();
        Map<String, Object> headerMap = null;
        if (StrUtil.isNotEmpty(header)) {
            headerMap = gson.fromJson(header, new TypeToken<Map<String, Object>>() {
            }.getType());
        }
        String body = param.getRequestBody();
        Map<String, Object> bodyMap = new HashMap<>();
        if (StrUtil.isNotEmpty(body)) {
            bodyMap = gson.fromJson(body, new TypeToken<Map<String, Object>>() {
            }.getType());
        }
        // 属性复制
        ThirdPartyApi toUpdate = new ThirdPartyApi();
        toUpdate.setId(old.getId());
        toUpdate.setRequestHeader(header);
        toUpdate.setRequestBody(body);

        // 构造请求对象 发送请求
        ForestResponse<?> response = Forest.request().url(url)
                .setType(ForestRequestType.findType(method))
                .contentTypeJson()     // 指定请求体为JSON格式
                .addHeader(headerMap)
                .addBody(bodyMap)
                .executeAsResponse();

        toUpdate.setRequestTime(response.getRequestTime());
        toUpdate.setResponseTime(response.getResponseTime());
        // noException() && statusOk()
        toUpdate.setDebugStatus(response.isSuccess() ? 1 : 0);
        // http状态码，异常会被包装成-1
        toUpdate.setStatusCode(Objects.toString(response.getStatusCode()));
        toUpdate.setResponseBody(response.readAsString());

        BusinessException ex = null;
        if (response.isTimeout()) {
            // 网络请求是否超时(SocketTimeoutException,状态码被包装成-1)
            ex = new BusinessException(ResultCodeEnum.THIRD_PARTY_SERVICE_ERROR, "网络请求超时");
        } else if (response.getException() != null) {
            if (response.getException() instanceof ConnectException) {
                // 建立连接失败
                ex = new BusinessException(ResultCodeEnum.THIRD_PARTY_SERVICE_ERROR, "网络连接失败");
            } else {
                // 请求过程中产生异常
                ex = new BusinessException(ResultCodeEnum.THIRD_PARTY_SERVICE_ERROR, "请求发生异常");
                log.error("请求发生异常", response.getException());
            }
        } else if (!response.statusOk()) {
            // http响应码不在 100 ~ 399 范围内 (网络请求成功但HTTP状态码错误)
            ex = new BusinessException(ResultCodeEnum.THIRD_PARTY_SERVICE_ERROR, "网络请求成功但HTTP状态码错误");
        }
        // 若无信息且未成功
        if (StrUtil.isEmpty(toUpdate.getResponseBody()) && ex != null) {
            toUpdate.setResponseBody(ex.getMessage());
        }
        // 更新数据
        this.updateById(toUpdate);
        // 调试反馈
        if (ex != null) {
            throw ex;
        }
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
