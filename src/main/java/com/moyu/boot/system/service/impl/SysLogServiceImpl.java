package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.enums.SortOrderEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.mapper.SysLogMapper;
import com.moyu.boot.system.model.entity.SysLog;
import com.moyu.boot.system.model.param.SysLogParam;
import com.moyu.boot.system.model.vo.SysLogVO;
import com.moyu.boot.system.service.SysLogService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
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
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定id查询
        queryWrapper.eq(SysLog::getId, param.getId(), ObjectUtil.isNotEmpty(param.getId()));
        // 指定logType查询
        queryWrapper.eq(SysLog::getLogType, param.getLogType(), ObjectUtil.isNotEmpty(param.getLogType()));
        // 指定name查询
        queryWrapper.like(SysLog::getName, param.getName(), ObjectUtil.isNotEmpty(param.getName()));
        // 指定module查询
        queryWrapper.like(SysLog::getModule, param.getModule(), ObjectUtil.isNotEmpty(param.getModule()));
        // 指定business查询
        queryWrapper.like(SysLog::getBusiness, param.getBusiness(), ObjectUtil.isNotEmpty(param.getBusiness()));
        // 指定operate查询
        queryWrapper.like(SysLog::getOperate, param.getOperate(), ObjectUtil.isNotEmpty(param.getOperate()));
        // 指定content查询
        queryWrapper.like(SysLog::getContent, param.getContent(), ObjectUtil.isNotEmpty(param.getContent()));
        // 指定requestUrl查询
        queryWrapper.like(SysLog::getRequestUrl, param.getRequestUrl(), ObjectUtil.isNotEmpty(param.getRequestUrl()));
        // 指定requestContent查询
        queryWrapper.like(SysLog::getRequestContent, param.getRequestContent(), ObjectUtil.isNotEmpty(param.getRequestContent()));
        // 指定responseContent查询
        queryWrapper.like(SysLog::getResponseContent, param.getResponseContent(), ObjectUtil.isNotEmpty(param.getResponseContent()));
        // 指定createBy查询
        if (ObjectUtil.equal(param.getLogType(), 1)) {
            // 登陆日志查操作人
            queryWrapper.and(qw -> qw.where(SysLog::getCreateBy).like(param.getCreateBy()).or(SysLog::getRequestContent).like(param.getCreateBy()),
                    ObjectUtil.isNotEmpty(param.getCreateBy()));
        } else {
            queryWrapper.eq(SysLog::getCreateBy, param.getCreateBy(), ObjectUtil.isNotEmpty(param.getCreateBy()));
        }
        // 指定startTime范围查询
        Date start = param.getStartTime1();
        // 如果是日期，则end应为当日的结尾
        // end = DateUtil.endOfDay(end);
        Date end = param.getStartTime2();
        // 范围查询-起始
        queryWrapper.ge(SysLog::getStartTime, start, ObjectUtil.isNotEmpty(start));
        // 范围查询-截止
        queryWrapper.le(SysLog::getStartTime, end, ObjectUtil.isNotEmpty(end));
        // 仅查询未删除的
        queryWrapper.eq(SysLog::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(SysLog::getStartTime, false);
        }
        // 查询
        List<SysLog> sysLogList = this.list(queryWrapper);
        // 转换为voList
        List<SysLogVO> voList = buildSysLogVOList(sysLogList);
        return voList;
    }

    @Override
    public PageData<SysLogVO> pageList(SysLogParam param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定id查询
        queryWrapper.eq(SysLog::getId, param.getId(), ObjectUtil.isNotEmpty(param.getId()));
        // 指定logType查询
        queryWrapper.eq(SysLog::getLogType, param.getLogType(), ObjectUtil.isNotEmpty(param.getLogType()));
        // 指定name查询
        queryWrapper.like(SysLog::getName, param.getName(), ObjectUtil.isNotEmpty(param.getName()));
        // 指定module查询
        queryWrapper.like(SysLog::getModule, param.getModule(), ObjectUtil.isNotEmpty(param.getModule()));
        // 指定business查询
        queryWrapper.like(SysLog::getBusiness, param.getBusiness(), ObjectUtil.isNotEmpty(param.getBusiness()));
        // 指定operate查询
        queryWrapper.like(SysLog::getOperate, param.getOperate(), ObjectUtil.isNotEmpty(param.getOperate()));
        // 指定content查询
        queryWrapper.like(SysLog::getContent, param.getContent(), ObjectUtil.isNotEmpty(param.getContent()));
        // 指定requestUrl查询
        queryWrapper.like(SysLog::getRequestUrl, param.getRequestUrl(), ObjectUtil.isNotEmpty(param.getRequestUrl()));
        // 指定requestContent查询
        queryWrapper.like(SysLog::getRequestContent, param.getRequestContent(), ObjectUtil.isNotEmpty(param.getRequestContent()));
        // 指定responseContent查询
        queryWrapper.like(SysLog::getResponseContent, param.getResponseContent(), ObjectUtil.isNotEmpty(param.getResponseContent()));
        // 指定createBy查询
        if (ObjectUtil.equal(param.getLogType(), 1)) {
            // 登陆日志查操作人
            queryWrapper.and(qw -> qw.where(SysLog::getCreateBy).like(param.getCreateBy()).or(SysLog::getRequestContent).like(param.getCreateBy()),
                    ObjectUtil.isNotEmpty(param.getCreateBy()));
        } else {
            queryWrapper.eq(SysLog::getCreateBy, param.getCreateBy(), ObjectUtil.isNotEmpty(param.getCreateBy()));
        }
        // 指定startTime范围查询
        Date start = param.getStartTime1();
        // 如果是日期，则end应为当日的结尾
        // end = DateUtil.endOfDay(end);
        Date end = param.getStartTime2();
        // 范围查询-起始
        queryWrapper.ge(SysLog::getStartTime, start, ObjectUtil.isNotEmpty(start));
        // 范围查询-截止
        queryWrapper.le(SysLog::getStartTime, end, ObjectUtil.isNotEmpty(end));
        // 仅查询未删除的
        queryWrapper.eq(SysLog::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(SysLog::getStartTime, false);
        }
        // 分页查询
        Page<SysLog> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<SysLog> sysLogPage = this.page(page, queryWrapper);
        List<SysLogVO> voList = buildSysLogVOList(sysLogPage.getRecords());
        return new PageData<>(sysLogPage.getTotalRow(), voList);
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
        UpdateChain.of(SysLog.class)
                .set(SysLog::getDeleted, 1)
                .where(SysLog::getId).in(idSet)
                .update();
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
            if (vo.getLogType().equals(1) && ObjectUtil.isEmpty(vo.getCreateBy())) {
                String account = JSONUtil.parseArray(entity.getRequestContent()).getJSONObject(0).getByPath("account", String.class);
                vo.setCreateBy(account);
            }
            voList.add(vo);
        }
        return voList;
    }
}
