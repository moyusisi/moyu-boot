package com.moyu.boot.plugin.thirdPartyApi.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moyu.boot.common.core.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 三方集成接口表(third_party_api)实体对象
 *
 * @author moyusisi
 * @since 2026-08-06
 */
@Getter
@Setter
@TableName("third_party_api")
public class ThirdPartyApi extends BaseEntity {

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
    private Date requestTime;
    /**
     * 响应时间
     */
    private Date responseTime;
    /**
     * 备注
     */
    private String remark;

}
