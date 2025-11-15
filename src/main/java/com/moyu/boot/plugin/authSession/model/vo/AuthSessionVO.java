package com.moyu.boot.plugin.authSession.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * 会话视图对象
 *
 * @author shisong
 * @since 2025-11-15
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthSessionVO {

    /**
     * 账号
     */
    private String account;

    /**
     * 姓名
     */
    private String name;

    /**
     * 上次登录ip
     */
    private String lastLoginIp;

    /**
     * 上次登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /**
     * 上次登录设备
     */
    private String lastLoginDevice;

    /**
     * 最新登录ip
     */
    private String latestLoginIp;

    /**
     * 最新登录时间
     */
    private Date latestLoginTime;

    /**
     * 最新登录设备
     */
    private String latestLoginDevice;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 会话创建时间
     */
    private Date sessionCreateTime;

    /**
     * 会话剩余有效期
     */
    private String sessionTimeout;

    /**
     * 令牌数量
     */
    private Integer tokenCount;

}
