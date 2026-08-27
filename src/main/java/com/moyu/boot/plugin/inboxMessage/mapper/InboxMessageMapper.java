package com.moyu.boot.plugin.inboxMessage.mapper;

import com.moyu.boot.plugin.inboxMessage.model.entity.InboxMessage;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表inbox_message(站内消息表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2026-01-13
 */
@Mapper
public interface InboxMessageMapper extends BaseMapper<InboxMessage> {

}

