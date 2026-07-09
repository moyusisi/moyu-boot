package com.moyu.boot.support.sysConfig.model.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.core.model.PageParam;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.util.Set;

/**
 * 系统配置请求参数(查询、修改)
 *
 * @author moyusisi
 * @since 2026-05-28
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysConfigParam extends PageParam {

    //********** 额外字段 **********//
    /**
     * 待删除的id集合
     */
    private Set<Long> ids;

    /**
     * 搜索关键词
     */
    private String searchKey;

    //********** db中存在的字段 **********//
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 配置项名称
     */
    @Size(max = 64, message = "configName长度不能超过64个字符")
    private String configName;
    /**
     * 配置key
     */
    @Size(max = 64, message = "configKey长度不能超过64个字符")
    private String configKey;
    /**
     * 配置value
     */
    @Size(max = 1024, message = "keyValue长度不能超过1024个字符")
    private String configValue;
    /**
     * 配置类型
     */
    @Size(max = 64, message = "configType长度不能超过64个字符")
    private String configType;
    /**
     * 使用状态（0正常 1停用）
     */
    private Integer status;
    /**
     * 扩展信息
     */
    @Size(max = 65535, message = "extJson长度不能超过65535个字符")
    private String extJson;
    /**
     * 备注
     */
    @Size(max = 65535, message = "remark长度不能超过65535个字符")
    private String remark;
}