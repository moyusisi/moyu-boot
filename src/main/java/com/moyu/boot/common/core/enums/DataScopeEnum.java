package com.moyu.boot.common.core.enums;


import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 数据权限枚举
 *
 * @author shisong
 * @since 2024-12-25
 */
@Getter
public enum DataScopeEnum {

    /**
     * 数据权限(字典 0无限制 1仅本人数据 2仅本机构 3本机构及以下 4自定义)
     */
    ALL(0, "所有数据"),
    SELF(1, "仅本人数据"),
    ORG(2, "仅本机构"),
    ORG_CHILD(3, "本机构及以下"),
    ORG_DEFINE(4, "自定义");

    private final Integer code;

    private final String desc;

    DataScopeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据枚举的code值获取枚举对象
     */
    public static DataScopeEnum getByCode(Integer code) {
        return Arrays.stream(DataScopeEnum.values()).filter(e -> Objects.equals(e.getCode(), code)).findFirst().orElse(ALL);
    }
}
