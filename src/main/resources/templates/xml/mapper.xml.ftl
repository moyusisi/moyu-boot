<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.${moduleName}.mapper.${entityName}Mapper">

    <!-- 获取${entityDesc}分页列表 -->
    <select id="get${entityName}Page" resultType="${packageName}.${moduleName}.model.vo.${entityName}VO">
        SELECT * FROM ${tableName}
        <where>
<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.showInQuery == 1>
            <#if fieldConfig.queryType == "LIKE">
                <#if fieldConfig.fieldType == "String">
            <if test="param.${fieldConfig.fieldName} != null and param.${fieldConfig.fieldName} != ''">
                <#else>
            <if test="param.${fieldConfig.fieldName} != null">
                </#if>
                AND ${fieldConfig.columnName} LIKE CONCAT('%', <#noparse>#{</#noparse>param.${fieldConfig.fieldName}}, '%')
            </if>
            <#elseif fieldConfig.queryType == "EQ">
                <#if fieldConfig.fieldType == "String">
            <if test="param.${fieldConfig.fieldName} != null and param.${fieldConfig.fieldName} != ''">
                <#else>
            <if test="param.${fieldConfig.fieldName} != null">
                </#if>
                AND ${fieldConfig.columnName} = <#noparse>#{</#noparse>param.${fieldConfig.fieldName}}
            </if>
            <#elseif fieldConfig.queryType == "GT">
                <#if fieldConfig.fieldType == "String">
            <if test="param.${fieldConfig.fieldName} != null and param.${fieldConfig.fieldName} != ''">
                <#else>
            <if test="param.${fieldConfig.fieldName} != null">
                </#if>
                AND ${fieldConfig.columnName} > <#noparse>#{</#noparse>param.${fieldConfig.fieldName}}
            </if>
            <#elseif fieldConfig.queryType == "GE">
                <#if fieldConfig.fieldType == "String">
            <if test="param.${fieldConfig.fieldName} != null and param.${fieldConfig.fieldName} != ''">
                <#else>
            <if test="param.${fieldConfig.fieldName} != null">
                </#if>
                AND ${fieldConfig.columnName} >= <#noparse>#{</#noparse>param.${fieldConfig.fieldName}}
            </if>
            <#elseif fieldConfig.queryType == "LT">
                <#if fieldConfig.fieldType == "String">
            <if test="param.${fieldConfig.fieldName} != null and param.${fieldConfig.fieldName} != ''">
                <#else>
            <if test="param.${fieldConfig.fieldName} != null">
                </#if>
                AND ${fieldConfig.columnName} < <#noparse>#{</#noparse>param.${fieldConfig.fieldName}}
            </if>
            <#elseif fieldConfig.queryType == "LE">
                <#if fieldConfig.fieldType == "String">
            <if test="param.${fieldConfig.fieldName} != null and param.${fieldConfig.fieldName} != ''">
                <#else>
            <if test="param.${fieldConfig.fieldName} != null">
                </#if>
                AND ${fieldConfig.columnName} <= <#noparse>#{</#noparse>param.${fieldConfig.fieldName}}
            </if>
            <#elseif fieldConfig.queryType == "NE">
                <#if fieldConfig.fieldType == "String">
            <if test="param.${fieldConfig.fieldName} != null and param.${fieldConfig.fieldName} != ''">
                <#else>
            <if test="param.${fieldConfig.fieldName} != null">
                </#if>
                AND ${fieldConfig.columnName} != <#noparse>#{</#noparse>param.${fieldConfig.fieldName}}
            </if>
            <#elseif fieldConfig.queryType == "BETWEEN">
            <if test="param.${fieldConfig.fieldName}Range[0] != null and param.${fieldConfig.fieldName}Range[0] != ''">
                AND ${fieldConfig.columnName} &gt;= <#noparse>#{</#noparse>param.${fieldConfig.fieldName}Range[0]}
            </if>
            <if test="param.${fieldConfig.fieldName}Range[1] != null and param.${fieldConfig.fieldName}Range[1] != ''">
                AND ${fieldConfig.columnName} &lt;= <#noparse>#{</#noparse>param.${fieldConfig.fieldName}Range[1]}
            </if>
            <#elseif fieldConfig.queryType == "IN">
                AND ${fieldConfig.columnName} IN  <foreach collection="param.${fieldConfig.fieldName}" item="item" open="(" close=")" separator=",">#{item}</foreach>
            </#if>
        </#if>
    </#list>
</#if>
        </where>
    </select>

</mapper>