package com.moyu.boot.common.core.annotation;


import java.lang.annotation.*;

/**
 * 持久化日志的注解，记录在sys_log表中
 * 仅支持在方法上加注解，用于记录Controller的操作
 *
 * @author shisong
 * @since 2025-10-23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SysLog {
    String value() default "";

    /**
     * 系统/模块
     */
    String module() default "";

    /**
     * 业务,用于记录哪个业务
     */
    String business() default "";

    /**
     * 操作,用于记录对应业务的什么操作, 如controller的某方法
     */
    String operate() default "";

    /**
     * 内容，用于自定义内容，便于日志搜索
     */
    String content() default "";

    /**
     * 是否记录请求参数, 默认为true
     */
    boolean request() default true;

    /**
     * 是否记录响应结果, 默认为false, 避免日志过大
     */
    boolean response() default false;

}
