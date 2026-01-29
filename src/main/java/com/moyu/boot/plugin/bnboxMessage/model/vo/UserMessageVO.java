package com.moyu.boot.plugin.bnboxMessage.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 站内信接收视图对象
 *
 * @author moyusisi
 * @since 2026-01-14
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMessageVO {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 来源对象id
     */
    private String fromId;
    /**
     * 消息标题
     */
    private String title;
    /**
     * 用户唯一id
     */
    private String userId;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 是否已读
     */
    private Integer hasRead;
    /**
     * 已读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;
    /**
     * 接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}