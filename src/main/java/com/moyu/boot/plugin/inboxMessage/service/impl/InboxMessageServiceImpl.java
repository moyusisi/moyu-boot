package com.moyu.boot.plugin.inboxMessage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.moyu.boot.common.authZ.util.LoginUserUtils;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.enums.SortOrderEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.inboxMessage.mapper.InboxMessageMapper;
import com.moyu.boot.plugin.inboxMessage.model.entity.InboxMessage;
import com.moyu.boot.plugin.inboxMessage.model.entity.UserMessage;
import com.moyu.boot.plugin.inboxMessage.model.param.InboxMessageParam;
import com.moyu.boot.plugin.inboxMessage.model.vo.InboxMessageVO;
import com.moyu.boot.plugin.inboxMessage.model.vo.UserMessageVO;
import com.moyu.boot.plugin.inboxMessage.service.InboxMessageService;
import com.moyu.boot.plugin.inboxMessage.service.UserMessageService;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.service.SysUserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 站内消息服务实现类
 *
 * @author moyusisi
 * @since 2026-01-13
 */
@Slf4j
@Service
public class InboxMessageServiceImpl extends ServiceImpl<InboxMessageMapper, InboxMessage> implements InboxMessageService {

    @Resource
    private UserMessageService userMessageService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public List<InboxMessageVO> list(InboxMessageParam param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定code查询
        queryWrapper.eq(InboxMessage::getCode, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        // 指定title查询
        queryWrapper.like(InboxMessage::getTitle, param.getTitle(), ObjectUtil.isNotEmpty(param.getTitle()));
        // 指定content查询
        queryWrapper.like(InboxMessage::getContent, param.getContent(), ObjectUtil.isNotEmpty(param.getContent()));
        // 指定sendTime查询
        Date start = param.getSendTime1();
        Date end = param.getSendTime2();
        // 范围查询-起始
        queryWrapper.ge(InboxMessage::getSendTime, start, ObjectUtil.isNotEmpty(start));
        // 范围查询-截止
        queryWrapper.le(InboxMessage::getSendTime, end, ObjectUtil.isNotEmpty(end));
        // 仅查询未删除的
        queryWrapper.eq(InboxMessage::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(InboxMessage::getSendTime, false);
        }
        // 查询
        List<InboxMessageVO> voList = this.listAs(queryWrapper, InboxMessageVO.class);
        return voList;
    }

    @Override
    public PageData<InboxMessageVO> pageList(InboxMessageParam param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定code查询
        queryWrapper.eq(InboxMessage::getCode, param.getCode(), ObjectUtil.isNotEmpty(param.getCode()));
        // 指定title查询
        queryWrapper.like(InboxMessage::getTitle, param.getTitle(), ObjectUtil.isNotEmpty(param.getTitle()));
        // 指定content查询
        queryWrapper.like(InboxMessage::getContent, param.getContent(), ObjectUtil.isNotEmpty(param.getContent()));
        // 指定sendTime查询
        Date start = param.getSendTime1();
        Date end = param.getSendTime2();
        // 范围查询-起始
        queryWrapper.ge(InboxMessage::getSendTime, start, ObjectUtil.isNotEmpty(start));
        // 范围查询-截止
        queryWrapper.le(InboxMessage::getSendTime, end, ObjectUtil.isNotEmpty(end));
        // 仅查询未删除的
        queryWrapper.eq(InboxMessage::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(InboxMessage::getSendTime, false);
        }
        // 分页查询
        Page<InboxMessageVO> page = Page.of(param.getPageNum(), param.getPageSize());
        Page<InboxMessageVO> voPage = this.pageAs(page, queryWrapper, InboxMessageVO.class);
        return new PageData<>(voPage.getTotalRow(), voPage.getRecords());
    }

    @Override
    public InboxMessageVO detail(InboxMessageParam param) {
        // 查询
        InboxMessage inboxMessage = this.getById(param.getId());
        if (inboxMessage == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        InboxMessageVO vo = BeanUtil.copyProperties(inboxMessage, InboxMessageVO.class);
        return vo;
    }

    @Override
    public void add(InboxMessageParam param) {
        // 属性复制
        InboxMessage inboxMessage = BeanUtil.copyProperties(param, InboxMessage.class);
        inboxMessage.setCode("MSG" + IdUtil.getSnowflakeNextId());
        inboxMessage.setSendTime(new Date());
        inboxMessage.setSendBy(LoginUserUtils.getUsername());
        // 其他处理
        inboxMessage.setId(null);
        List<String> userList = param.getReceiveUserList();
        List<UserMessage> messageList = new ArrayList<>();
        Date now = new Date();
        for (String userId : userList) {
            UserMessage userMessage = new UserMessage();
            userMessage.setFromId(inboxMessage.getCode());
            userMessage.setUserId(userId);
            userMessage.setCreateTime(now);
            messageList.add(userMessage);
        }
        transactionTemplate.execute((transactionStatus) -> {
            this.save(inboxMessage);
            userMessageService.saveBatch(messageList);
            return null;
        });
    }

    @Override
    public void deleteByIds(InboxMessageParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 删除时先查再删
        Long count = this.count(QueryWrapper.create().in(InboxMessage::getId, idSet));
        // 查到的数量比对
        if (ObjectUtil.notEqual(idSet.size(), count)) {
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

    @Override
    public PageData<UserMessageVO> userMessagePage(InboxMessageParam param) {
        PageData<UserMessageVO> pageData = userMessageService.pageList(param);
        if (pageData.getTotal() == 0) {
            return pageData;
        }
        // 补充消息title
        Map<String, InboxMessage> messageMap = new HashMap<>();
        Set<String> messageSet = pageData.getRecords().stream().map(UserMessageVO::getFromId).collect(Collectors.toSet());
        this.list(QueryWrapper.create().select(InboxMessage::getCode, InboxMessage::getTitle)
                        .in(InboxMessage::getCode, messageSet))
                .forEach(e -> messageMap.put(e.getCode(), e));
        // 补充用户name
        Map<String, String> userMap = new HashMap<>();
        Set<String> userSet = pageData.getRecords().stream().map(UserMessageVO::getUserId).collect(Collectors.toSet());
        sysUserService.list(QueryWrapper.create().select(SysUser::getAccount, SysUser::getName)
                        .where(SysUser::getAccount).in(userSet))
                .forEach(e -> userMap.put(e.getAccount(), e.getName()));

        // 补充message和user信息
        pageData.getRecords().forEach(vo -> {
            vo.setName(userMap.get(vo.getUserId()));
            vo.setTitle(messageMap.get(vo.getFromId()).getTitle());
        });
        // 补充用户name
        return pageData;
    }

    @Override
    public InboxMessageVO read(InboxMessageParam param) {
        String userId = LoginUserUtils.getUsername();
        Assert.notEmpty(userId, "用户ID不能为空");
        // 查询消息
        InboxMessage inboxMessage = this.getOne(QueryWrapper.create().eq(InboxMessage::getCode, param.getCode()));
        if (inboxMessage == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        InboxMessageVO vo = BeanUtil.copyProperties(inboxMessage, InboxMessageVO.class);
        // 查询触达记录
        UserMessage userMessage = userMessageService.getOne(QueryWrapper.create()
                .eq(UserMessage::getFromId, param.getCode()).eq(UserMessage::getUserId, userId));
        // 用户消息未读则更新为已读
        if (userMessage != null && userMessage.getHasRead() != 1) {
            // 属性复制
            UserMessage toUpdate = new UserMessage();
            toUpdate.setId(userMessage.getId());
            toUpdate.setHasRead(1);
            toUpdate.setReadTime(new Date());
            userMessageService.updateById(toUpdate);
        }
        return vo;
    }

    @Override
    public Long unreadCount(InboxMessageParam param) {
        String userId = LoginUserUtils.getUsername();
        Assert.notEmpty(userId, "用户ID不能为空");
        Long count = userMessageService.count(QueryWrapper.create()
                .eq(UserMessage::getUserId, userId)
                .eq(UserMessage::getHasRead, 0)
                .eq(UserMessage::getDeleted, 0)
        );
        return count;
    }

    @Override
    public PageData<UserMessageVO> userReadPage(InboxMessageParam param) {
        param.setUserId(LoginUserUtils.getUsername());
        Assert.notEmpty(param.getUserId(), "用户ID不能为空");
        PageData<UserMessageVO> pageData = userMessageService.pageList(param);
        if (pageData.getTotal() == 0) {
            return pageData;
        }
        // 补充消息title
        Map<String, InboxMessage> messageMap = new HashMap<>();
        Set<String> messageSet = pageData.getRecords().stream().map(UserMessageVO::getFromId).collect(Collectors.toSet());
        this.list(QueryWrapper.create().select(InboxMessage::getCode, InboxMessage::getTitle)
                        .in(InboxMessage::getCode, messageSet))
                .forEach(e -> messageMap.put(e.getCode(), e));
        // 补充title
        pageData.getRecords().forEach(vo -> {
            vo.setTitle(messageMap.get(vo.getFromId()).getTitle());
        });
        return pageData;
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<InboxMessageVO> buildDevMessageVOList(List<InboxMessage> entityList) {
        List<InboxMessageVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (InboxMessage entity : entityList) {
            InboxMessageVO vo = BeanUtil.copyProperties(entity, InboxMessageVO.class);
            voList.add(vo);
        }
        return voList;
    }
}
