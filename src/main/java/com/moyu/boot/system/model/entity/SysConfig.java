package com.moyu.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.moyu.boot.common.core.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

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
    * 配置项
    */
    private String keyTitle;
    /**
    * 配置name
    */
    private String keyName;
    /**
    * 配置value
    */
    private String keyValue;
    /**
    * 配置项类型
    */
    private String keyType;
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
