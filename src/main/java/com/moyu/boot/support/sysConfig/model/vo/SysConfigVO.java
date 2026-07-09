package com.moyu.boot.support.sysConfig.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 系统配置视图对象
 *
 * @author moyusisi
 * @since 2026-05-28
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysConfigVO {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 配置项名称
     */
    private String configName;
    /**
     * 配置key
     */
    private String configKey;
    /**
     * 配置value
     */
    private String configValue;
    /**
     * 配置类型
     */
    private String configType;
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