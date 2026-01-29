package com.moyu.boot.plugin.bnboxMessage.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 站内消息视图对象
 *
 * @author moyusisi
 * @since 2026-01-13
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InboxMessageVO {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /**
     * 消息触达记录
     */
    private List<UserMessageVO> readList;
}