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

    @Resource
    private ${entityName}Service ${entityName?uncap_first}Service;

    @Override
    public List<${entityName}VO> list(${entityName}Param param) {

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
            .like(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'EQ'>
            .eq(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'GT'>
            .gt(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'GE'>
            .ge(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'LT'>
            .lt(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'LE'>
            .le(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'NE'>
            .ne(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            <#elseif fieldConfig.queryType == 'BETWEEN'>
            .between(ObjectUtil.isAllNotEmpty(param.get${fieldConfig.fieldName?cap_first}Range().get(0), param.get${fieldConfig.fieldName?cap_first}Range().get(1)),
                    ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}Range().get(0), param.get${fieldConfig.fieldName?cap_first}Range().get(1))
            <#elseif fieldConfig.queryType == 'IN'>
            .in(ObjectUtil.isNotEmpty(param.get${fieldConfig.fieldName?cap_first}), ${entityName}::get${fieldConfig.fieldName?cap_first}, param.get${fieldConfig.fieldName?cap_first}())
            </#if>
        </#if>
    </#list>
            // TODO 是否需要排序
            .orderByDesc(${entityName}::getUpdateTime);
</#if>
    }

    @Override
    public ${entityName}VO detail(${entityName}Param param) {

    }

    @Override
    public void add(${entityName}Param param) {

    }

    @Override
    public void update(${entityName}Param param) {

    }

    @Override
    public void deleteByIds(${entityName}Param param) {

    }
}

