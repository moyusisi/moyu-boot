package com.moyu.boot.plugin.inboxMessage.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.core.model.PageParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 站内消息请求参数(查询、修改)
 *
 * @author moyusisi
 * @since 2026-01-13
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InboxMessageParam extends PageParam {

    //********** 额外字段 **********//
    /**
     * 待删除的id集合
     */
    private Set<Long> ids;

    /**
     * 搜索关键词
     */
    private String searchKey;

    //********** db中存在的字段 **********//
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 唯一编码
     */
    @Size(max = 64, message = "code长度不能超过64个字符")
    private String code;
    /**
     * 消息类型（0正常 1停用）
     */
    private Integer messageType;
    /**
     * 标题
     */
    @NotBlank(message = "title不能为空")
    @Size(max = 255, message = "title长度不能超过255个字符")
    private String title;
    /**
     * 内容
     */
    @NotBlank(message = "content不能为空")
    @Size(max = 65535, message = "content长度不能超过65535个字符")
    private String content;
    /**
     * 发送人
     */
    @Size(max = 32, message = "sendBy长度不能超过32个字符")
    private String sendBy;
    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
    /**
     * 发送时间-起始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime1;
    /**
     * 发送时间-截止
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime2;
    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    //***** 触达参数 *****//
    /**
     * 接收人列表
     */
    @NotEmpty(message = "接收人不能为空")
    private List<String> receiveUserList;
    /**
     * 用户唯一id（account）
     */
    private String userId;
    /**
     * 是否已读
     */
    private Integer hasRead;
}