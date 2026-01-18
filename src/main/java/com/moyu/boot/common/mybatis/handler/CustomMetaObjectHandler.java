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
            // 严格填充,只针对非主键的字段,只有该表注解了fill 并且 字段名和字段属性 能匹配到才会进行填充(null 值不填充)。
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
            // 会自动判断表中是否有对应的字段
            // 更新时不使用严格模式,不管原来是否有值,都更新(严格模式只有原值为null才更新)
            this.setFieldValByName(UPDATE_TIME, new Date(), metaObject);
            this.setFieldValByName(UPDATE_BY, getUserId(), metaObject);
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
