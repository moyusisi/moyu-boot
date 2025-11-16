package com.moyu.boot.plugin.authSession.model.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 会话统计分析视图对象
 *
 * @author shisong
 * @since 2025-11-16
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthSessionAnalysisVO {

    /**
     * 当前会话总数量
     */
    private Integer sessionTotalCount;

    /**
     * 当前令牌总数量
     */
    private Integer tokenTotalCount;

    /**
     * 最大签发令牌数
     */
    private Integer maxTokenCount;

    /**
     * 今日签发令牌数
     */
    private Integer todayTokenCount;
}
