package com.moyu.boot.common.core.enums;

import com.moyu.boot.common.core.model.IResultCode;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * <p>响应码的枚举类, 不细分具体错误错误类型</p>
 * <p>可通过 {@code BaseException(ResultCodeEnum.INVALID_PARAMETER, "uid不能为空")} 这种方式对错误进行具体描述</p>
 *
 * @author song.shi
 * @since 2016-04-01
 */
public enum ResultCodeEnum implements IResultCode, Serializable {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 参数错误
     */
    INVALID_PARAMETER(-1, "参数错误"),
    /**
     * 业务异常
     */
    BUSINESS_ERROR(1, "业务异常"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(2, "系统异常"),

    /**
     * 自定义提示异常,描述信息可以直接展示给用户看
     */
    CUSTOM_ERROR(3, "自定义提示异常");

    private int code;

    private String message;

    ResultCodeEnum(int errorCode, String errorMessage) {
        this.code = errorCode;
        this.message = errorMessage;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResultCodeEnum.class.getSimpleName() + "[", "]")
                .add("code=" + code)
                .add("message='" + message + "'")
                .toString();
    }
}
