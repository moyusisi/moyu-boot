package com.moyu.boot.system.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 性别
 *
 * @author shisong
 * @since 2024-12-25
 */
@Getter
public enum GenderEnum {

    /**
     * 组织机构类型(字典 1公司组织 2部门机构 3虚拟节点)
     */
    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    @EnumValue
    private final Integer code;

    private final String desc;

    GenderEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据枚举的code值获取枚举对象
     */
    public static GenderEnum getByCode(Integer code) {
        return Arrays.stream(GenderEnum.values()).filter(e -> Objects.equals(e.getCode(), code)).findFirst().orElse(UNKNOWN);
    }
}
