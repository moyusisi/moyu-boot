package ${packageName}.${moduleName}.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
        LambdaQueryWrapper<${entityName}> queryWrapper = Wrappers.lambdaQuery(${entityName}.class)
<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.showInQuery == 1>
            // 指定${fieldConfig.fieldName}查询条件
            <#if fieldConfig.queryType == "LIKE">
            .like(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'EQ'>
            .eq(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'GT'>
            .gt(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'GE'>
            .ge(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'LT'>
            .lt(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'LE'>
            .le(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'NE'>
            .ne(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'BETWEEN'>
            .between(ObjectUtil.isAllNotEmpty(param.get${fieldConfig.fieldName?cap_first}Range().get(0), param.get${fieldConfig.fieldName?cap_first}Range().get(1)),
                    ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}Range().get(0), param.get${fieldConfig.fieldName?cap_first}Range().get(1))
            <#elseif fieldConfig.queryType == 'IN'>
            .in(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            </#if>
        </#if>
    </#list>
</#if>
            // TODO 是否需要排序
            .orderByDesc(${entityName}::getUpdateTime);
        // 查询
        List<${entityName}> ${entityName?uncap_first}List = this.list(queryWrapper);
        // 转换为voList
        List<${entityName}VO> voList = build${entityName}VOList(${entityName?uncap_first}List);
        return voList;
    }

    @Override
    public PageData<${entityName}VO> pageList(${entityName}Param param) {
        // 查询条件
        LambdaQueryWrapper<${entityName}> queryWrapper = Wrappers.lambdaQuery(${entityName}.class)
<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.showInQuery == 1>
            // 指定${fieldConfig.fieldName}查询条件
            <#if fieldConfig.queryType == "LIKE">
            .like(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'EQ'>
            .eq(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'GT'>
            .gt(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'GE'>
            .ge(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'LT'>
            .lt(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'LE'>
            .le(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'NE'>
            .ne(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'BETWEEN'>
            .between(ObjectUtil.isAllNotEmpty(param.get${fieldConfig.fieldName?cap_first}Range().get(0), param.get${fieldConfig.fieldName?cap_first}Range().get(1)),
                    ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}Range().get(0), param.get${fieldConfig.fieldName?cap_first}Range().get(1))
            <#elseif fieldConfig.queryType == 'IN'>
            .in(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            </#if>
        </#if>
    </#list>
</#if>
            // TODO 是否需要排序
            .orderByDesc(${entityName}::getUpdateTime);
        // 分页查询
        Page<${entityName}> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<${entityName}> ${entityName?uncap_first}Page = this.page(page, queryWrapper);
        List<${entityName}VO> voList = build${entityName}VOList(${entityName?uncap_first}Page.getRecords());
        return new PageData<>(${entityName?uncap_first}Page.getTotal(), voList);
    }

    @Override
    public ${entityName}VO detail(${entityName}Param param) {
        // 查询条件
        LambdaQueryWrapper<${entityName}> queryWrapper = Wrappers.lambdaQuery(${entityName}.class);
<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.showInQuery == 1>
        // 指定${fieldConfig.fieldName}查询条件
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
            queryWrapper.between(${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}Range().get(0), param.get${fieldConfig.fieldName?cap_first}Range().get(1));
        }
            <#elseif fieldConfig.queryType == 'IN'>
        queryWrapper.in(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}()), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}());
            </#if>
        </#if>
    </#list>
</#if>
        // 查询
        ${entityName} ${entityName?uncap_first} = this.getOne(queryWrapper);
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
        this.removeByIds(idSet);
        // 逻辑删除
        //this.update(Wrappers.lambdaUpdate(${entityName}.class).in(${entityName}::getId, idSet).set(${entityName}::getDeleteFlag, 1));
    }

    /**
     * 实体对象生成展示对象 entity -> vo
     */
    private ${entityName}VO build${entityName}VO(${entityName} entity) {
        if (entity == null) {
            return null;
        }
        ${entityName}VO vo = new ${entityName}VO();
    <#list fieldList as fieldConfig>
        vo.set${fieldConfig.fieldName?cap_first}(entity.get${fieldConfig.fieldName?cap_first}());
    </#list>
        return vo;
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<${entityName}VO> build${entityName}VOList(List<${entityName}> entityList) {
        List<${entityName}VO> voList = new ArrayList<>();
        if(CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (${entityName} entity : entityList) {
            voList.add(build${entityName}VO(entity));
        }
        return voList;
    }
}
