package com.moyu.boot.system.model.entity;

import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.mybatis.handler.CustomInsertListener;
import com.moyu.boot.common.mybatis.handler.CustomUpdateListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
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
@Table(value = "sys_api", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
public class SysApi extends BaseEntity {
    /**
     * 主键id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
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
