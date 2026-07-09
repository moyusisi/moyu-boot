package com.moyu.boot.support.sysConfig.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.moyu.boot.common.core.model.BaseEntity;
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
@TableName("sys_config")
public class SysConfig extends BaseEntity {

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

}
