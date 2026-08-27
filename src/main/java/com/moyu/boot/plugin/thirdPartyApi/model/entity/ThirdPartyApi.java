package com.moyu.boot.plugin.thirdPartyApi.model.entity;

import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.mybatis.handler.CustomInsertListener;
import com.moyu.boot.common.mybatis.handler.CustomUpdateListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
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
@Table(value = "third_party_api", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
public class ThirdPartyApi extends BaseEntity {
    /**
     * 主键id
     */
    @Id(keyType = KeyType.Auto)
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
     * 扩展信息
     */
    private String extJson;
    /**
     * 备注
     */
    private String remark;

}
