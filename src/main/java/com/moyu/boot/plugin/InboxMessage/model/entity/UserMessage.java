package com.moyu.boot.plugin.InboxMessage.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
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
@TableName("user_message")
public class UserMessage extends BaseEntity {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 接收时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
