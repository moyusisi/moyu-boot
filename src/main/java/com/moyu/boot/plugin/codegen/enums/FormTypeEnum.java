package com.moyu.boot.plugin.codegen.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
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
     * 单选框
     */
    RADIO(3, "单选框"),

    /**
     * 复选框
     */
    CHECK_BOX(4, "复选框"),

    /**
     * 数字输入框
     */
    INPUT_NUMBER(5, "数字输入框"),

    /**
     * 开关
     */
    SWITCH(6, "开关"),

    /**
     * 文本域
     */
    TEXT_AREA(7, "文本域"),

    /**
     * 日期时间框
     */
    DATE(8, "日期框"),

    /**
     * 日期框
     */
    DATE_TIME(9, "日期时间框"),

    /**
     * 隐藏域
     */
    HIDDEN(10, "隐藏域");

    //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    // @EnumValue
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