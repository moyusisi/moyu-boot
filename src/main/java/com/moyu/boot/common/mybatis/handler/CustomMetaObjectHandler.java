package com.moyu.boot.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.moyu.boot.common.core.model.BaseEntity;
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

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            // 严格填充,只针对非主键的字段,只有该表注解了fill 并且 字段名和字段属性 能匹配到才会进行填充(null 值不填充)。
            this.strictInsertFill(metaObject, BaseEntity.DELETED, Integer.class, 0);
            this.strictInsertFill(metaObject, BaseEntity.CREATE_TIME, Date.class, new Date());
            this.strictInsertFill(metaObject, BaseEntity.CREATE_BY, String.class, getUserId());
            this.strictInsertFill(metaObject, BaseEntity.UPDATE_TIME, Date.class, new Date());
            this.strictInsertFill(metaObject, BaseEntity.UPDATE_BY, String.class, getUserId());
        } catch (Exception e) {
            log.warn("CustomMetaObjectHandler自动填充字段失败，可不做处理");
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            // 严格填充,只针对非主键的字段,只有该表注解了fill 并且 字段名和字段属性 能匹配到才会进行填充(null 值不填充)。
            this.strictUpdateFill(metaObject, BaseEntity.UPDATE_TIME, Date.class, new Date());
            this.strictUpdateFill(metaObject, BaseEntity.UPDATE_BY, String.class, getUserId());
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
