package com.moyu.boot.plugin.inboxMessage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.enums.SortOrderEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.inboxMessage.mapper.UserMessageMapper;
import com.moyu.boot.plugin.inboxMessage.model.entity.InboxMessage;
import com.moyu.boot.plugin.inboxMessage.model.entity.UserMessage;
import com.moyu.boot.plugin.inboxMessage.model.param.InboxMessageParam;
import com.moyu.boot.plugin.inboxMessage.model.vo.UserMessageVO;
import com.moyu.boot.plugin.inboxMessage.service.UserMessageService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 站内信接收服务实现类
 *
 * @author moyusisi
 * @since 2026-01-14
 */
@Slf4j
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

    @Override
    public List<UserMessageVO> list(InboxMessageParam param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定fromId查询
        queryWrapper.eq(UserMessage::getFromId, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        // 指定userId查询
        queryWrapper.eq(UserMessage::getUserId, param.getUserId(), ObjectUtil.isNotEmpty(param.getUserId()));
        // 指定hasRead查询
        queryWrapper.eq(UserMessage::getHasRead, param.getHasRead(), ObjectUtil.isNotEmpty(param.getHasRead()));
        // 仅查询未删除的
        queryWrapper.eq(UserMessage::getDeleted, 0);
        // 指定排序，按接收时间排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(UserMessage::getCreateTime, false);
        }
        // 限制时间范围一年内
        DateTime oneYear = DateTime.now().minusYears(1).withTimeAtStartOfDay();
        queryWrapper.ge(UserMessage::getCreateTime, oneYear.toDate());
        // 查询
        List<UserMessageVO> voList = this.listAs(queryWrapper, UserMessageVO.class);
        return voList;
    }

    @Override
    public PageData<UserMessageVO> pageList(InboxMessageParam param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定fromId查询
        queryWrapper.eq(UserMessage::getFromId, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        // 指定userId查询
        queryWrapper.eq(UserMessage::getUserId, param.getUserId(), ObjectUtil.isNotEmpty(param.getUserId()));
        // 指定hasRead查询
        queryWrapper.eq(UserMessage::getHasRead, param.getHasRead(), ObjectUtil.isNotEmpty(param.getHasRead()));
        // 仅查询未删除的
        queryWrapper.eq(UserMessage::getDeleted, 0);
        // 指定排序，按接收时间排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(UserMessage::getCreateTime, false);
        }
        // 分页查询
        Page<UserMessageVO> page = Page.of(param.getPageNum(), param.getPageSize());
        Page<UserMessageVO> voPage = this.pageAs(page, queryWrapper, UserMessageVO.class);
        return new PageData<>(voPage.getTotalRow(), voPage.getRecords());
    }

    @Override
    public UserMessageVO detail(InboxMessageParam param) {
        // 查询
        UserMessage userMessage = this.getById(param.getId());
        if (userMessage == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        UserMessageVO vo = BeanUtil.copyProperties(userMessage, UserMessageVO.class);
        return vo;
    }

    @Override
    public void add(InboxMessageParam param) {
        // 属性复制
        UserMessage userMessage = BeanUtil.copyProperties(param, UserMessage.class);
        // 其他处理
        userMessage.setId(null);
        this.save(userMessage);
    }

    @Override
    public void update(InboxMessageParam param) {
        // 通过主键id查询原有数据
        UserMessage old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        UserMessage toUpdate = BeanUtil.copyProperties(param, UserMessage.class);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(InboxMessageParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 删除时先查再删
        long count = this.count(QueryWrapper.create().in(InboxMessage::getId, idSet));
        // 查到的数量比对
        if (idSet.size() != count) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        UpdateChain.of(InboxMessage.class)
                .set(InboxMessage::getDeleted, 1)
                .where(InboxMessage::getId).in(idSet)
                .update();
    }

}
