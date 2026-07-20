package com.moyu.boot.system.model.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.core.model.PageParam;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * 接口信息请求参数(查询、修改)
 *
 * @author moyusisi
 * @since 2026-07-20
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysApiParam extends PageParam {

    //********** 额外字段 **********//
    /**
     * 待删除的id集合
     */
    private Set<Long> ids;

    /**
     * 指定要查询的code集合
     */
    private Set<String> codeSet;

    //********** db中存在的字段 **********//
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 接口名称
     */
    @NotBlank(message = "name不能为空")
    @Size(max = 64, message = "name长度不能超过64个字符")
    private String name;
    /**
     * 接口(权限)标识
     */
    @NotBlank(message = "code不能为空")
    @Size(max = 64, message = "code长度不能超过64个字符")
    private String code;
    /**
     * 接口地址
     */
    @NotBlank(message = "path不能为空")
    @Size(max = 1024, message = "path长度不能超过1024个字符")
    private String path;
    /**
     * 是否有数据范围
     */
    private Integer hasScope;
    /**
     * 接口类型（字典 1后端接口 2三方接口）
     */
    private Integer apiType;
    /**
     * 扩展信息
     */
    private String extJson;
    /**
     * 备注
     */
    private String remark;
}