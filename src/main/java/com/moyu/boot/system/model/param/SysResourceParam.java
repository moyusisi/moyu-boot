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
 * 菜单查询参数
 *
 * @author shisong
 * @since 2024-11-28
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysResourceParam extends BasePageParam {
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
     * 角色code
     */
    private String roleCode;

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
     * 父编码
     */
    private String parentCode;

    /**
     * 名称
     */
    @NotBlank(message = "名称name不能为空")
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 资源类型（字典 1模块 2目录 3菜单 4内链 5外链 6按钮）
     *
     * @see com.moyu.boot.system.enums.ResourceTypeEnum
     */
    @NotNull(message = "菜单类型resourceType不能为空")
    @Min(value = 1, message = "资源类型resourceType有效取值范围为[1,5]")
    @Max(value = 5, message = "资源类型resourceType有效取值范围为[1,5]")
    private Integer resourceType;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 链接地址
     */
    private String link;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否可见（0不可见 1可见）/数据权限（0无 1有）
     */
    private Integer visible;

    /**
     * 归属模块
     */
    private String module;

    /**
     * 排序顺序
     */
    @NotNull(message = "排序顺序sortNum不能为空")
    private Integer sortNum;

    /**
     * 扩展信息
     */
    private String extJson;

    /**
     * 备注
     */
    private String remark;
}
