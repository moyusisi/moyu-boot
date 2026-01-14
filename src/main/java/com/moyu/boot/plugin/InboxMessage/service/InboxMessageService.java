package com.moyu.boot.plugin.InboxMessage.service;

import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.InboxMessage.model.entity.InboxMessage;
import com.moyu.boot.plugin.InboxMessage.model.param.InboxMessageParam;
import com.moyu.boot.plugin.InboxMessage.model.vo.InboxMessageVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.plugin.InboxMessage.model.vo.UserMessageVO;

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
     *
     * @param param
     * @return
     */
    PageData<UserMessageVO> userMessagePage(InboxMessageParam param);
}
