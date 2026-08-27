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
 * 系统配置表(sys_config)实体对象
 *
 * @author moyusisi
 * @since 2026-05-28
 */
@Getter
@Setter
@Table(value = "sys_config", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
public class SysConfig extends BaseEntity {
    /**
     * 主键id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    /**
     * 配置项名称
     */
    private String configName;
    /**
     * 配置key
     */
    private String configKey;
    /**
     * 配置value
     */
    private String configValue;
    /**
     * 配置类型
     */
    private String configType;
    /**
     * 使用状态（0正常 1停用）
     */
    private Integer status;
    /**
     * 扩展信息
     */
    private String extJson;
    /**
     * 备注
     */
    private String remark;
    /**
     * 删除标志（0未删除  1已删除）
     */
    private Integer deleted;

}
