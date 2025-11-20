package com.moyu.boot.plugin.sysLog.enums;


import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 系统日志分类枚举
 *
 * @author shisong
 * @since 2024-12-25
 */
@Getter
public enum LogTypeEnum {

    /**
     * 日志类型(字典 0默认日志 1操作日志 2登录认证 3三方交互)
     */
    DEFAULT(0, "默认日志"),
    OPERATE(1, "操作日志"),
    LOGIN(2, "登录认证"),
    THIRD_PART(3, "三方交互");

    private final Integer code;

    private final String desc;

    LogTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据枚举的code值获取枚举对象
     */
    public static LogTypeEnum getByCode(Integer code) {
        return Arrays.stream(LogTypeEnum.values()).filter(e -> Objects.equals(e.getCode(), code)).findFirst().orElse(DEFAULT);
    }
}
