package com.moyu.boot.plugin.inboxMessage.model.entity;

import com.moyu.boot.common.mybatis.handler.CustomInsertListener;
import com.moyu.boot.common.mybatis.handler.CustomUpdateListener;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 站内信接收表(user_message)实体对象
 *
 * @author moyusisi
 * @since 2026-01-14
 */
@Getter
@Setter
@Table(value = "user_message", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
public class UserMessage {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 来源对象id
     */
    private String fromId;
    /**
     * 用户唯一id
     */
    private String userId;
    /**
     * 是否已读
     */
    private Integer hasRead;
    /**
     * 已读时间
     */
    private Date readTime;

    /**
     * 删除标志（0未删除  1已删除）
     */
    private Integer deleted;

    /**
     * 接收时间
     */
    private Date createTime;
}
