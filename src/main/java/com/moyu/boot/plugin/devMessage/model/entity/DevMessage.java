package com.moyu.boot.plugin.devMessage.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 站内消息表(dev_message)实体对象
 *
 * @author moyusisi
 * @since 2026-01-13
 */
@Getter
@Setter
@TableName("dev_message")
public class DevMessage {

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
    * 内容说明
    */
    private String content;
    /**
    * 扩展信息
    */
    private String extJson;
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
