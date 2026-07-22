package com.moyu.boot.common.mybatis.util;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.moyu.boot.common.authZ.util.LoginUserUtils;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 数据范围拼接处理工具
 *
 * @author shisong
 * @since 2026-07-16
 */
@Slf4j
public class DataScopeHelper {

    public static <T, R> void dataScopeFilter(LambdaQueryWrapper<T> queryWrapper, SFunction<T, R> userColumn, SFunction<T, R> orgColumn) {
        // 非ROOT则限制数据范围
        if (!LoginUserUtils.isRoot()) {
            // 数据范围
            Integer dataScope = LoginUserUtils.getDataScope();
            Set<String> scopeSet = LoginUserUtils.getScopes();

            if (DataScopeEnum.SELF.getCode().equals(dataScope)) {
                String username = LoginUserUtils.getUsername();
                queryWrapper.eq(userColumn, username);
            } else if (DataScopeEnum.ORG.getCode().equals(dataScope)) {
                String orgCode = LoginUserUtils.getOrgCode();
                queryWrapper.eq(orgColumn, orgCode);
            } else if (DataScopeEnum.ORG_CHILD.getCode().equals(dataScope)) {
                // 通过 scopeSet 处理
                queryWrapper.in(ObjectUtil.isNotEmpty(scopeSet), orgColumn, scopeSet);
            } else if (DataScopeEnum.COMPANY.getCode().equals(dataScope)) {
                // 通过 scopeSet 处理
                queryWrapper.in(ObjectUtil.isNotEmpty(scopeSet), orgColumn, scopeSet);
            } else if (DataScopeEnum.ORG_DEFINE.getCode().equals(dataScope)) {
                // 通过 scopeSet 处理
                queryWrapper.in(ObjectUtil.isNotEmpty(scopeSet), orgColumn, scopeSet);
            }
        }
    }

    public static <T> void dataScopeFilter(QueryWrapper<T> queryWrapper, String userColumn, String orgColumn) {
        // 非ROOT则限制数据范围
        if (!LoginUserUtils.isRoot()) {
            // 数据范围
            Integer dataScope = LoginUserUtils.getDataScope();
            Set<String> scopeSet = LoginUserUtils.getScopes();

            if (DataScopeEnum.SELF.getCode().equals(dataScope)) {
                String username = LoginUserUtils.getUsername();
                queryWrapper.eq(userColumn, username);
            } else if (DataScopeEnum.ORG.getCode().equals(dataScope)) {
                String orgCode = LoginUserUtils.getOrgCode();
                queryWrapper.eq(orgColumn, orgCode);
            } else if (DataScopeEnum.ORG_CHILD.getCode().equals(dataScope)) {
                queryWrapper.in(ObjectUtil.isNotEmpty(scopeSet), orgColumn, scopeSet);
            } else if (DataScopeEnum.COMPANY.getCode().equals(dataScope)) {
                queryWrapper.in(ObjectUtil.isNotEmpty(scopeSet), orgColumn, scopeSet);
            } else if (DataScopeEnum.ORG_DEFINE.getCode().equals(dataScope)) {
                queryWrapper.in(ObjectUtil.isNotEmpty(scopeSet), orgColumn, scopeSet);
            }
        }
    }
}
