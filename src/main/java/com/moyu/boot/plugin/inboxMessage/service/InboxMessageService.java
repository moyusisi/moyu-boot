package com.moyu.boot.plugin.inboxMessage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.inboxMessage.model.entity.InboxMessage;
import com.moyu.boot.plugin.inboxMessage.model.param.InboxMessageParam;
import com.moyu.boot.plugin.inboxMessage.model.vo.InboxMessageVO;
import com.moyu.boot.plugin.inboxMessage.model.vo.UserMessageVO;

import java.util.List;

/**
 * 站内消息服务类Service
 *
 * @author moyusisi
 * @since 2026-01-13
 */
public interface InboxMessageService extends IService<InboxMessage> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<InboxMessageVO> list(InboxMessageParam param);

    /**
     * 分页获取记录列表
     */
    PageData<InboxMessageVO> pageList(InboxMessageParam param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
    InboxMessageVO detail(InboxMessageParam param);

    /**
     * 添加记录
     */
    void add(InboxMessageParam param);

    /**
     * 阅读记录详情(通过主键更新)
     */
    InboxMessageVO read(InboxMessageParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(InboxMessageParam param);

    /**
     * 用户消息分页列表(无登录)
     */
    PageData<UserMessageVO> userMessagePage(InboxMessageParam param);

    /**
     * 用户是否有未读消息(无登录)
     */
    Long unreadCount(InboxMessageParam param);

    /**
     * 用户消息触达分页列表
     */
    PageData<UserMessageVO> userReadPage(InboxMessageParam param);
}
