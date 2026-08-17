package com.moyu.boot.plugin.ThirdPartyApp.model.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.core.model.PageParam;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * 三方应用请求参数(查询、修改)
 *
 * @author moyusisi
 * @since 2026-08-17
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThirdPartyAppParam extends PageParam {

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
     * 应用标识
     */
    @NotBlank(message = "appKey不能为空")
    @Size(max = 64, message = "appKey长度不能超过64个字符")
    private String appKey;
    /**
     * 应用名称
     */
    @NotBlank(message = "appName不能为空")
    @Size(max = 64, message = "appName长度不能超过64个字符")
    private String appName;
    /**
     * 应用密钥
     */
    @Size(max = 64, message = "appSecret长度不能超过64个字符")
    private String appSecret;
    /**
     * 签名算法
     */
    @NotBlank(message = "digestAlgo不能为空")
    @Size(max = 16, message = "digestAlgo长度不能超过16个字符")
    @Pattern(regexp = "^(md5|sha1|sha256|sha384|sha512)$", message = "签名算法仅支持:md5、sha1、sha256、sha384、sha512")
    private String digestAlgo;
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