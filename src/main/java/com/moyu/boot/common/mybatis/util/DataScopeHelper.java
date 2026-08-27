package com.moyu.boot.common.mybatis.util;

import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.authZ.util.LoginUserUtils;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.mybatisflex.core.util.LambdaGetter;
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

    public static <T> void dataScopeFilter(com.mybatisflex.core.query.QueryWrapper queryWrapper, LambdaGetter<T> userColumn, LambdaGetter<T> orgColumn) {
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
                queryWrapper.in(orgColumn, scopeSet, ObjectUtil.isNotEmpty(scopeSet));
            } else if (DataScopeEnum.COMPANY.getCode().equals(dataScope)) {
                // 通过 scopeSet 处理
                queryWrapper.in(orgColumn, scopeSet, ObjectUtil.isNotEmpty(scopeSet));
            } else if (DataScopeEnum.ORG_DEFINE.getCode().equals(dataScope)) {
                // 通过 scopeSet 处理
                queryWrapper.in(orgColumn, scopeSet, ObjectUtil.isNotEmpty(scopeSet));
            }
        }
    }

}
