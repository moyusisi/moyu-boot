package com.moyu.boot.plugin.thirdPartyApi.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 三方集成接口视图对象
 *
 * @author moyusisi
 * @since 2026-08-06
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThirdPartyApiVO {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 三方接口唯一标识
     */
    private String code;
    /**
     * 三方接口名称
     */
    private String name;
    /**
     * 三方接口URL
     */
    private String url;
    /**
     * 请求方式 GET/POST/PUT等
     */
    private String requestMethod;
    /**
     * 三方系统名称
     */
    private String thirdAppName;
    /**
     * 调试状态（0未调试 1已调试）
     */
    private Integer debugStatus;
    /**
     * 请求头参数
     */
    private String requestHeader;
    /**
     * 请求体参数
     */
    private String requestBody;
    /**
     * 响应结果
     */
    private String responseBody;
    /**
     * HTTP状态码
     */
    private String statusCode;
    /**
     * 请求时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date requestTime;
    /**
     * 响应时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date responseTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 修改人
     */
    private String updateBy;
}