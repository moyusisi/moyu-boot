package com.moyu.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 分组信息视图对象
 *
 * @author moyusisi
 * @since 2025-10-06
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysGroupVO {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 数据范围(字典 0无限制 1本人数据 2本机构 3本机构及以下 4自定义)
     */
    private Integer dataScope;
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
    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 修改人
     */
    private String updateBy;
}