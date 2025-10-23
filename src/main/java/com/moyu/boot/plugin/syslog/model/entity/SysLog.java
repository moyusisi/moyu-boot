package com.moyu.boot.plugin.syslog.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

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
public class SysLog {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 请求路径地址
     */
    private String requestUrl;
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

    /**
     * 删除标志（0未删除  1已删除）
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

}
