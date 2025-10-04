package com.moyu.boot.common.mybatis.handler;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import com.moyu.boot.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * mybatis-plus 自动填充BaseEntity中的公共字段
 *
 * @author shisong
 * @see BaseEntity
 * @since 2024-12-23
 */
@Slf4j
public class CustomMetaObjectHandler implements MetaObjectHandler {

    /**
     * 删除标志
     */
    private static final String DELETED = "deleted";
    /**
     * 创建时间
     */
    private static final String CREATE_TIME = "createTime";
    /**
     * 创建人
     */
    private static final String CREATE_BY = "createBy";
    /**
     * 更新时间
     */
    private static final String UPDATE_TIME = "updateTime";
    /**
     * 更新人
     */
    private static final String UPDATE_BY = "updateBy";

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            // 严格模式的插入填充，只有当字段为空时才进行填充，避免覆盖已有的值。
            this.strictInsertFill(metaObject, DELETED, Integer.class, 0);
            this.strictInsertFill(metaObject, CREATE_TIME, Date.class, new Date());
            this.strictInsertFill(metaObject, CREATE_BY, String.class, getUserId());
            this.strictInsertFill(metaObject, UPDATE_TIME, Date.class, new Date());
            this.strictInsertFill(metaObject, UPDATE_BY, String.class, getUserId());
        } catch (Exception e) {
            log.warn("CustomMetaObjectHandler自动填充字段失败，可不做处理");
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            // setFieldValByName方法会判断db中是否有对应的字段,无需判断 (metaObject.getOriginalObject() instanceof BaseEntity)
            // 更新时不使用严格模式,不管原来是否有值,都更新
            setFieldValByName(UPDATE_TIME, new Date(), metaObject);
            setFieldValByName(UPDATE_BY, getUserId(), metaObject);
        } catch (Exception e) {
            log.warn("CustomMetaObjectHandler.updateFill自动填充字段失败，可不做处理");
        }
    }

    /**
     * 获取用户id
     */
    private String getUserId() {
        return SecurityUtils.getUsername();
    }
}
