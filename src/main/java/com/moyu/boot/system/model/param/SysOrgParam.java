package com.moyu.boot.system.model.param;


import com.fasterxml.jackson.annotation.JsonInclude;
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
 * 组织查询参数
 *
 * @author shisong
 * @since 2024-11-28
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysOrgParam extends BasePageParam {
    //********** 额外字段 **********//
    /**
     * 待删除的code列表，通常会集联删除
     */
    private Set<String> codes;

    /**
     * 待删除的id列表，通常不会集联删除
     */
    private Set<Long> ids;

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
     * 父节点编码
     */
    @NotBlank(message = "父节点编码parentCode不能为空")
    private String parentCode;

    /**
     * 名称
     */
    @NotBlank(message = "组织名称name不能为空")
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 组织机构类型(字典 1公司组织 2部门机构 3虚拟节点)
     *
     * @see com.moyu.boot.system.enums.OrgTypeEnum
     */
    @NotNull(message = "组织机构类型orgType不能为空")
    @Min(value = 1, message = "组织机构类型orgType有效取值范围为[1,3]")
    @Max(value = 3, message = "组织机构类型orgType有效取值范围为[1,3]")
    private Integer orgType;

    /**
     * 组织层级(字典 1一级公司 2二级公司 3三级公司)
     */
    @NotNull(message = "组织层级orgLevel不能为空")
    @Min(value = 1, message = "组织层级orgLevel有效取值范围为[1,3]")
    @Max(value = 3, message = "组织层级orgLevel有效取值范围为[1,3]")
    private Integer orgLevel;

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
