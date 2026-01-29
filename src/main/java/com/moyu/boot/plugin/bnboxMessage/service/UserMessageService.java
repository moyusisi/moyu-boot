package com.moyu.boot.plugin.bnboxMessage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.bnboxMessage.model.entity.UserMessage;
import com.moyu.boot.plugin.bnboxMessage.model.param.InboxMessageParam;
import com.moyu.boot.plugin.bnboxMessage.model.vo.UserMessageVO;

import java.util.List;

/**
 * 站内信接收服务类Service
 *
 * @author moyusisi
 * @since 2026-01-14
 */
public interface UserMessageService extends IService<UserMessage> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<UserMessageVO> list(InboxMessageParam param);

    /**
     * 分页获取记录列表
     */
    PageData<UserMessageVO> pageList(InboxMessageParam param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
    UserMessageVO detail(InboxMessageParam param);

    /**
     * 添加记录
     */
    void add(InboxMessageParam param);

    /**
     * 修改记录(通过主键id更新)
     */
    void update(InboxMessageParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(InboxMessageParam param);
}
