package com.moyu.boot.common.mybatis.annotation;


import java.lang.annotation.*;

/**
 * 数据权限注解(支持指定org或user列名,同时指定则org优先)
 * 数据权限 {@link com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor}
 *
 * @author shisong
 * @since 2025-02-25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DataPermission {

    /**
     * 用于控制用户的列名，如:create_by
     */
    String userColumn() default "create_by";

    /**
     * 用于控制组织机构的列名，如:org_code
     */
    String orgColumn() default "org_code";

    /**
     * 用于控制组织机构层级路径的列名，如:org_path
     */
    String orgPathColumn() default "org_path";

}
