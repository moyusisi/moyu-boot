package com.moyu.boot.plugin.codegen.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.CaseFormat;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codegen.config.CodegenProperties;
import com.moyu.boot.plugin.codegen.enums.FormTypeEnum;
import com.moyu.boot.plugin.codegen.enums.JavaTypeEnum;
import com.moyu.boot.plugin.codegen.enums.QueryTypeEnum;
import com.moyu.boot.plugin.codegen.mapper.DataBaseMapper;
import com.moyu.boot.plugin.codegen.model.entity.GenField;
import com.moyu.boot.plugin.codegen.model.entity.GenConfig;
import com.moyu.boot.plugin.codegen.model.param.TableQueryParam;
import com.moyu.boot.plugin.codegen.model.vo.ColumnMetaData;
import com.moyu.boot.plugin.codegen.model.vo.GenConfigInfo;
import com.moyu.boot.plugin.codegen.model.vo.TableMetaData;
import com.moyu.boot.plugin.codegen.service.CodegenService;
import com.moyu.boot.plugin.codegen.service.GenConfigService;
import com.moyu.boot.plugin.codegen.service.GenFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 代码生成器服务类实现
 *
 * @author shisong
 * @since 2025-09-14
 */
@Slf4j
@Service
public class CodegenServiceImpl implements CodegenService {

    @Resource
    private DataBaseMapper dataBaseMapper;

    @Resource
    private GenConfigService genConfigService;

    @Resource
    private GenFieldService genFieldService;

    @Resource
    private CodegenProperties codegenProperties;

    @Override
    public PageData<TableMetaData> tablePageList(TableQueryParam param) {
        Page<TableMetaData> page = new Page<>(param.getPageNum(), param.getPageSize());
        // 设置排除的表
        param.setExcludeTables(codegenProperties.getExcludeTables());
        //  分页查询
        Page<TableMetaData> tablePage = dataBaseMapper.getTablePage(page, param);
        return new PageData<>(tablePage.getTotal(), tablePage.getRecords());
    }

    @Override
    public GenConfigInfo configDetail(String tableName) {
        // 查询表生成配置
        GenConfig genConfig = genConfigService.getOne(Wrappers.lambdaQuery(GenConfig.class).eq(GenConfig::getTableName, tableName));

        // 若无配置，则根据表的元数据生成默认配置(仅生成，不保存)
        if (genConfig == null) {
            TableMetaData tableMetadata = dataBaseMapper.getTableMetadata(tableName);
            Assert.isTrue(tableMetadata != null, "未找到表元数据");
            // 生成默认的表配置
            genConfig = new GenConfig();
            genConfig.setTableName(tableName);
            // 表注释作为业务名称，去掉表字 例如：用户表 -> 用户
            String tableComment = tableMetadata.getTableComment();
            if (ObjectUtil.isNotEmpty(tableComment)) {
                genConfig.setBusinessName(tableComment.replace("表", "").trim());
            }
            //  根据表名生成实体类名 例如：sys_user -> SysUser
            // lower_underscore 转 UpperCamel
            genConfig.setEntityName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName));
            genConfig.setPackageName(codegenProperties.getPackageName());
            genConfig.setModuleName(codegenProperties.getModuleName());
            genConfig.setAuthor(codegenProperties.getAuthor());
        }

        // 字段配置，优先查库，无则生成
        List<GenField> fieldConfigList = new ArrayList<>();

        // 获取表的列
        List<ColumnMetaData> tableColumnList = dataBaseMapper.getTableColumns(tableName);
        if (CollectionUtil.isNotEmpty(tableColumnList)) {
            for (ColumnMetaData tableColumn : tableColumnList) {
                // 查询db中的字段生成配置
                List<GenField> genFieldList = genFieldService.list(Wrappers.lambdaQuery(GenField.class)
                        .eq(GenField::getTableId, genConfig.getId())
                        .orderByAsc(GenField::getFieldSort)
                );

                // 优先取db中存的，无则新生成默认字段配置
                String columnName = tableColumn.getColumnName();
                GenField fieldConfig = genFieldList.stream()
                        .filter(item -> Objects.equals(item.getColumnName(), columnName))
                        .findFirst().orElseGet(() -> buildGenFieldConfig(tableColumn));
                // 加入字段配置列表
                fieldConfigList.add(fieldConfig);
            }
        }
        GenConfigInfo genConfigInfo = buildGenConfigInfo(genConfig);
        genConfigInfo.setFieldConfigList(fieldConfigList);
        return genConfigInfo;
    }

    @Override
    public void saveConfig(GenConfigInfo genConfigInfo) {
        // 新生成的表配置
        GenConfig genConfig = buildGenTable(genConfigInfo);
        genConfigService.saveOrUpdate(genConfig);

        List<GenField> genFieldList = genConfigInfo.getFieldConfigList();
        if (CollectionUtil.isEmpty(genFieldList)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "字段配置不能为空");
        }
        genFieldList.forEach(genFieldConfig -> {
            genFieldConfig.setTableId(genConfig.getId());
        });
        genFieldService.saveOrUpdateBatch(genFieldList);
    }

    @Override
    public void deleteConfig(String tableName) {
        GenConfig genConfig = genConfigService.getOne(Wrappers.lambdaQuery(GenConfig.class).eq(GenConfig::getTableName, tableName));
        Assert.isTrue(genConfig != null, "未找到相关配置");
        boolean result = genConfigService.removeById(genConfig.getId());
        if (result) {
            genFieldService.remove(Wrappers.lambdaQuery(GenField.class).eq(GenField::getTableId, genConfig.getId()));
        }
    }

    /**
     * 根据列元数据构建字段配置
     */
    private GenField buildGenFieldConfig(ColumnMetaData columnMetaData) {
        GenField fieldConfig = new GenField();
        fieldConfig.setColumnName(columnMetaData.getColumnName());
        fieldConfig.setColumnType(columnMetaData.getDataType());
        // 字段名
        fieldConfig.setFieldName(StrUtil.toCamelCase(columnMetaData.getColumnName()));
        // 字段类型
        fieldConfig.setFieldType(JavaTypeEnum.getByColumnType(fieldConfig.getColumnType()));
        fieldConfig.setFieldComment(columnMetaData.getColumnComment());
        fieldConfig.setMaxLength(columnMetaData.getMaxLength());
        // 必填和允许为空反着
        fieldConfig.setRequired(columnMetaData.getNullable() == 1 ? 0 : 1);

        fieldConfig.setShowInList(1);
        fieldConfig.setShowInForm(1);
        fieldConfig.setShowInQuery(0);
        // formType
        if (fieldConfig.getColumnType().equals("date")) {
            fieldConfig.setFormType(FormTypeEnum.DATE.name());
        } else if (fieldConfig.getColumnType().equals("datetime")) {
            fieldConfig.setFormType(FormTypeEnum.DATE_TIME.name());
        } else {
            fieldConfig.setFormType(FormTypeEnum.INPUT.name());
        }
        // queryType
        fieldConfig.setQueryType(QueryTypeEnum.EQ.name());

        return fieldConfig;
    }

    /**
     * 根据配置生成配置信息
     */
    private GenConfigInfo buildGenConfigInfo(GenConfig genConfig) {
        if (genConfig == null) {
            return null;
        }
        GenConfigInfo genConfigInfo = new GenConfigInfo();
        genConfigInfo.setId(genConfig.getId());
        genConfigInfo.setTableName(genConfig.getTableName());
        genConfigInfo.setPackageName(genConfig.getPackageName());
        genConfigInfo.setModuleName(genConfig.getModuleName());
        genConfigInfo.setEntityName(genConfig.getEntityName());
        genConfigInfo.setBusinessName(genConfig.getBusinessName());
        genConfigInfo.setAuthor(genConfig.getAuthor());
//        genConfigInfo.setGenFieldConfigList(genTable.getGenFieldConfigList());
        return genConfigInfo;
    }

    /**
     * 生成代码配置表
     */
    private GenConfig buildGenTable(GenConfigInfo genConfigInfo) {
        if (genConfigInfo == null) {
            return null;
        }
        GenConfig genConfig = new GenConfig();
        genConfig.setId(genConfigInfo.getId());
        genConfig.setTableName(genConfigInfo.getTableName());
        genConfig.setPackageName(genConfigInfo.getPackageName());
        genConfig.setModuleName(genConfigInfo.getModuleName());
        genConfig.setEntityName(genConfigInfo.getEntityName());
        genConfig.setBusinessName(genConfigInfo.getBusinessName());
        genConfig.setAuthor(genConfigInfo.getAuthor());
        return genConfig;
    }
}
