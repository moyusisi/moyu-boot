package com.moyu.boot.common.core.enums;


/**
 * 响应码接口
 *
 * @author shisong
 * @since 2025-09-12
 */
public interface ResultCode {

    /**
     * 获取响应码
     */
    String getCode();

    /**
     * 获取相应描述信息
     */
    String getMessage();
}
