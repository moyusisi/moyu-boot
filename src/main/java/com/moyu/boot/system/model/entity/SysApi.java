package com.moyu.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moyu.boot.common.core.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 接口信息表(sys_api)实体对象
 *
 * @author moyusisi
 * @since 2026-07-20
 */
@Getter
@Setter
@TableName("sys_api")
public class SysApi extends BaseEntity {

    /**
     * 接口名称
     */
    private String name;
    /**
     * 接口(权限)标识
     */
    private String code;
    /**
     * 接口地址
     */
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
