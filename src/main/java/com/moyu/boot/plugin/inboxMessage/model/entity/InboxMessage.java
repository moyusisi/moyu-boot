package com.moyu.boot.plugin.inboxMessage.model.entity;

import com.moyu.boot.common.mybatis.handler.CustomInsertListener;
import com.moyu.boot.common.mybatis.handler.CustomUpdateListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 站内消息表(inbox_message)实体对象
 *
 * @author moyusisi
 * @since 2026-01-13
 */
@Getter
@Setter
@Table(value = "inbox_message", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
public class InboxMessage {

    /**
     * 主键id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 唯一编码
     */
    private String code;
    /**
     * 消息类型（0正常 1停用）
     */
    private Integer messageType;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 发送人
     */
    private String sendBy;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 删除标志（0未删除  1已删除）
     */
    private Integer deleted;
}
