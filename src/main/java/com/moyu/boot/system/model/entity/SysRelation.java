package com.moyu.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色权限关系表
 *
 * @TableName sys_relation
 */
@Data
@TableName(value = "sys_relation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysRelation extends BaseEntity implements Serializable {
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 对象ID
     */
    private String objectId;

    /**
     * 目标ID
     */
    private String targetId;

    /**
     * 关系类型(字典 1:role_has_user,2:role_has_perm,3:group_has_user,4:group_has_role,5:scope_has_user)
     *
     * @see com.moyu.boot.system.enums.RelationTypeEnum
     */
    private Integer relationType;
}