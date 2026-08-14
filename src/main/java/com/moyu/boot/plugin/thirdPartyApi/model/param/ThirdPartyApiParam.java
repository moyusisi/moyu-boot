package com.moyu.boot.plugin.thirdPartyApi.model.param;

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
 * 三方集成接口请求参数(查询、修改)
 *
 * @author moyusisi
 * @since 2026-08-06
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThirdPartyApiParam extends PageParam {

    //********** 额外字段 **********//
    /**
     * 待删除的id集合
     */
    private Set<Long> ids;

    //********** db中存在的字段 **********//
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 三方接口唯一标识
     */
    @NotBlank(message = "code不能为空")
    @Size(max = 64, message = "code长度不能超过64个字符")
    private String code;
    /**
     * 三方接口名称
     */
    @NotBlank(message = "name不能为空")
    @Size(max = 64, message = "name长度不能超过64个字符")
    private String name;
    /**
     * 三方接口URL
     */
    @NotBlank(message = "url不能为空")
    @Size(max = 1024, message = "url长度不能超过1024个字符")
    private String url;
    /**
     * 请求方式 GET/POST/PUT等
     */
    @NotBlank(message = "requestMethod不能为空")
    @Size(max = 16, message = "requestMethod长度不能超过16个字符")
    private String requestMethod;
    /**
     * 调试状态（0未调试 1已调试）
     */
    private Integer debugStatus;
    /**
     * 请求头参数
     */
    @Size(max = 65535, message = "requestHeader长度不能超过65535个字符")
    private String requestHeader;
    /**
     * 请求体参数
     */
    @Size(max = 65535, message = "requestBody长度不能超过65535个字符")
    private String requestBody;
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