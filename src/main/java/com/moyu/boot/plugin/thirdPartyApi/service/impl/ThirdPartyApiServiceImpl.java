package com.moyu.boot.plugin.thirdPartyApi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.http.ForestResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.moyu.boot.common.authZ.util.LoginUserUtils;
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
import com.moyu.boot.system.model.entity.SysApi;
import com.moyu.boot.system.model.entity.SysLog;
import com.moyu.boot.system.service.SysLogService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.DbChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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

    @Resource
    private SysLogService sysLogService;

    @Override
    public List<ThirdPartyApiVO> list(ThirdPartyApiParam param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定code查询
        queryWrapper.like(ThirdPartyApi::getCode, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        // 指定name查询
        queryWrapper.like(ThirdPartyApi::getName, param.getName(), ObjectUtil.isNotEmpty(param.getName()));
        // 指定url查询
        queryWrapper.like(ThirdPartyApi::getUrl, param.getUrl(), ObjectUtil.isNotEmpty(param.getUrl()));
        // 指定debugStatus查询
        queryWrapper.eq(ThirdPartyApi::getDebugStatus, param.getDebugStatus(), ObjectUtil.isNotEmpty(param.getDebugStatus()));
        // 仅查询未删除的
        queryWrapper.eq(ThirdPartyApi::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(ThirdPartyApi::getUpdateTime, false);
        }
        // 查询
        List<ThirdPartyApiVO> voList = this.listAs(queryWrapper, ThirdPartyApiVO.class);
        return voList;
    }

    @Override
    public PageData<ThirdPartyApiVO> pageList(ThirdPartyApiParam param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定code查询
        queryWrapper.like(ThirdPartyApi::getCode, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        // 指定name查询
        queryWrapper.like(ThirdPartyApi::getName, param.getName(), ObjectUtil.isNotEmpty(param.getName()));
        // 指定url查询
        queryWrapper.like(ThirdPartyApi::getUrl, param.getUrl(), ObjectUtil.isNotEmpty(param.getUrl()));
        // 指定debugStatus查询
        queryWrapper.eq(ThirdPartyApi::getDebugStatus, param.getDebugStatus(), ObjectUtil.isNotEmpty(param.getDebugStatus()));
        // 仅查询未删除的
        queryWrapper.eq(ThirdPartyApi::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(ThirdPartyApi::getUpdateTime, false);
        }
        // 分页查询
        Page<ThirdPartyApiVO> page = Page.of(param.getPageNum(), param.getPageSize());
        Page<ThirdPartyApiVO> voPage = this.pageAs(page, queryWrapper, ThirdPartyApiVO.class);
        return new PageData<>(voPage.getTotalRow(), voPage.getRecords());
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

    @Override
    public void debugApi(ThirdPartyApiParam param) {
        // 查询原有数据
        ThirdPartyApi old = this.getOne(QueryWrapper.create()
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
        toUpdate.setResponseBody(response.getContent());

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
        // 保存日志
        saveLog(old, bodyMap, response);
        // 调试反馈
        if (ex != null) {
            throw ex;
        }
    }

    @Override
    public String requestApi(String apiCode, Map<String, Object> headers, Map<String, Object> params) {
        // 查询原有数据
        ThirdPartyApi api = this.getOne(QueryWrapper.create().select(ThirdPartyApi::getUrl, ThirdPartyApi::getRequestMethod, ThirdPartyApi::getName)
                .eq(ThirdPartyApi::getCode, apiCode)
                .eq(ThirdPartyApi::getDeleted, 0)
        );
        if (api == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未找到指定接口");
        }
        if (CollectionUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        // 构造请求对象 发送请求
        ForestResponse<?> response = Forest.request().url(api.getUrl())
                .setType(ForestRequestType.findType(api.getRequestMethod()))
                .contentTypeJson()     // 指定请求体为JSON格式
                .addHeader(headers)
                .addBody(params)
                .executeAsResponse();
        // 保存日志
        saveLog(api, params, response);
        return response.getContent();
    }

    /**
     * 保存三方请求记录
     */
    private void saveLog(ThirdPartyApi api, Map<String, Object> params, ForestResponse<?> response) {
        SysLog sysLog = new SysLog();
        // 操作人
        sysLog.setName("三方接口调用");
        sysLog.setLogType(3);
        sysLog.setBusiness(api.getName());
        sysLog.setRequestUrl(api.getUrl());
        sysLog.setRequestContent(gson.toJson(params));
        sysLog.setResponseContent(response.getContent());
        if (StrUtil.isEmpty(sysLog.getResponseContent())) {
            sysLog.setResponseContent("HTTP状态码:" + response.getStatusCode());
        }
        sysLog.setStartTime(response.getRequestTime());
        sysLog.setEndTime(response.getResponseTime());
        sysLog.setExecutionTime(response.getTimeAsMillisecond());

        sysLog.setCreateTime(new Date());
        sysLog.setCreateBy(LoginUserUtils.getUsername());

        // 保存记录
        //sysLogService.save(sysLog);
        DbChain.create(SysLog.class).save(sysLog);
    }

}
