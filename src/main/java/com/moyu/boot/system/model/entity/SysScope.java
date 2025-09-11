package com.moyu.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据权限分组表
 *
 * @TableName sys_scope
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_scope")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysScope extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 直属组织编码
     */
    private String orgCode;

    /**
     * 直属组织名称
     */
    private String orgName;

    /**
     * 数据权限(字典 0无限制 1本人数据 2本机构 3本机构及以下 4自定义)
     */
    private Integer scopeType;

    /**
     * 自定义scope集合,逗号分隔
     */
    private String scopeSet;

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