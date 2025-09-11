package com.moyu.boot.common.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 打印日志的注解, 可选择打印request, response 和 runTime
 * <p>
 * e.g. 只打印响应结果: {@code @Log(request = false, runTime = false)}
 *
 * @author song.shi
 * @since 2016-06-01
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    /**
     * 是否打印请求参数, 默认为true, 可通过 {@code @Log(request = false)} 来说明不输出
     */
    boolean request() default true;

    /**
     * 是否打印响应结果, 默认为true, 可通过 {@code @Log(response = false)} 来说明不输出
     */
    boolean response() default true;

    /**
     * 是否打印运行时间, 默认为true, 可通过 {@code @Log(runTime = false)} 来说明不输出
     */
    boolean runTime() default true;

    /**
     * 打印日志是否为json格式, 默认为false, 即采用对象的toString而不是json形式, 可通过 {@code @Log(jsonLog = true)} 来设置
     */
    boolean jsonLog() default false;

    /**
     * 只有抛异常时打印请求日志, 可通过 {@code @Log(exceptionOnly = true)} 来设置
     */
    boolean exceptionOnly() default false;
}
