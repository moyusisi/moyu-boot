package com.moyu.boot.plugin.dblog.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 系统日志表(sys_log)实体对象
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Getter
@Setter
@TableName("sys_log")
public class SysLog extends BaseEntity {

    /**
    * 请求路径地址
    */
    private String requestUrl;
    /**
    * 系统/模块
    */
    private String module;
    /**
    * 业务
    */
    private String business;
    /**
    * 操作
    */
    private String operate;
    /**
    * 内容说明
    */
    private String content;
    /**
    * 请求参数
    */
    private String requestContent;
    /**
    * 返回结果
    */
    private String responseContent;
    /**
    * 开始时间
    */
    private Date startTime;
    /**
    * 结束时间
    */
    private Date endTime;
    /**
    * 执行耗时(ms)
    */
    private Long executionTime;

}
