package com.moyu.boot.plugin.codegen.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.CaseFormat;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codegen.config.CodegenProperties;
import com.moyu.boot.plugin.codegen.enums.FormTypeEnum;
import com.moyu.boot.plugin.codegen.enums.JavaTypeEnum;
import com.moyu.boot.plugin.codegen.enums.QueryTypeEnum;
import com.moyu.boot.plugin.codegen.mapper.DataBaseMapper;
import com.moyu.boot.plugin.codegen.mapper.GenConfigMapper;
import com.moyu.boot.plugin.codegen.model.bo.ColumnMetaData;
import com.moyu.boot.plugin.codegen.model.entity.GenConfig;
import com.moyu.boot.plugin.codegen.model.entity.GenField;
import com.moyu.boot.plugin.codegen.model.param.GenConfigParam;
import com.moyu.boot.plugin.codegen.model.vo.FieldConfigVO;
import com.moyu.boot.plugin.codegen.model.vo.GenConfigInfo;
import com.moyu.boot.plugin.codegen.model.vo.TableMetaData;
import com.moyu.boot.plugin.codegen.service.GenConfigService;
import com.moyu.boot.plugin.codegen.service.GenFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 针对表【gen_config(代码生成实体配置表)】的数据库操作Service实现
 *
 * @author shisong
 * @since 2025-09-14
 */
@Slf4j
@Service
public class GenConfigServiceImpl extends ServiceImpl<GenConfigMapper, GenConfig> implements GenConfigService {

    @Resource
    private DataBaseMapper dataBaseMapper;

    @Resource
    private GenFieldService genFieldService;

    @Resource
    private CodegenProperties codegenProperties;

    @Override
    public PageData<GenConfig> pageList(GenConfigParam param) {
        // 查询条件
        LambdaQueryWrapper<GenConfig> queryWrapper = Wrappers.lambdaQuery(GenConfig.class)
                // 关键词搜索(表名、业务名)
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenConfig::getTableName, param.getSearchKey())
                .or()
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenConfig::getBusinessName, param.getSearchKey())
                .orderByAsc(GenConfig::getUpdateTime);
        // 分页查询
        Page<GenConfig> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<GenConfig> rolePage = this.page(page, queryWrapper);
        return new PageData<>(rolePage.getTotal(), rolePage.getRecords());
    }

    @Override
    public GenConfigInfo getConfigDetail(String tableName) {
        // 查询表生成配置
        GenConfig genConfig = this.getOne(Wrappers.lambdaQuery(GenConfig.class).eq(GenConfig::getTableName, tableName));

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
        List<GenField> fieldList = new ArrayList<>();

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
                GenField genField = genFieldList.stream()
                        .filter(item -> Objects.equals(item.getColumnName(), columnName))
                        .findFirst().orElseGet(() -> buildGenFieldConfig(tableColumn));
                // 加入字段配置列表
                fieldList.add(genField);
            }
        }
        return buildGenConfigInfo(genConfig, fieldList);
    }

    @Override
    public void saveConfig(GenConfigInfo genConfigInfo) {
        List<FieldConfigVO> fieldConfigList = genConfigInfo.getFieldConfigList();
        if (CollectionUtil.isEmpty(fieldConfigList)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "字段配置不能为空");
        }

        // 新生成的表配置
        GenConfig genConfig = buildGenTable(genConfigInfo);
        this.saveOrUpdate(genConfig);

        // 组装实体
        List<GenField> genFieldList = new ArrayList<>();
        fieldConfigList.forEach(fieldConfigVO -> {
            GenField genField = buildGenFieldConfig(fieldConfigVO);
            genField.setTableId(genConfig.getId());
        });
        genFieldService.saveOrUpdateBatch(genFieldList);
    }

    @Override
    public void deleteConfig(String tableName) {
        GenConfig genConfig = this.getOne(Wrappers.lambdaQuery(GenConfig.class).eq(GenConfig::getTableName, tableName));
        Assert.isTrue(genConfig != null, "未找到相关配置");
        boolean result = this.removeById(genConfig.getId());
        if (result) {
            genFieldService.remove(Wrappers.lambdaQuery(GenField.class).eq(GenField::getTableId, genConfig.getId()));
        }
    }

    /**
     * 根据列元数据构建字段配置 (bo -> entity)
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
     * 字段配置实体 转换为 字段配置vo
     */
    private FieldConfigVO buildFieldConfigVO(GenField genField) {
        if (genField == null) {
            return null;
        }
        FieldConfigVO fieldConfigVO = new FieldConfigVO();
        fieldConfigVO.setId(genField.getId());
        fieldConfigVO.setTableId(genField.getTableId());
        fieldConfigVO.setColumnName(genField.getColumnName());
        fieldConfigVO.setColumnType(genField.getColumnType());
        fieldConfigVO.setFieldName(genField.getFieldName());
        fieldConfigVO.setFieldType(genField.getFieldType());
        fieldConfigVO.setFieldComment(genField.getFieldComment());
        fieldConfigVO.setFieldSort(genField.getFieldSort());
        fieldConfigVO.setMaxLength(genField.getMaxLength());
        fieldConfigVO.setRequired(genField.getRequired());
        fieldConfigVO.setShowInList(genField.getShowInList() == 1);
        fieldConfigVO.setShowInForm(genField.getShowInForm() == 1);
        fieldConfigVO.setShowInQuery(genField.getShowInQuery() == 1);
        fieldConfigVO.setFormType(genField.getFormType());
        fieldConfigVO.setQueryType(genField.getQueryType());
        fieldConfigVO.setDictType(genField.getDictType());
        fieldConfigVO.setCreateTime(genField.getCreateTime());
        fieldConfigVO.setUpdateTime(genField.getUpdateTime());
        return fieldConfigVO;
    }

    private GenField buildGenFieldConfig(FieldConfigVO fieldConfigVO) {
        if (fieldConfigVO == null) {
            return null;
        }
        GenField genField = new GenField();
        genField.setId(fieldConfigVO.getId());
        genField.setTableId(fieldConfigVO.getTableId());
        genField.setColumnName(fieldConfigVO.getColumnName());
        genField.setColumnType(fieldConfigVO.getColumnType());
        genField.setFieldName(fieldConfigVO.getFieldName());
        genField.setFieldType(fieldConfigVO.getFieldType());
        genField.setFieldComment(fieldConfigVO.getFieldComment());
        genField.setFieldSort(fieldConfigVO.getFieldSort());
        genField.setMaxLength(fieldConfigVO.getMaxLength());
        genField.setRequired(fieldConfigVO.getRequired());
        genField.setShowInList(Boolean.TRUE.equals(fieldConfigVO.getShowInList()) ? 1 : 0);
        genField.setShowInForm(Boolean.TRUE.equals(fieldConfigVO.getShowInForm()) ? 1 : 0);
        genField.setShowInQuery(Boolean.TRUE.equals(fieldConfigVO.getShowInQuery()) ? 1 : 0);
        genField.setFormType(fieldConfigVO.getFormType());
        genField.setQueryType(fieldConfigVO.getQueryType());
        genField.setDictType(fieldConfigVO.getDictType());
        return genField;
    }

    /**
     * 根据配置生成配置信息 entity -> vo
     */
    private GenConfigInfo buildGenConfigInfo(GenConfig genConfig, List<GenField> fieldList) {
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
        List<FieldConfigVO> fieldConfigList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(fieldList)) {
            for (GenField field : fieldList) {
                FieldConfigVO fieldConfigVO = buildFieldConfigVO(field);
                fieldConfigList.add(fieldConfigVO);
            }
        }
        genConfigInfo.setFieldConfigList(fieldConfigList);
        return genConfigInfo;
    }

    /**
     * 生成代码配置表 vo -> entity
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
