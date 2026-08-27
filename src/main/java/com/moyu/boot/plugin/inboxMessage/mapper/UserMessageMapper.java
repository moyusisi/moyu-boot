package com.moyu.boot.plugin.inboxMessage.mapper;

import com.moyu.boot.plugin.inboxMessage.model.entity.UserMessage;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表user_message(站内信接收表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2026-01-14
 */
@Mapper
public interface UserMessageMapper extends BaseMapper<UserMessage> {

}

