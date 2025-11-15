package com.moyu.boot.plugin.codeGen.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 表单类型枚举
 *
 * @author shisong
 * @since 2025-09-17
 */
@Getter
public enum FormTypeEnum {

    /**
     * 输入框
     */
    INPUT(1, "输入框"),

    /**
     * 下拉框
     */
    SELECT(2, "下拉框"),

    /**
     * 数字输入框
     */
    INPUT_NUMBER(3, "数字输入框"),

    /**
     * 单选框
     */
    RADIO(4, "单选框"),

    /**
     * 复选框
     */
    CHECK_BOX(5, "复选框"),

    /**
     * 文本域
     */
    TEXT_AREA(6, "文本域"),

    /**
     * 日期时间框
     */
    DATE(7, "日期框"),

    /**
     * 日期框
     */
    DATE_TIME(8, "日期时间框"),

    /**
     * 隐藏域
     */
    HIDDEN(9, "隐藏域");

    @JsonValue
    private final Integer code;

    private final String desc;

    FormTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据枚举的code值获取枚举对象
     */
    public static FormTypeEnum getByCode(Integer code) {
        return Arrays.stream(FormTypeEnum.values()).filter(e -> Objects.equals(e.getCode(), code)).findFirst()
                // 取值失败返回默认值
                .orElse(INPUT);
    }

}