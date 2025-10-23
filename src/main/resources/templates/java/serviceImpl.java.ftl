package ${packageName}.${moduleName}.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import ${packageName}.${moduleName}.mapper.${entityName}Mapper;
import ${packageName}.${moduleName}.model.entity.${entityName};
import ${packageName}.${moduleName}.model.param.${entityName}Param;
import ${packageName}.${moduleName}.model.vo.${entityName}VO;
import ${packageName}.${moduleName}.service.${entityName}Service;
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
        LambdaQueryWrapper<${entityName}> queryWrapper = Wrappers.lambdaQuery(${entityName}.class);
<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.showInQuery == 1>
        // 指定${fieldConfig.fieldName}查询
            <#if fieldConfig.queryType == "LIKE">
        queryWrapper.like(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'EQ'>
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'GT'>
        queryWrapper.gt(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'GE'>
        queryWrapper.ge(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'LT'>
        queryWrapper.lt(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'LE'>
        queryWrapper.le(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'NE'>
        queryWrapper.ne(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'BETWEEN'>
        if (param.get${fieldConfig.fieldName?cap_first}Range() != null && param.get${fieldConfig.fieldName?cap_first}Range().size() > 1 && ObjectUtil.isAllNotEmpty(param.get${fieldConfig.fieldName?cap_first}Range().get(0), param.get${fieldConfig.fieldName?cap_first}Range().get(1))) {
            Date startTime = param.get${fieldConfig.fieldName?cap_first}Range().get(0);
            Date endTime = param.get${fieldConfig.fieldName?cap_first}Range().get(1);
                <#if fieldConfig.formType == "DATE">
            // 如果是日期范围，则endTime应为当日的结尾
            endTime = DateUtil.endOfDay(endTime);
                </#if>
            queryWrapper.between(${entityName}::get${fieldConfig.fieldName?cap_first}, startTime, endTime);
        }
            <#elseif fieldConfig.queryType == 'IN'>
        queryWrapper.in(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            </#if>
        </#if>
        <#if fieldConfig.fieldName == 'deleted'>
        // 仅查询未删除的
        queryWrapper.eq(${entityName}::getDeleted, 0);
        </#if>
    </#list>
</#if>
        // TODO 指定排序
        queryWrapper.orderByDesc(${entityName}::getUpdateTime);
        // 查询
        List<${entityName}> ${entityName?uncap_first}List = this.list(queryWrapper);
        // 转换为voList
        List<${entityName}VO> voList = build${entityName}VOList(${entityName?uncap_first}List);
        return voList;
    }

    @Override
    public PageData<${entityName}VO> pageList(${entityName}Param param) {
        // 查询条件
        LambdaQueryWrapper<${entityName}> queryWrapper = Wrappers.lambdaQuery(${entityName}.class);
<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.showInQuery == 1>
        // 指定${fieldConfig.fieldName}查询
            <#if fieldConfig.queryType == "LIKE">
        queryWrapper.like(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'EQ'>
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'GT'>
        queryWrapper.gt(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'GE'>
        queryWrapper.ge(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'LT'>
        queryWrapper.lt(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'LE'>
        queryWrapper.le(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'NE'>
        queryWrapper.ne(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            <#elseif fieldConfig.queryType == 'BETWEEN'>
        if (param.get${fieldConfig.fieldName?cap_first}Range() != null && param.get${fieldConfig.fieldName?cap_first}Range().size() > 1 && ObjectUtil.isAllNotEmpty(param.get${fieldConfig.fieldName?cap_first}Range().get(0), param.get${fieldConfig.fieldName?cap_first}Range().get(1))) {
            Date startTime = param.get${fieldConfig.fieldName?cap_first}Range().get(0);
            Date endTime = param.get${fieldConfig.fieldName?cap_first}Range().get(1);
                <#if fieldConfig.formType == "DATE">
            // 如果是日期范围，则endTime应为当日的结尾
            endTime = DateUtil.endOfDay(endTime);
                </#if>
            queryWrapper.between(${entityName}::get${fieldConfig.fieldName?cap_first}, startTime, endTime);
        }
            <#elseif fieldConfig.queryType == 'IN'>
        queryWrapper.in(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            </#if>
        </#if>
        <#if fieldConfig.fieldName == 'deleted'>
        // 仅查询未删除的
        queryWrapper.eq(${entityName}::getDeleted, 0);
        </#if>
    </#list>
</#if>
        // TODO 指定排序
        queryWrapper.orderByDesc(${entityName}::getUpdateTime);
        // 分页查询
        Page<${entityName}> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<${entityName}> ${entityName?uncap_first}Page = this.page(page, queryWrapper);
        List<${entityName}VO> voList = build${entityName}VOList(${entityName?uncap_first}Page.getRecords());
        return new PageData<>(${entityName?uncap_first}Page.getTotal(), voList);
    }

    @Override
    public ${entityName}VO detail(${entityName}Param param) {
        // 查询
        ${entityName} ${entityName?uncap_first} = this.getById(param.getId());
        if (${entityName?uncap_first} == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
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
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "更新失败，未查到原数据");
        }
        // 属性复制
        ${entityName} toUpdate = BeanUtil.copyProperties(param, ${entityName}.class);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(${entityName}Param param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(${entityName}.class).in(${entityName}::getId, idSet).set(${entityName}::getDeleted, 1));
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
