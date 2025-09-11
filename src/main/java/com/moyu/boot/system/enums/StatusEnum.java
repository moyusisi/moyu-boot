package com.moyu.boot.system.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 使用状态枚举，实体中status字段对应的取值范围
 *
 * @author shisong
 * @since 2024-12-11
 */
@Getter
public enum StatusEnum {

    /**
     * 使用状态(字典 0正常 1停用)
     */
    ENABLE(0, "正常"),
    DISABLE(1, "停用");

    //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    @EnumValue
    private final Integer code;

    private final String desc;

    StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据枚举的code值获取枚举对象
     */
    public static StatusEnum getByCode(Integer code) {
        return Arrays.stream(StatusEnum.values()).filter(e -> Objects.equals(e.getCode(), code)).findFirst().orElse(null);
    }
}
