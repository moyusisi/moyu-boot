package com.moyu.boot.system.model.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyu.boot.common.core.enums.DataScopeEnum;
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
 * 岗位信息参数
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysGroupParam extends BasePageParam {
    //********** 额外字段 **********//
    /**
     * 待删除的id列表
     */
    private Set<Long> ids;
    /**
     * 指定的code集合
     */
    private Set<String> codeSet;
    /**
     * 名称关键词
     */
    private String searchKey;

    //********** db中存在的字段 **********//
    /**
     * 主键id
     */
    private Long id;

    /**
     * 名称
     */
    @NotBlank(message = "分组名称name不能为空")
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 直属组织
     */
    @NotBlank(message = "直属组织orgCode不能为空")
    private String orgCode;

    /**
     * 数据范围(字典 0无限制 1本人数据 2本机构 3本机构及以下 4自定义)
     *
     * @see DataScopeEnum
     */
    private Integer dataScope;

    /**
     * 自定义scope集合,逗号分隔
     */
    private String scopeSet;

    /**
     * 排序顺序
     */
    @NotNull(message = "排序顺序sortNum不能为空")
    private Integer sortNum;

    /**
     * 状态（0正常 1停用）
     */
    @NotNull(message = "分组状态status不能为空")
    @Min(value = 0, message = "分组状态status有效取值范围为[0,1]")
    @Max(value = 1, message = "分组状态status有效取值范围为[0,1]")
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