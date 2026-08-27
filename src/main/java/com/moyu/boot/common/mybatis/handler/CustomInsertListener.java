package com.moyu.boot.common.mybatis.handler;

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
        if (o instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) o;
            if (entity.getCreateTime() != null) {
                entity.setCreateTime(new Date());
            }
            if (entity.getCreateBy() != null) {
                entity.setCreateBy(LoginUserUtils.getUsername());
            }
        }
    }
}
