package com.moyu.boot.plugin.syslog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.syslog.mapper.SysLogMapper;
import com.moyu.boot.plugin.syslog.model.entity.SysLog;
import com.moyu.boot.plugin.syslog.model.param.SysLogParam;
import com.moyu.boot.plugin.syslog.model.vo.SysLogVO;
import com.moyu.boot.plugin.syslog.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * 系统日志服务实现类
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Slf4j
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Override
    public List<SysLogVO> list(SysLogParam param) {
        // 查询条件
        LambdaQueryWrapper<SysLog> queryWrapper = Wrappers.lambdaQuery(SysLog.class);
        // 指定requestUrl查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getRequestUrl()), SysLog::getRequestUrl, param.getRequestUrl());
        // 指定module查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getModule()), SysLog::getModule, param.getModule());
        // 指定business查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getBusiness()), SysLog::getBusiness, param.getBusiness());
        // 指定operate查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getOperate()), SysLog::getOperate, param.getOperate());
        // 指定content查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getContent()), SysLog::getContent, param.getContent());
        // 指定requestContent查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getRequestContent()), SysLog::getRequestContent, param.getRequestContent());
        // 指定responseContent查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getResponseContent()), SysLog::getResponseContent, param.getResponseContent());
        // 指定createBy查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCreateBy()), SysLog::getCreateBy, param.getCreateBy());
        // 指定startTime范围查询
        Date start = param.getStartTime1();
        // 如果是日期，则end应为当日的结尾
        // end = DateUtil.endOfDay(end);
        Date end = param.getStartTime2();
        // 范围查询-起始
        queryWrapper.ge(ObjectUtil.isNotEmpty(start), SysLog::getStartTime, start);
        // 范围查询-截止
        queryWrapper.le(ObjectUtil.isNotEmpty(end), SysLog::getStartTime, end);
        // 仅查询未删除的
        queryWrapper.eq(SysLog::getDeleted, 0);
        queryWrapper.orderByDesc(SysLog::getStartTime);
        // 查询
        List<SysLog> sysLogList = this.list(queryWrapper);
        // 转换为voList
        List<SysLogVO> voList = buildSysLogVOList(sysLogList);
        return voList;
    }

    @Override
    public PageData<SysLogVO> pageList(SysLogParam param) {
        // 查询条件
        LambdaQueryWrapper<SysLog> queryWrapper = Wrappers.lambdaQuery(SysLog.class);
        // 指定module查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getModule()), SysLog::getModule, param.getModule());
        // 指定business查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getBusiness()), SysLog::getBusiness, param.getBusiness());
        // 指定operate查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getOperate()), SysLog::getOperate, param.getOperate());
        // 指定content查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getContent()), SysLog::getContent, param.getContent());
        // 指定requestUrl查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getRequestUrl()), SysLog::getRequestUrl, param.getRequestUrl());
        // 指定requestContent查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getRequestContent()), SysLog::getRequestContent, param.getRequestContent());
        // 指定responseContent查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getResponseContent()), SysLog::getResponseContent, param.getResponseContent());
        // 指定createBy查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCreateBy()), SysLog::getCreateBy, param.getCreateBy());
        // 指定startTime范围查询
        Date start = param.getStartTime1();
        // 如果是日期，则end应为当日的结尾
        // end = DateUtil.endOfDay(end);
        Date end = param.getStartTime2();
        // 范围查询-起始
        queryWrapper.ge(ObjectUtil.isNotEmpty(start), SysLog::getStartTime, start);
        // 范围查询-截止
        queryWrapper.le(ObjectUtil.isNotEmpty(end), SysLog::getStartTime, end);
        // 仅查询未删除的
        queryWrapper.eq(SysLog::getDeleted, 0);
        queryWrapper.orderByDesc(SysLog::getStartTime);
        // 分页查询
        Page<SysLog> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<SysLog> sysLogPage = this.page(page, queryWrapper);
        List<SysLogVO> voList = buildSysLogVOList(sysLogPage.getRecords());
        return new PageData<>(sysLogPage.getTotal(), voList);
    }

    @Override
    public SysLogVO detail(SysLogParam param) {
        // 查询
        SysLog sysLog = this.getById(param.getId());
        if (sysLog == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        SysLogVO vo = BeanUtil.copyProperties(sysLog, SysLogVO.class);
        return vo;
    }

    @Override
    public void add(SysLogParam param) {
        // 属性复制
        SysLog sysLog = BeanUtil.copyProperties(param, SysLog.class);
        // 其他处理
        sysLog.setId(null);
        this.save(sysLog);
    }

    @Override
    public void update(SysLogParam param) {
        // 通过主键id查询原有数据
        SysLog old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        SysLog toUpdate = BeanUtil.copyProperties(param, SysLog.class);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(SysLogParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(SysLog.class).in(SysLog::getId, idSet).set(SysLog::getDeleted, 1));
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<SysLogVO> buildSysLogVOList(List<SysLog> entityList) {
        List<SysLogVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (SysLog entity : entityList) {
            SysLogVO vo = BeanUtil.copyProperties(entity, SysLogVO.class);
            voList.add(vo);
        }
        return voList;
    }
}
