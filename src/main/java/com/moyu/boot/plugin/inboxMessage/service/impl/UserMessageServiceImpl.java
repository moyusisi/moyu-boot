package com.moyu.boot.plugin.inboxMessage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.inboxMessage.mapper.UserMessageMapper;
import com.moyu.boot.plugin.inboxMessage.model.entity.UserMessage;
import com.moyu.boot.plugin.inboxMessage.model.param.InboxMessageParam;
import com.moyu.boot.plugin.inboxMessage.model.vo.UserMessageVO;
import com.moyu.boot.plugin.inboxMessage.service.UserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
        LambdaQueryWrapper<UserMessage> queryWrapper = Wrappers.lambdaQuery(UserMessage.class);
        // 指定fromId查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), UserMessage::getFromId, param.getCode());
        // 指定userId查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getUserId()), UserMessage::getUserId, param.getUserId());
        // 指定hasRead查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getHasRead()), UserMessage::getHasRead, param.getHasRead());
        // 仅查询未删除的
        queryWrapper.eq(UserMessage::getDeleted, 0);
        // 指定排序，按接收时间排序
        queryWrapper.orderByDesc(UserMessage::getCreateTime);
        // 限制时间范围一年内
//        DateTime oneYear = DateTime.now().minusYears(1).withTimeAtStartOfDay();
//        queryWrapper.ge(UserMessage::getCreateTime, oneYear.toDate());
        // 查询
        List<UserMessage> userMessageList = this.list(queryWrapper);
        // 转换为voList
        List<UserMessageVO> voList = buildUserMessageVOList(userMessageList);
        return voList;
    }

    @Override
    public PageData<UserMessageVO> pageList(InboxMessageParam param) {
        // 查询条件
        LambdaQueryWrapper<UserMessage> queryWrapper = Wrappers.lambdaQuery(UserMessage.class);
        // 指定fromId查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getCode()), UserMessage::getFromId, param.getCode());
        // 指定userId查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getUserId()), UserMessage::getUserId, param.getUserId());
        // 指定hasRead查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getHasRead()), UserMessage::getHasRead, param.getHasRead());
        // 仅查询未删除的
        queryWrapper.eq(UserMessage::getDeleted, 0);
        // 指定排序，按接收时间排序
        queryWrapper.orderByDesc(UserMessage::getCreateTime);
        // 分页查询
        Page<UserMessage> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<UserMessage> userMessagePage = this.page(page, queryWrapper);
        List<UserMessageVO> voList = buildUserMessageVOList(userMessagePage.getRecords());
        return new PageData<>(userMessagePage.getTotal(), voList);
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
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(UserMessage.class).in(UserMessage::getId, idSet).set(UserMessage::getDeleted, 1));
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<UserMessageVO> buildUserMessageVOList(List<UserMessage> entityList) {
        List<UserMessageVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (UserMessage entity : entityList) {
            UserMessageVO vo = BeanUtil.copyProperties(entity, UserMessageVO.class);
            voList.add(vo);
        }
        return voList;
    }
}
