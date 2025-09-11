package com.moyu.boot.common.core.annotation;

import com.moyu.boot.common.core.config.CommonLogConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用日志组件的自动配置注解
 *
 * @author song.shi
 * @since 2016-06-01
 */
@Import(CommonLogConfig.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCommonLog {
}
