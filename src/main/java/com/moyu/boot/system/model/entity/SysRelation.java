package com.moyu.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户角色权限关系表
 *
 * @TableName sys_relation
 */
@Getter
@Setter
@TableName(value = "sys_relation")
public class SysRelation extends BaseEntity {
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