package com.moyu.boot.system.model.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.core.model.BasePageParam;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 角色信息参数
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysRoleParam extends BasePageParam {
    //********** 额外字段 **********//
    /**
     * 待删除的id列表
     */
    private Set<Long> ids;
    /**
     * 指定要查询的code集合
     */
    private Set<String> codeSet;
    /**
     * 指定要查询的orgCode
     */
    private String orgCode;
    /**
     * 角色授权时的资源code列表(按钮也在里面)
     */
    private Set<String> grantMenuList;

    /**
     * 指定模块
     */
    private String module;

    /**
     * 名称关键词
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
     * 名称
     */
    @NotBlank(message = "角色名称name不能为空")
    private String name;
    /**
     * 编码
     */
    private String code;

    /**
     * 排序顺序
     */
    private Integer sortNum;

    /**
     * 使用状态（0正常 1停用）
     */
    @NotNull(message = "角色状态status不能为空")
    @Min(value = 0, message = "角色状态status有效取值范围为[0,1]")
    @Max(value = 1, message = "角色状态status有效取值范围为[0,1]")
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