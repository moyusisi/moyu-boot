package com.moyu.boot.plugin.ThirdPartyApp.service.impl;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.sign.error.SaSignErrorCode;
import cn.dev33.satoken.sign.exception.SaSignException;
import cn.dev33.satoken.sign.template.SaSignMany;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
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
import com.moyu.boot.plugin.ThirdPartyApp.mapper.ThirdPartyAppMapper;
import com.moyu.boot.plugin.ThirdPartyApp.model.entity.ThirdPartyApp;
import com.moyu.boot.plugin.ThirdPartyApp.model.param.ThirdPartyAppParam;
import com.moyu.boot.plugin.ThirdPartyApp.model.vo.ThirdPartyAppVO;
import com.moyu.boot.plugin.ThirdPartyApp.service.ThirdPartyAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 三方应用服务实现类
 *
 * @author moyusisi
 * @since 2026-08-17
 */
@Slf4j
@Service
public class ThirdPartyAppServiceImpl extends ServiceImpl<ThirdPartyAppMapper, ThirdPartyApp> implements ThirdPartyAppService {

    private static final String HEADER_PREFIX = "X-";

    @Override
    public List<ThirdPartyAppVO> list(ThirdPartyAppParam param) {
        // 查询条件
        QueryWrapper<ThirdPartyApp> queryWrapper = Wrappers.query(ThirdPartyApp.class).checkSqlInjection();
        // 指定appKey查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getAppKey()), ThirdPartyApp::getAppKey, param.getAppKey());
        // 指定appName查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getAppName()), ThirdPartyApp::getAppName, param.getAppName());
        // 指定digestAlgo查询
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getDigestAlgo()), ThirdPartyApp::getDigestAlgo, param.getDigestAlgo());
        // 仅查询未删除的
        queryWrapper.lambda().eq(ThirdPartyApp::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(SortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(ThirdPartyApp::getUpdateTime);
        }
        // 查询
        List<ThirdPartyApp> thirdPartyAppList = this.list(queryWrapper);
        // 转换为voList
        List<ThirdPartyAppVO> voList = buildThirdPartyAppVOList(thirdPartyAppList);
        return voList;
    }

    @Override
    public PageData<ThirdPartyAppVO> pageList(ThirdPartyAppParam param) {
        // 查询条件
        QueryWrapper<ThirdPartyApp> queryWrapper = Wrappers.query(ThirdPartyApp.class).checkSqlInjection();
        // 指定appKey查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getAppKey()), ThirdPartyApp::getAppKey, param.getAppKey());
        // 指定appName查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getAppName()), ThirdPartyApp::getAppName, param.getAppName());
        // 指定digestAlgo查询
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(param.getDigestAlgo()), ThirdPartyApp::getDigestAlgo, param.getDigestAlgo());
        // 仅查询未删除的
        queryWrapper.lambda().eq(ThirdPartyApp::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(SortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(ThirdPartyApp::getUpdateTime);
        }
        // 分页查询
        Page<ThirdPartyApp> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<ThirdPartyApp> thirdPartyAppPage = this.page(page, queryWrapper);
        List<ThirdPartyAppVO> voList = buildThirdPartyAppVOList(thirdPartyAppPage.getRecords());
        return new PageData<>(thirdPartyAppPage.getTotal(), voList);
    }

    @Override
    public ThirdPartyAppVO detail(ThirdPartyAppParam param) {
        // 查询
        ThirdPartyApp thirdPartyApp = this.getById(param.getId());
        if (thirdPartyApp == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        ThirdPartyAppVO vo = BeanUtil.copyProperties(thirdPartyApp, ThirdPartyAppVO.class);
        return vo;
    }

    @Override
    public void add(ThirdPartyAppParam param) {
        // 属性复制
        ThirdPartyApp thirdPartyApp = BeanUtil.copyProperties(param, ThirdPartyApp.class);
        // 其他处理
        thirdPartyApp.setId(null);
        if (StrUtil.isBlank(thirdPartyApp.getAppSecret())) {
            // 为空则生成24位SK
            thirdPartyApp.setAppSecret(IdUtil.objectId());
        }
        this.save(thirdPartyApp);
    }

    @Override
    public void update(ThirdPartyAppParam param) {
        // 通过主键id查询原有数据
        ThirdPartyApp old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        ThirdPartyApp toUpdate = BeanUtil.copyProperties(param, ThirdPartyApp.class, BaseEntity.UPDATE_TIME, BaseEntity.UPDATE_BY);
        // 其他处理
        toUpdate.setId(param.getId());
        if (StrUtil.isBlank(toUpdate.getAppSecret())) {
            // 为空则生成24位SK
            toUpdate.setAppSecret(IdUtil.objectId());
        }
        // 更新数据
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(ThirdPartyAppParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 删除时先查再删
        List<ThirdPartyApp> toDelList = this.listByIds(idSet);
        // 要删除的和查询到的进行比对
        if (ObjectUtil.notEqual(idSet.size(), toDelList.size())) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(ThirdPartyApp.class).in(ThirdPartyApp::getId, idSet).set(ThirdPartyApp::getDeleted, 1));
    }

    @Override
    public void checkSign(String... paramNames) {
        SaRequest request = SaHolder.getRequest();
        String appid = request.getHeader("X-Appid");
        SaSignException.notEmpty(appid, "应用标识appid不可为空", SaSignErrorCode.CODE_12201);

        // 验签的参数map
        Map<String, String> paramMap = new TreeMap<>();
        if (paramNames.length == 0) {
            paramMap.putAll(request.getParamMap());
        } else {
            // 获取指定的参数
            for (String paramName : paramNames) {
                paramMap.put(paramName, request.getParam(paramName));
            }
        }
        // 从Header中获取的参数
        String timestampValue = request.getHeader(SaSignTemplate.timestamp);
        String nonceValue = request.getHeader(SaSignTemplate.nonce);
        String signValue = request.getHeader(SaSignTemplate.sign);

        // 此三个参数是必须获取的
        paramMap.put(SaSignTemplate.timestamp, timestampValue);
        paramMap.put(SaSignTemplate.nonce, nonceValue);
        // 验签
        SaSignMany.getSignTemplate(appid).checkSign(paramMap, signValue);
        //SaSignMany.getSignTemplate(appid).checkParamMap(paramMap);
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<ThirdPartyAppVO> buildThirdPartyAppVOList(List<ThirdPartyApp> entityList) {
        List<ThirdPartyAppVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (ThirdPartyApp entity : entityList) {
            ThirdPartyAppVO vo = BeanUtil.copyProperties(entity, ThirdPartyAppVO.class);
            voList.add(vo);
        }
        return voList;
    }
}
