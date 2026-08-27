package com.moyu.boot.system.model.entity;

import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.mybatis.handler.CustomInsertListener;
import com.moyu.boot.common.mybatis.handler.CustomUpdateListener;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户角色权限关系表(sys_relation)实体对象
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Getter
@Setter
@Table(value = "sys_relation", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
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
     * 关系类型(字典 1:user_has_role, 2:role_has_perm, 3:user_has_group, 4:group_has_role, 5:scope_has_user)
     *
     * @see com.moyu.boot.system.enums.RelationTypeEnum
     */
    private Integer relationType;

    /**
     * 扩展信息
     *
     * @see com.moyu.boot.system.model.entity.ext.RelationExt
     */
    private String extJson;
}