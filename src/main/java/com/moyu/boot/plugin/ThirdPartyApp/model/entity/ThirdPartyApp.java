package com.moyu.boot.plugin.ThirdPartyApp.model.entity;

import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.mybatis.handler.CustomInsertListener;
import com.moyu.boot.common.mybatis.handler.CustomUpdateListener;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 三方应用表(third_party_app)实体对象
 *
 * @author moyusisi
 * @since 2026-08-17
 */
@Getter
@Setter
@Table(value = "third_party_app", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
public class ThirdPartyApp extends BaseEntity {

    /**
     * 应用标识
     */
    private String appCode;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用密钥
     */
    private String appSecret;
    /**
     * 签名算法
     */
    private String digestAlgo;
    /**
     * 扩展信息
     */
    private String extJson;
    /**
     * 备注
     */
    private String remark;

}
