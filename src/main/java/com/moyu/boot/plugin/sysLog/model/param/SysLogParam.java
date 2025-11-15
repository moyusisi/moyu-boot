package com.moyu.boot.plugin.sysLog.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.core.model.BasePageParam;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.Set;

/**
 * 系统日志请求参数(查询、修改)
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysLogParam extends BasePageParam {

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
    * 系统/模块
    */
    @Size(max=50, message="module长度不能超过50个字符")
    private String module;
    /**
    * 业务
    */
    @Size(max=50, message="business长度不能超过50个字符")
    private String business;
    /**
    * 操作
    */
    @Size(max=50, message="operate长度不能超过50个字符")
    private String operate;
    /**
    * 内容说明
    */
    @Size(max=255, message="content长度不能超过255个字符")
    private String content;
    /**
     * 请求路径地址
     */
    @Size(max=255, message="requestUrl长度不能超过255个字符")
    private String requestUrl;
    /**
    * 请求参数
    */
    @Size(max=65535, message="requestContent长度不能超过65535个字符")
    private String requestContent;
    /**
    * 返回结果
    */
    @Size(max=65535, message="responseContent长度不能超过65535个字符")
    private String responseContent;
    /**
    * 开始时间
    */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 开始时间-起始
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime1;
    /**
     * 开始时间-截止
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime2;
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
}