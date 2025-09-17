package com.moyu.boot.plugin.codegen.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 查询方式枚举
 *
 * @author shisong
 * @since 2025-09-17
 */
@Getter
public enum QueryTypeEnum {

    /**
     * 等于
     */
    EQ(1, "="),

    /**
     * 模糊匹配
     */
    LIKE(2, "LIKE '%s%'"),

    /**
     * 包含
     */
    IN(3, "IN"),

    /**
     * 范围
     */
    BETWEEN(4, "BETWEEN"),

    /**
     * 大于
     */
    GT(5, ">"),

    /**
     * 大于等于
     */
    GE(6, ">="),

    /**
     * 小于
     */
    LT(7, "<"),

    /**
     * 小于等于
     */
    LE(8, "<="),

    /**
     * 不等于
     */
    NE(9, "!="),

    /**
     * 左模糊匹配
     */
    LIKE_LEFT(10, "LIKE '%s'"),

    /**
     * 右模糊匹配
     */
    LIKE_RIGHT(11, "LIKE 's%'");

    @JsonValue
    private final Integer code;

    private final String desc;

    QueryTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据枚举的code值获取枚举对象
     */
    public static QueryTypeEnum getByCode(Integer code) {
        return Arrays.stream(QueryTypeEnum.values()).filter(e -> Objects.equals(e.getCode(), code)).findFirst()
                // 取值失败返回默认值
                .orElse(EQ);
    }

}