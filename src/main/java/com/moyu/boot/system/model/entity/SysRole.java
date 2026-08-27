package com.moyu.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.mybatis.handler.CustomInsertListener;
import com.moyu.boot.common.mybatis.handler.CustomUpdateListener;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色信息表(sys_role)实体对象
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Getter
@Setter
@TableName(value = "sys_role")
@Table(value = "sys_role", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
public class SysRole extends BaseEntity {

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 编码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 排序顺序
     */
    private Integer sortNum;

    /**
     * 使用状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 扩展信息
     */
    private String extJson;

    /**
     * 备注
     */
    private String remark;

}
