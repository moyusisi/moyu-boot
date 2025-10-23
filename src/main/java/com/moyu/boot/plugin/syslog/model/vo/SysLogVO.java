package com.moyu.boot.plugin.syslog.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 系统日志视图对象
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysLogVO {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 系统/模块
     */
    private String module;
    /**
     * 业务
     */
    private String business;
    /**
     * 操作
     */
    private String operate;
    /**
     * 内容说明
     */
    private String content;
    /**
     * 请求路径地址
     */
    private String requestUrl;
    /**
     * 请求参数
     */
    private String requestContent;
    /**
     * 返回结果
     */
    private String responseContent;
    /**
     * 开始时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 执行耗时(ms)
     */
    private Long executionTime;
    /**
     * 操作人ID
     */
    private String createBy;
    /**
     * 记录时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}