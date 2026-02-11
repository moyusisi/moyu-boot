package com.moyu.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 组织机构视图对象
 *
 * @author moyusisi
 * @since 2026-02-11
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysOrgVO {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
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
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 修改人
     */
    private String updateBy;
}