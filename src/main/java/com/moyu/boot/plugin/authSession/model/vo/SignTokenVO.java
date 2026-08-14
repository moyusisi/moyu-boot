package com.moyu.boot.plugin.authSession.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * 签发token视图对象
 *
 * @author shisong
 * @since 2026-07-15
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignTokenVO {

    /**
     * token值
     */
    private String tokenValue;

    /**
     * 登录设备
     */
    private String tokenDevice;

    /**
     * token剩余有效期(s)
     */
    private Long tokenTimeout;

    /**
     * token有效期截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    /**
     * token剩余有效期百分比
     */
    private Double tokenTimeoutPercent;

    /**
     * token闲置冻结有效期(s)
     */
    private Long activeTimeout;

    /**
     * token闲置冻有效期截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date activeTimeoutDeadline;

    /**
     * token闲置冻结剩余有效期百分比
     */
    private Double activeTimeoutPercent;

    /**
     * 最后活跃时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastActiveTime;

    /**
     * 令牌创建时间
     */
    private Date createTime;
}
