package com.moyu.boot.common.mybatis.handler;

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
        if (o instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) o;
            if (entity.getUpdateTime() != null) {
                entity.setUpdateTime(new Date());
            }
            if (entity.getUpdateBy() != null) {
                entity.setUpdateBy(LoginUserUtils.getUsername());
            }
        }
    }
}
