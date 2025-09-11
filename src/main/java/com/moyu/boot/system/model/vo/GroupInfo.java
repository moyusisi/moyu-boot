package com.moyu.boot.system.model.vo;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 岗位信息视图对象
 *
 * @author shisong
 * @since 2025-03-06
 */
@Getter
@Setter
@ToString
@Builder
public class GroupInfo {
    /**
     * 岗位code
     */
    private String code;
    /**
     * 岗位名称
     */
    private String name;
    /**
     * 当前组织机构
     */
    private String orgCode;
    /**
     * 当前组织机构名称
     */
    private String orgName;
    /**
     * 当前组织机构名称全称
     */
    private String orgFullName;
}
