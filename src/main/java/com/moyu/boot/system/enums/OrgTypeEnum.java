package com.moyu.boot.system.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 组织机构类型，SysOrg实体中orgType字段对应的取值范围
 *
 * @author shisong
 * @since 2024-12-11
 */
@Getter
public enum OrgTypeEnum {

    /**
     * 组织机构类型(字典 1公司组织 2部门机构 3虚拟节点)
     */
    INVALID(0, null),
    COMPANY(1, "公司组织"),
    DEPT(2, "部门机构"),
    NODE(3, "虚拟节点");

    //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    @EnumValue
    private final Integer code;

    private final String desc;

    OrgTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据枚举的code值获取枚举对象
     */
    public static OrgTypeEnum getByCode(Integer code) {
        return Arrays.stream(OrgTypeEnum.values()).filter(e -> Objects.equals(e.getCode(), code)).findFirst().orElse(INVALID);
    }
}
