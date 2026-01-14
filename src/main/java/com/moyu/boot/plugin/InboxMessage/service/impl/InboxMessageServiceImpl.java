package com.moyu.boot.plugin.InboxMessage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.InboxMessage.mapper.InboxMessageMapper;
import com.moyu.boot.plugin.InboxMessage.model.entity.InboxMessage;
import com.moyu.boot.plugin.InboxMessage.model.param.InboxMessageParam;
import com.moyu.boot.plugin.InboxMessage.model.vo.InboxMessageVO;
import com.moyu.boot.plugin.InboxMessage.model.vo.UserMessageVO;
import com.moyu.boot.plugin.InboxMessage.service.InboxMessageService;
import com.moyu.boot.plugin.InboxMessage.service.UserMessageService;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    @Override
    public List<InboxMessageVO> list(InboxMessageParam param) {
        // 查询条件
        LambdaQueryWrapper<InboxMessage> queryWrapper = Wrappers.lambdaQuery(InboxMessage.class);
        // 指定title查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getTitle()), InboxMessage::getTitle, param.getTitle());
        // 指定content查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getContent()), InboxMessage::getContent, param.getContent());
        // 指定sendTime查询
        Date start = param.getSendTime1();
        Date end = param.getSendTime2();
        // 范围查询-起始
        queryWrapper.ge(ObjectUtil.isNotEmpty(start), InboxMessage::getSendTime, start);
        // 范围查询-截止
        queryWrapper.le(ObjectUtil.isNotEmpty(end), InboxMessage::getSendTime, end);
        // 仅查询未删除的
        queryWrapper.eq(InboxMessage::getDeleted, 0);
        // 指定排序
        queryWrapper.orderByDesc(InboxMessage::getSendTime);
        // 查询
        List<InboxMessage> inboxMessageList = this.list(queryWrapper);
        // 转换为voList
        List<InboxMessageVO> voList = buildDevMessageVOList(inboxMessageList);
        return voList;
    }

    @Override
    public PageData<InboxMessageVO> pageList(InboxMessageParam param) {
        // 查询条件
        LambdaQueryWrapper<InboxMessage> queryWrapper = Wrappers.lambdaQuery(InboxMessage.class);
        // 指定title查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getTitle()), InboxMessage::getTitle, param.getTitle());
        // 指定content查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getContent()), InboxMessage::getContent, param.getContent());
        // 指定sendTime查询
        Date start = param.getSendTime1();
        Date end = param.getSendTime2();
        // 范围查询-起始
        queryWrapper.ge(ObjectUtil.isNotEmpty(start), InboxMessage::getSendTime, start);
        // 范围查询-截止
        queryWrapper.le(ObjectUtil.isNotEmpty(end), InboxMessage::getSendTime, end);
        // 仅查询未删除的
        queryWrapper.eq(InboxMessage::getDeleted, 0);
        // 指定排序
        queryWrapper.orderByDesc(InboxMessage::getSendTime);
        // 分页查询
        Page<InboxMessage> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<InboxMessage> devMessagePage = this.page(page, queryWrapper);
        List<InboxMessageVO> voList = buildDevMessageVOList(devMessagePage.getRecords());
        return new PageData<>(devMessagePage.getTotal(), voList);
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
        // 其他处理
        inboxMessage.setId(null);
        this.save(inboxMessage);
    }

    @Override
    public InboxMessageVO read(InboxMessageParam param) {
        InboxMessageVO vo = detail(param);
/*        // 通过主键id查询原有数据 TODO
        DevMessage old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        DevMessage toUpdate = BeanUtil.copyProperties(param, DevMessage.class);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);*/
        return vo;
    }

    @Override
    public void deleteByIds(InboxMessageParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(InboxMessage.class).in(InboxMessage::getId, idSet).set(InboxMessage::getDeleted, 1));
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
        this.list(Wrappers.lambdaQuery(InboxMessage.class).select(InboxMessage::getCode, InboxMessage::getTitle)
                        .in(InboxMessage::getCode, messageSet))
                .forEach(e -> messageMap.put(e.getCode(), e));
        // 补充用户name
        Map<String, SysUser> userMap = new HashMap<>();
        Set<String> userSet = pageData.getRecords().stream().map(UserMessageVO::getUserId).collect(Collectors.toSet());
        sysUserService.list(Wrappers.lambdaQuery(SysUser.class).select(SysUser::getAccount, SysUser::getName)
                        .in(SysUser::getAccount, userSet))
                .forEach(e -> userMap.put(e.getAccount(), e));
        // 补充message和user信息
        pageData.getRecords().forEach(vo -> {
            vo.setName(userMap.get(vo.getUserId()).getName());
            vo.setTitle(messageMap.get(vo.getFromId()).getTitle());
        });
        // 补充用户name
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
