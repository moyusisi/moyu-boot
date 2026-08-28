package com.moyu.boot.common.mybatis.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.moyu.boot.common.authZ.util.LoginUserUtils;
import com.moyu.boot.common.core.model.BaseEntity;
import com.mybatisflex.annotation.InsertListener;

import java.util.Date;

/**
 * 插入时字段填充(无值才填充)
 * 应用层填充，避免数据库的差异
 *
 * @author shisong
 * @since 2026-08-26
 */
public class CustomInsertListener implements InsertListener {

    @Override
    public void onInsert(Object o) {
        if (o == null) {
            return;
        }
        if (o instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) o;
            if (entity.getCreateTime() == null) {
                entity.setCreateTime(new Date());
            }
            if (entity.getCreateBy() == null) {
                entity.setCreateBy(LoginUserUtils.getUsername());
            }
        } else {
            // 字段存在且值为null则填充
            if (ReflectUtil.hasField(o.getClass(), BaseEntity.CREATE_TIME)
                    && BeanUtil.getFieldValue(o, BaseEntity.CREATE_TIME) == null) {
                BeanUtil.setFieldValue(o, BaseEntity.CREATE_TIME, new Date());
            }
            if (ReflectUtil.hasField(o.getClass(), BaseEntity.CREATE_BY)
                    && BeanUtil.getFieldValue(o, BaseEntity.CREATE_BY) == null) {
                BeanUtil.setFieldValue(o, BaseEntity.CREATE_BY, LoginUserUtils.getUsername());
            }
        }
    }
}
