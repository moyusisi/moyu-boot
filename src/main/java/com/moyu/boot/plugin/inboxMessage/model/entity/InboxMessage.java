package com.moyu.boot.plugin.inboxMessage.model.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("inbox_message")
public class InboxMessage {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
