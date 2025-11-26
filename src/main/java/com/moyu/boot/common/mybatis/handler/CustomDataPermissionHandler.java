package com.moyu.boot.common.mybatis.handler;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.mybatis.annotation.DataPermission;
import com.moyu.boot.common.security.util.SecurityUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * 自定义数据权限处理器
 *
 * @author shisong
 * @since 2025-02-25
 */
@Slf4j
public class CustomDataPermissionHandler implements MultiDataPermissionHandler {

    /**
     * @param table             所执行的数据库表信息，可以通过此参数获取表名和表别名
     * @param where             原有的 where 条件信息
     * @param mappedStatementId Mybatis MappedStatement Id 根据该参数可以判断具体执行方法
     * @return JSqlParser 条件表达式，返回的条件表达式会拼接在原有的表达式后面（不会覆盖原有的表达式）
     */
    @Override
    @SneakyThrows
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // mappedStatementId 根据该参数可以判断具体执行方法，xml中为自定义名，注解中为方法签名，类似于com.xx.xxMapper.selectList
        Class<?> clazz = Class.forName(mappedStatementId.substring(0, mappedStatementId.lastIndexOf(StringPool.DOT)));
        // 只有Mapper中的方法才会被拦截到
        String methodName = mappedStatementId.substring(mappedStatementId.lastIndexOf(StringPool.DOT) + 1);
        Method[] methods = clazz.getDeclaredMethods();
        Optional<Method> optMethod = Arrays.stream(methods).filter(e -> methodName.equals(e.getName())).findFirst();
        // 若未匹配到执行的mapper方法,不做处理返回
        if (!optMethod.isPresent()) {
            return null;
        }
        DataPermission annotation = optMethod.get().getAnnotation(DataPermission.class);
        // 若匹配到的执行方法上无注解，不做处理返回
        if (annotation == null) {
            return null;
        }
        // root超管不做任何限制
        if (SecurityUtils.isRoot()) {
            return null;
        }
        log.debug("{} 执行数据权限过滤", mappedStatementId);
        // 匹配到的方法上有注解，则按照注解内容生成追加的表达式
        return dataScopeFilter(annotation);
    }

    /**
     * 设置数据权限的语句，可在通过拦截注解此处理，也可不通过注解直接在代码中处理
     *
     * @return 构建后查询条件
     */
    @SneakyThrows
    public static Expression dataScopeFilter(DataPermission annotation) {
        // 指定的列名
        String orgColumn = annotation.orgColumn();
        Integer dataScope = SecurityUtils.getDataScope();
        DataScopeEnum scopeEnum = DataScopeEnum.getByCode(dataScope);
        // 要追加的条件
        String sqlStr = "";
        switch (scopeEnum) {
            case ALL: {
                //  不限制
                return null;
            }
            case SELF: {
                // 仅自己
                String username = SecurityUtils.getUsername();
                sqlStr = annotation.userColumn() + " = '" + username + "'";
                break;
            }
            case ORG: {
                // 本机构
                String orgCode = SecurityUtils.getGroupOrgCode();
                sqlStr = orgColumn + " = '" + orgCode + "'";
                break;
            }
            case ORG_CHILD: {
                // 本机构及以下
                String orgCode = SecurityUtils.getGroupOrgCode();
                // 第一种方法使用IN scopes,这种处理方式适合所有服务都能访问组织机构表且组织机构不多的情况(否则数量过多会导致in效率低下)
                // sqlStr = orgColumn + " IN ( SELECT code FROM sys_org WHERE code = '" + orgCode + "' OR find_in_set( '" + orgCode + "' , org_path ) )";
                // 第二种方法使用 orgPath Like, 这种处理方式则需要在数据表中新增一个表示组织机构树层级路径的字段，如:org_path(组织机构若变更则要洗数据)
                // sqlStr = "( " + orgColumn + " = '" + orgCode + "' OR " + annotation.orgPathColumn() + " LIKE '%" + orgCode + "%' )";
                // 第三种方法使用 orgPath find_in_set, 这种处理方式与like类似但效率高点
                sqlStr = "( " + orgColumn + " = '" + orgCode + "' OR find_in_set('" + orgCode + "', " + annotation.orgPathColumn() + " ) )";
                break;
            }
            case ORG_DEFINE: {
                //  自定义
                Set<String> scopes = SecurityUtils.getScopes();
                if (ObjectUtil.isEmpty(scopes)) {
                    sqlStr = "1 = 0";
                } else {
                    sqlStr = orgColumn + " IN ('" + CollectionUtil.join(scopes, "', '") + "')";
                }
                break;
            }
            default: {
                // 默认不处理数据权限
                return null;
            }
        }
        if (StrUtil.isBlank(sqlStr)) {
            return null;
        }
        log.debug("数据权限为:{}, 追加的过滤条件为:{}", scopeEnum, sqlStr);
        // 将sqlStr转换为条件表达式
        return CCJSqlParserUtil.parseCondExpression(sqlStr);
    }

}
