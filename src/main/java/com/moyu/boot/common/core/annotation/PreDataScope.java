package com.moyu.boot.common.core.annotation;


import java.lang.annotation.*;

/**
 * 准备数据范围注解(若要使用数据权限需先对数据范围进行准备才可使用)
 *
 * @author shisong
 * @since 2026-01-05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PreDataScope {

    /**
     * 需要准备数据范围的接口权限标识
     */
    String value();

}
