package com.moyu.boot.common.mybatis.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.moyu.boot.common.authZ.util.LoginUserUtils;
import com.moyu.boot.common.core.model.BaseEntity;
import com.mybatisflex.annotation.UpdateListener;

import java.util.Date;

/**
 * 更新时字段填充(无值才填充)
 * 应用层填充，避免数据库的差异
 *
 * @author shisong
 * @since 2026-08-26
 */
public class CustomUpdateListener implements UpdateListener {
    @Override
    public void onUpdate(Object o) {
        if (o == null) {
            return;
        }
        if (o instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) o;
            if (entity.getUpdateTime() == null) {
                entity.setUpdateTime(new Date());
            }
            if (entity.getUpdateBy() == null) {
                entity.setUpdateBy(LoginUserUtils.getUsername());
            }
        } else {
            // 字段存在且值为null则填充
            if (ReflectUtil.hasField(o.getClass(), BaseEntity.UPDATE_TIME)
                    && BeanUtil.getFieldValue(o, BaseEntity.UPDATE_TIME) == null) {
                BeanUtil.setFieldValue(o, BaseEntity.UPDATE_TIME, new Date());
            }
            if (ReflectUtil.hasField(o.getClass(), BaseEntity.UPDATE_BY)
                    && BeanUtil.getFieldValue(o, BaseEntity.UPDATE_BY) == null) {
                BeanUtil.setFieldValue(o, BaseEntity.UPDATE_BY, LoginUserUtils.getUsername());
            }
        }
    }
}
