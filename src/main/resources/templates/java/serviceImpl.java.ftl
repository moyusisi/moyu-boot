package ${packageName}.${moduleName}.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.enums.SortOrderEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.core.model.PageData;
import ${packageName}.${moduleName}.mapper.${entityName}Mapper;
import ${packageName}.${moduleName}.model.entity.${entityName};
import ${packageName}.${moduleName}.model.param.${entityName}Param;
import ${packageName}.${moduleName}.model.vo.${entityName}VO;
import ${packageName}.${moduleName}.service.${entityName}Service;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ${entityDesc}服务实现类
 *
 * @author ${author}
 * @since ${.now?string["yyyy-MM-dd"]}
 */
@Slf4j
@Service
public class ${entityName}ServiceImpl extends ServiceImpl<${entityName}Mapper, ${entityName}> implements ${entityName}Service {

    @Override
    public List<${entityName}VO> list(${entityName}Param param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.showInQuery == 1>
        // 指定${fieldConfig.fieldName}查询
            <#if fieldConfig.queryType == "LIKE">
        queryWrapper.like(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'EQ'>
        queryWrapper.eq(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'GT'>
        queryWrapper.gt(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'GE'>
        queryWrapper.ge(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'LT'>
        queryWrapper.lt(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'LE'>
        queryWrapper.le(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'NE'>
        queryWrapper.ne(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'BETWEEN'>
        ${fieldConfig.fieldType} start = param.get${fieldConfig.fieldName?cap_first}1();
        ${fieldConfig.fieldType} end = param.get${fieldConfig.fieldName?cap_first}2();
                <#if fieldConfig.formType == "DATE">
        // 如果是日期范围，则endTime应为当日的结尾
        end = DateUtil.endOfDay(end);
                </#if>
        // 范围查询-起始
        queryWrapper.ge(${entityName}::get${fieldConfig.fieldName?cap_first}, start, ObjectUtil.isNotEmpty(start));
        // 范围查询-截止
        queryWrapper.le(${entityName}::get${fieldConfig.fieldName?cap_first}, end, ObjectUtil.isNotEmpty(end));
            <#elseif fieldConfig.queryType == 'IN'>
        queryWrapper.in(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            </#if>
        </#if>
        <#if fieldConfig.fieldName == 'deleted'>
        // 仅查询未删除的
        queryWrapper.eq(${entityName}::getDeleted, 0);
        </#if>
    </#list>
</#if>
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(${entityName}::getUpdateTime, false);
        }
        // 查询
        List<${entityName}VO> voList = this.listAs(queryWrapper, ${entityName}VO.class);
        return voList;
    }

    @Override
    public PageData<${entityName}VO> pageList(${entityName}Param param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.showInQuery == 1>
        // 指定${fieldConfig.fieldName}查询
            <#if fieldConfig.queryType == "LIKE">
        queryWrapper.like(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'EQ'>
        queryWrapper.eq(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'GT'>
        queryWrapper.gt(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'GE'>
        queryWrapper.ge(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'LT'>
        queryWrapper.lt(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'LE'>
        queryWrapper.le(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'NE'>
        queryWrapper.ne(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            <#elseif fieldConfig.queryType == 'BETWEEN'>
        ${fieldConfig.fieldType} start = param.get${fieldConfig.fieldName?cap_first}1();
        ${fieldConfig.fieldType} end = param.get${fieldConfig.fieldName?cap_first}2();
                <#if fieldConfig.formType == "DATE">
        // 如果是日期，则endTime应为当日的结尾
        end = DateUtil.endOfDay(end);
                </#if>
        // 范围查询-起始
        queryWrapper.ge(${entityName}::get${fieldConfig.fieldName?cap_first}, start, ObjectUtil.isNotEmpty(start));
        // 范围查询-截止
        queryWrapper.le(${entityName}::get${fieldConfig.fieldName?cap_first}, end, ObjectUtil.isNotEmpty(end));
            <#elseif fieldConfig.queryType == 'IN'>
        queryWrapper.in(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}(), ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()));
            </#if>
        </#if>
        <#if fieldConfig.fieldName == 'deleted'>
        // 仅查询未删除的
        queryWrapper.eq(${entityName}::getDeleted, 0);
        </#if>
    </#list>
</#if>
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(StrUtil.toUnderlineCase(param.getSortField()), param.getSortOrder().equals(SortOrderEnum.ASC.getValue()));
        } else {
            queryWrapper.orderBy(${entityName}::getUpdateTime, false);
        }
        // 分页查询
        Page<${entityName}VO> page = Page.of(param.getPageNum(), param.getPageSize());
        Page<${entityName}VO> voPage = this.pageAs(page, queryWrapper, ${entityName}VO.class);
        return new PageData<>(voPage.getTotalRow(), voPage.getRecords());
    }

    @Override
    public ${entityName}VO detail(${entityName}Param param) {
        // 查询
        ${entityName} ${entityName?uncap_first} = this.getById(param.getId());
        if (${entityName?uncap_first} == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        ${entityName}VO vo = BeanUtil.copyProperties(${entityName?uncap_first}, ${entityName}VO.class);
        return vo;
    }

    @Override
    public void add(${entityName}Param param) {
        // 属性复制
        ${entityName} ${entityName?uncap_first} = BeanUtil.copyProperties(param, ${entityName}.class);
        // 其他处理
        ${entityName?uncap_first}.setId(null);
        this.save(${entityName?uncap_first});
    }

    @Override
    public void update(${entityName}Param param) {
        // 通过主键id查询原有数据
        ${entityName} old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        ${entityName} toUpdate = BeanUtil.copyProperties(param, ${entityName}.class, BaseEntity.UPDATE_TIME, BaseEntity.UPDATE_BY);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(${entityName}Param param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 删除时先查再删
        long count = this.count(QueryWrapper.create().in(SysLog::getId, idSet));
        // 要删除的和查询到的进行数量比对
        if (idSet.size() != count) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 物理删除 or 逻辑删除
        this.removeByIds(idSet);
        //LogicDeleteManager.execWithoutLogicDelete(() -> this.removeByIds(idSet));
        // 逻辑删除
        //UpdateChain.of(SysRole.class).set(SysRole::getDeleted, 1).where(SysRole::getId).in(idSet).update();
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<${entityName}VO> build${entityName}VOList(List<${entityName}> entityList) {
        List<${entityName}VO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (${entityName} entity : entityList) {
            ${entityName}VO vo = BeanUtil.copyProperties(entity, ${entityName}VO.class);
            voList.add(vo);
        }
        return voList;
    }
}
