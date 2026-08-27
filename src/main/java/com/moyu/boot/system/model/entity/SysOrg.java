package com.moyu.boot.system.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.mybatis.handler.CustomInsertListener;
import com.moyu.boot.common.mybatis.handler.CustomUpdateListener;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织机构表(sys_org)实体对象
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_org", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysOrg extends BaseEntity {
    /**
     * 父编码
     */
    private String parentCode;
    /**
     * 名称
     */
    private String name;
    /**
     * 编码
     */
    private String code;
    /**
     * 组织机构类型(字典 1公司组织 2部门机构 3虚拟节点)
     *
     * @see com.moyu.boot.system.enums.OrgTypeEnum
     */
    private Integer orgType;
    /**
     * 组织层级(字典 1一级公司 2二级公司 3三级公司)
     */
    private Integer orgLevel;
    /**
     * 组织机构层级路径,逗号分隔,父节点在后
     */
    private String orgPath;
    /**
     * 排序顺序
     */
    private Integer sortNum;
    /**
     * 状态（0正常 1停用）
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