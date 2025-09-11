package com.moyu.boot.system.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 关系类型枚举，SysRelation实体中relationType字段对应的取值范围
 *
 * @author shisong
 * @since 2024-12-16
 */
@Getter
public enum RelationTypeEnum {

    /**
     * 关系类型(字典 1:role_has_user, 2:role_has_perm, 3:group_has_user, 4:group_has_role, 5:scope_has_user)
     */
    INVALID(0, null),
    ROLE_HAS_USER(1, "角色-用户关系"),
    ROLE_HAS_PERM(2, "角色-权限关系"),
    GROUP_HAS_USER(3, "分组-用户关系"),
    GROUP_HAS_ROLE(4, "分组-角色关系"),
    SCOPE_HAS_USER(5, "数据-用户关系");

    //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    @EnumValue
    private final Integer code;

    private final String desc;

    RelationTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据枚举的code值获取枚举对象
     */
    public static RelationTypeEnum getByCode(Integer code) {
        return Arrays.stream(RelationTypeEnum.values()).filter(e -> Objects.equals(e.getCode(), code)).findFirst().orElse(INVALID);
    }
}
