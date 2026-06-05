package com.moyu.boot.common.core.enums;

import com.moyu.boot.common.core.exception.BusinessException;
import lombok.Getter;

/**
 * 数据权限枚举
 *
 * @author shisong
 * @since 2024-12-25
 */
@Getter
public enum SortOrderEnum {

    /**
     * 升序
     */
    ASC("asc"),

    /**
     * 降序
     */
    DESC("desc");

    private final String value;

    SortOrderEnum(String value) {
        this.value = value.toLowerCase();
    }

    public static void validate(String value) {
        boolean valid = ASC.getValue().toLowerCase().equals(value) || DESC.getValue().toLowerCase().equals(value);
        if (!valid) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "不支持该排序方式：" + value);
        }
    }
}
