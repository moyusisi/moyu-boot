package com.moyu.boot.plugin.codegen.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                // 关键词搜索(表表名、表描述)
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenConfig::getTableName, param.getSearchKey())
                .or()
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenConfig::getTableComment, param.getSearchKey())
                .orderByDesc(GenConfig::getUpdateTime);
        // 分页查询
        Page<GenConfig> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<GenConfig> rolePage = this.page(page, queryWrapper);
        return new PageData<>(rolePage.getTotal(), rolePage.getRecords());
    }

    @Override
    public GenConfigInfo configDetail(GenConfigParam param) {
        // 查询表生成配置
        LambdaQueryWrapper<GenConfig> queryWrapper = Wrappers.lambdaQuery(GenConfig.class)
                .eq(ObjectUtil.isNotEmpty(param.getId()), GenConfig::getId, param.getId())
                .eq(ObjectUtil.isNotEmpty(param.getTableName()), GenConfig::getTableName, param.getTableName());
        // id、tableName 均为唯一标识
        GenConfig genConfig = this.getOne(queryWrapper);
        if (genConfig == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        // 字段配置
        List<GenField> fieldList = genFieldService.list(Wrappers.lambdaQuery(GenField.class)
                .eq(GenField::getTableId, genConfig.getId()).orderByAsc(GenField::getFieldSort));
        // 组装VO
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
            genFieldList.add(genField);
        });
        genFieldService.saveOrUpdateBatch(genFieldList);
    }

    @Override
    @Transactional
    public void deleteByIds(GenConfigParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 删除实体配置
        this.removeByIds(idSet);
        // 删除字段配置
        genFieldService.remove(Wrappers.lambdaQuery(GenField.class).in(GenField::getTableId, idSet));
    }

    @Override
    public PageData<TableMetaData> tablePageList(GenConfigParam param) {
        Page<TableMetaData> page = new Page<>(param.getPageNum(), param.getPageSize());
        // 设置排除的表
        param.setExcludeTables(codegenProperties.getExcludeTables());
        //  分页查询
        Page<TableMetaData> tablePage = dataBaseMapper.getTablePage(page, param);
        return new PageData<>(tablePage.getTotal(), tablePage.getRecords());
    }

    @Override
    @Transactional
    public void importTable(Set<String> tableNameSet) {
        List<TableMetaData> tableList = dataBaseMapper.getTableListByNames(tableNameSet);
        if (CollectionUtils.isEmpty(tableList)) {
            return;
        }
        for (TableMetaData tableMetaData : tableList) {
            // 构造表配置
            GenConfig genConfig = buildGenTableConfig(tableMetaData);
            // 写入表配置
            boolean result = this.save(genConfig);
            if (result) {
                // 查询列元数据
                List<ColumnMetaData> columnList = dataBaseMapper.getTableColumns(tableMetaData.getTableName());
                // 列配置列表
                List<GenField> genFieldList = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(columnList)) {
                    for (ColumnMetaData tableColumn : columnList) {
                        // 构造列配置
                        GenField genField = buildGenFieldConfig(tableColumn);
                        genField.setTableId(genConfig.getId());
                        // 加入字段配置列表
                        genFieldList.add(genField);
                    }
                }
                // 写入字段配置
                genFieldService.saveBatch(genFieldList);
            }
        }

    }

    @Override
    public void syncTable(String tableName) {
        // tableName 均为唯一标识
        GenConfig old = this.getOne(Wrappers.lambdaQuery(GenConfig.class).eq(GenConfig::getTableName, tableName));
        // 表元数据
        TableMetaData tableMetaData = dataBaseMapper.getTableMetaData(tableName);
        // 列元数据
        List<ColumnMetaData> columnList = dataBaseMapper.getTableColumns(tableMetaData.getTableName());
        if (old == null || CollectionUtils.isEmpty(columnList)) {
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, "同步数据失败，表结构已不存在");
        }
        // 构造表配置
        GenConfig genConfig = buildGenTableConfig(tableMetaData);
        genConfig.setId(old.getId());
        // 更新表配置
        boolean result = this.updateById(genConfig);
        if (result) {
            // 列配置列表
            List<GenField> genFieldList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(columnList)) {
                for (ColumnMetaData tableColumn : columnList) {
                    // 构造列配置
                    GenField genField = buildGenFieldConfig(tableColumn);
                    genField.setTableId(genConfig.getId());
                    // 加入字段配置列表
                    genFieldList.add(genField);
                }
            }
            // 删除字段配置
            genFieldService.remove(Wrappers.lambdaQuery(GenField.class).eq(GenField::getTableId, genConfig.getId()));
            // 写入新字段配置
            genFieldService.saveBatch(genFieldList);
        }
    }

    @Override
    public Map<String, String> previewCode(GenConfigParam param) {
        // 查询表配置
        GenConfig genConfig = this.getOne(Wrappers.lambdaQuery(GenConfig.class).eq(GenConfig::getId, param.getId()));
        if (genConfig == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        return genCode(genConfig);
    }

    @Override
    public byte[] downloadZip(GenConfigParam param) {
        Assert.isTrue(param.getIds().size() <= 10, "下载内容过多，每次生成不能超过10个");
        // 查询表配置
        List<GenConfig> genConfigList = this.list(Wrappers.lambdaQuery(GenConfig.class).in(GenConfig::getId, param.getIds()));
        if (CollectionUtils.isEmpty(genConfigList)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "暂无数据");
        }
        // 创建一个字节输出流来存储ZIP文件
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (GenConfig genConfig : genConfigList) {
            Map<String, String> codeMap = genCode(genConfig);
            zipCode(genConfig, codeMap, zip);
        }
        try {
            // 完成所有文件的添加
            zip.finish();
            zip.close();
        } catch (IOException e) {
            log.error("生成文件失败", e);
        }
        return outputStream.toByteArray();
    }

    /**
     * 生成代码
     */
    private Map<String, String> genCode(@NotNull GenConfig genConfig) {
        // code代码map
        Map<String, String> codeMap = new LinkedHashMap<>();
        // 字段配置
        List<GenField> fieldList = genFieldService.list(Wrappers.lambdaQuery(GenField.class).eq(GenField::getTableId, genConfig.getId()));
        if (CollectionUtil.isEmpty(fieldList)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        // 获取所有模板文件名
        List<String> templateList = getTemplateList();
        // 组装模板中用到的变量
        Map<String, Object> bindMap = buildBindMap(genConfig, fieldList);
        // 自动根据用户引入的模板引擎库的jar来自动选择使用的引擎
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("", TemplateConfig.ResourceMode.CLASSPATH));
        // 遍历模板文件
        for (String templateName : templateList) {
            Template template = engine.getTemplate(templateName);
            // 模板渲染后的内容
            String content = template.render(bindMap);
            // 模板名
            String simpleName = templateName.replace(".ftl", "");
            simpleName = simpleName.substring(simpleName.lastIndexOf("/") + 1);
            codeMap.put(simpleName, content);
        }
        return codeMap;
    }

    /**
     * 将代码输出到zip流
     */
    private void zipCode(GenConfig genConfig, Map<String, String> codeMap, ZipOutputStream zip) {
        if (CollectionUtils.isEmpty(codeMap)) {
            return;
        }
        for (Map.Entry<String, String> entry : codeMap.entrySet()) {
            String fileName = getFullFileName(entry.getKey(), genConfig);
            try {
                // 添加文件到ZIP文件
                ZipEntry zipEntry = new ZipEntry(fileName);
                zip.putNextEntry(zipEntry);
                String code = entry.getValue();
                zip.write(code.getBytes(StandardCharsets.UTF_8));
                zip.flush();
                zip.closeEntry();
            } catch (IOException e) {
                log.error("生成文件失败，表名:" + genConfig.getTableName(), e);
            }
        }
    }

    /**
     * 根据表元数据构建表配置 (bo -> entity)
     */
    private GenConfig buildGenTableConfig(TableMetaData tableMetaData) {
        String tableName = tableMetaData.getTableName();
        // 生成默认的表配置
        GenConfig genConfig = new GenConfig();
        genConfig.setTableName(tableName);
        // 表注释作为业务名称，去掉表字 例如：用户表 -> 用户
        String tableComment = tableMetaData.getTableComment();
        if (ObjectUtil.isNotEmpty(tableComment)) {
            genConfig.setTableComment(tableComment);
            genConfig.setEntityDesc(tableComment.replace("表", "").trim());
        }
        //  根据表名生成实体类名 例如：sys_user -> SysUser
        // lower_underscore 转 UpperCamel
        genConfig.setEntityName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName));
        genConfig.setPackageName(codegenProperties.getPackageName());
        genConfig.setModuleName(codegenProperties.getModuleName());
        genConfig.setAuthor(codegenProperties.getAuthor());
        return genConfig;
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
        fieldConfig.setFieldRemark(columnMetaData.getColumnComment());
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
        fieldConfigVO.setFieldRemark(genField.getFieldRemark());
        fieldConfigVO.setFieldSort(genField.getFieldSort());
        fieldConfigVO.setMaxLength(genField.getMaxLength());
        fieldConfigVO.setRequired(genField.getRequired() == 1);
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
        genField.setFieldRemark(fieldConfigVO.getFieldRemark());
        genField.setFieldSort(fieldConfigVO.getFieldSort());
        genField.setMaxLength(fieldConfigVO.getMaxLength());
        genField.setRequired(Boolean.TRUE.equals(fieldConfigVO.getRequired()) ? 1 : 0);
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
        genConfigInfo.setTableComment(genConfig.getTableComment());
        genConfigInfo.setPackageName(genConfig.getPackageName());
        genConfigInfo.setModuleName(genConfig.getModuleName());
        genConfigInfo.setEntityName(genConfig.getEntityName());
        genConfigInfo.setEntityDesc(genConfig.getEntityDesc());
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
        genConfig.setTableComment(genConfigInfo.getTableComment());
        genConfig.setPackageName(genConfigInfo.getPackageName());
        genConfig.setModuleName(genConfigInfo.getModuleName());
        genConfig.setEntityName(genConfigInfo.getEntityName());
        genConfig.setEntityDesc(genConfigInfo.getEntityDesc());
        genConfig.setAuthor(genConfigInfo.getAuthor());
        return genConfig;
    }

    /**
     * 设置模板变量信息
     */
    public static Map<String, Object> buildBindMap(GenConfig genConfig, List<GenField> fieldList) {
        String packageName = genConfig.getPackageName();
        String moduleName = genConfig.getModuleName();
        String entityName = genConfig.getEntityName();
        String entityDesc = genConfig.getEntityDesc();

        Map<String, Object> bindMap = new HashMap<>();
        bindMap.put("packageName", packageName);
        bindMap.put("moduleName", moduleName);
        bindMap.put("tableName", genConfig.getTableName());
        bindMap.put("tableComment", genConfig.getTableComment());
        bindMap.put("entityName", entityName);
        bindMap.put("entityDesc", entityDesc);
        bindMap.put("author", genConfig.getAuthor());
        bindMap.put("fieldList", fieldList);
        return bindMap;
    }

    /**
     * 获取模板列表(从resources/templates开始的全路径)
     */
    private List<String> getTemplateList() {
        List<String> templates = new ArrayList<String>();
        templates.add("templates/java/entity.java.ftl");
        templates.add("templates/java/param.java.ftl");
        templates.add("templates/java/vo.java.ftl");
        templates.add("templates/java/mapper.java.ftl");
        templates.add("templates/java/service.java.ftl");
        templates.add("templates/java/serviceImpl.java.ftl");
        templates.add("templates/java/controller.java.ftl");
        templates.add("templates/xml/mapper.xml.ftl");
        templates.add("templates/sql/mysql.sql.ftl");
        templates.add("templates/js/api.js.ftl");
        templates.add("templates/vue/index.vue.ftl");
        templates.add("templates/vue/addForm.vue.ftl");
        templates.add("templates/vue/editForm.vue.ftl");
        return templates;
    }

    /**
     * 获取代码文件的全路径文件名
     */
    private String getFullFileName(String simpleName, GenConfig genConfig) {
        String className = genConfig.getEntityName();
        String packageName = genConfig.getPackageName();
        String moduleName = genConfig.getModuleName();
        String javaPath = "main/java/" + packageName.replace(".", "/") + "/" + moduleName;

        String fileName = "";

        if (simpleName.contains("controller.java")) {
            fileName = String.format("%s/controller/%sController.java", javaPath, className);
        } else if (simpleName.contains("service.java")) {
            fileName = String.format("%s/service/%sService.java", javaPath, className);
        } else if (simpleName.contains("serviceImpl.java")) {
            fileName = String.format("%s/service/impl/%sServiceImpl.java", javaPath, className);
        } else if (simpleName.contains("mapper.java")) {
            fileName = String.format("%s/mapper/%sMapper.java", javaPath, className);
        } else if (simpleName.contains("entity.java")) {
            fileName = String.format("%s/model/entity/%s.java", javaPath, className);
        } else if (simpleName.contains("param.java")) {
            fileName = String.format("%s/model/param/%sParam.java", javaPath, className);
        } else if (simpleName.contains("vo.java")) {
            fileName = String.format("%s/model/vo/%sVO.java", javaPath, className);
        } else if (simpleName.contains("mapper.xml")) {
            fileName = String.format("main/resources/mapper/%sMapper.xml", className);
        } else if (simpleName.contains("mysql.sql")) {
            fileName = "sql/mysql/mysql.sql";
        } else if (simpleName.contains("api.js")) {
            fileName = String.format("src/api/%s/%sApi.js", moduleName, className);
        } else if (simpleName.contains("index.vue")) {
            fileName = String.format("src/views/%s/%s/index.vue", moduleName, className);
        } else if (simpleName.contains("addForm.vue")) {
            fileName = String.format("src/views/%s/%s/addForm.vue", moduleName, className);
        } else if (simpleName.contains("editForm.vue")) {
            fileName = String.format("src/views/%s/%s/editForm.vue", moduleName, className);
        }
        return fileName;
    }

}
