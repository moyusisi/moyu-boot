package com.moyu.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织机构表
 *
 * @TableName sys_org
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_org")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysOrg extends BaseEntity {
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

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