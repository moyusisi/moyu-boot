package com.moyu.boot.plugin.codeGen.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.CaseFormat;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import com.moyu.boot.plugin.codeGen.config.CodegenProperties;
import com.moyu.boot.plugin.codeGen.enums.FormTypeEnum;
import com.moyu.boot.plugin.codeGen.enums.JavaTypeEnum;
import com.moyu.boot.plugin.codeGen.enums.QueryTypeEnum;
import com.moyu.boot.plugin.codeGen.mapper.DataBaseMapper;
import com.moyu.boot.plugin.codeGen.mapper.GenConfigMapper;
import com.moyu.boot.plugin.codeGen.model.bo.ColumnMetaData;
import com.moyu.boot.plugin.codeGen.model.entity.GenConfig;
import com.moyu.boot.plugin.codeGen.model.entity.GenField;
import com.moyu.boot.plugin.codeGen.model.param.GenConfigParam;
import com.moyu.boot.plugin.codeGen.model.vo.CodePreviewVO;
import com.moyu.boot.plugin.codeGen.model.vo.FieldConfigVO;
import com.moyu.boot.plugin.codeGen.model.vo.GenConfigInfo;
import com.moyu.boot.plugin.codeGen.model.vo.TableMetaData;
import com.moyu.boot.plugin.codeGen.service.GenConfigService;
import com.moyu.boot.plugin.codeGen.service.GenFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
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

    @Resource
    private TransactionTemplate transactionTemplate;

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
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
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
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "字段配置不能为空");
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
    public void importSql(String sql) {
        // 校验格式
        List<SQLStatement> statementList = new ArrayList<>();
        try {
            Assert.notEmpty(sql, "SQL不能为空");
            statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);
        } catch (Exception e) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR.getCode(), "SQL语句语法错误：" + e.getMessage());
        }
        List<MySqlCreateTableStatement> createStatementList;
        if (CollectionUtils.isEmpty(statementList)) {
            createStatementList = new ArrayList<>();
        } else {
            createStatementList = statementList.stream().filter(statement -> statement instanceof MySqlCreateTableStatement).map(e -> (MySqlCreateTableStatement) e).collect(Collectors.toList());
        }
        Assert.isTrue(!createStatementList.isEmpty() && createStatementList.size() <= 10, "每次只能处理1~10个建表语句");
        // 生成并保存配置, 放在一个事物中
        transactionTemplate.execute((transactionStatus) -> {
            // 遍历建表语句生成代码代码配置保存到db
            for (MySqlCreateTableStatement createTableStatement : createStatementList) {
                // 表配置信息
                GenConfig genConfig = parseTable(createTableStatement);
                try {
                    // 先保存 genConfig，生成id后才能setTableId
                    this.save(genConfig);
                } catch (DuplicateKeyException e) {
                    String detail = String.format("表%s的配置已存在，表名不能重复", genConfig.getTableName());
                    throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, detail);
                }
                Long tableId = genConfig.getId();
                Assert.notNull(tableId, "从sql导入时，生成tableId失败");
                List<GenField> fieldList = parseFieldList(createTableStatement, tableId);
                genFieldService.saveBatch(fieldList);
                // 保存代码生成配置
            }
            return null;
        });

    }

    @Override
    public void resetTable(GenConfigParam param) {
        GenConfig old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.TABLE_NOT_EXIST, "同步数据失败，表结构已不存在");
        }
        // 通过表名获取表元数据
        TableMetaData tableMetaData = dataBaseMapper.getTableMetaData(old.getTableName());
        // 列元数据
        List<ColumnMetaData> columnList = dataBaseMapper.getTableColumns(old.getTableName());
        if (CollectionUtils.isEmpty(columnList)) {
            throw new BusinessException(ResultCodeEnum.TABLE_NOT_EXIST, "同步数据失败，表结构已不存在");
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
    public List<CodePreviewVO> previewCode(GenConfigParam param) {
        // 查询表配置
        GenConfig genConfig = this.getOne(Wrappers.lambdaQuery(GenConfig.class).eq(GenConfig::getId, param.getId()));
        if (genConfig == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        return genCodeList(genConfig);
    }

    @Override
    public byte[] downloadZip(GenConfigParam param) {
        Assert.isTrue(param.getIds().size() <= 10, "下载内容过多，每次生成不能超过10个");
        // 查询表配置
        List<GenConfig> genConfigList = this.list(Wrappers.lambdaQuery(GenConfig.class).in(GenConfig::getId, param.getIds()));
        if (CollectionUtils.isEmpty(genConfigList)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "暂无数据");
        }
        // 创建一个字节输出流来存储ZIP文件
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (GenConfig genConfig : genConfigList) {
            List<CodePreviewVO> codeList = genCodeList(genConfig);
            zipCode(codeList, zip);
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
    private List<CodePreviewVO> genCodeList(@NotNull GenConfig genConfig) {
        // code代码列表
        List<CodePreviewVO> codeList = new ArrayList<>();
        // 字段配置
        List<GenField> fieldList = genFieldService.list(Wrappers.lambdaQuery(GenField.class).eq(GenField::getTableId, genConfig.getId()));
        if (CollectionUtil.isEmpty(fieldList)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 获取所有模板文件名
        Map<String, String> templateMap = getTemplateMap();
        // 组装模板中用到的变量
        Map<String, Object> bindMap = buildBindMap(genConfig, fieldList);
        // 自动根据用户引入的模板引擎库的jar来自动选择使用的引擎
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("", TemplateConfig.ResourceMode.CLASSPATH));
        // 遍历模板文件
        for (Map.Entry<String, String> entry : templateMap.entrySet()) {
            // 获取模板并渲染
            Template template = engine.getTemplate(entry.getValue());
            String content = template.render(bindMap);
            // 构造 CodePreviewVO
            CodePreviewVO vo = new CodePreviewVO();
            vo.setCodeKey(entry.getKey());
            vo.setContent(content);
            fillCodePreview(vo, genConfig);
            codeList.add(vo);
        }
        return codeList;
    }

    /**
     * 生成codePreviewVO
     */
    private List<CodePreviewVO> genCodePreviewList(GenConfig genConfig, Map<String, String> codeMap) {
        List<CodePreviewVO> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(codeMap)) {
            return list;
        }
        for (Map.Entry<String, String> entry : codeMap.entrySet()) {
            CodePreviewVO vo = new CodePreviewVO();
            vo.setCodeKey(entry.getKey());
            vo.setContent(entry.getValue());
            fillCodePreview(vo, genConfig);
            list.add(vo);
        }
        return list;
    }

    /**
     * 填充CodePreviewVO的字段
     */
    private void fillCodePreview(CodePreviewVO vo, GenConfig genConfig) {
        String entityName = genConfig.getEntityName();
        String packageName = genConfig.getPackageName();
        String moduleName = genConfig.getModuleName();
        String packagePath = (packageName + "." + moduleName).replace(".", File.separator);

        String codeKey = vo.getCodeKey();
        if (codeKey.contains("controller.java")) {
            vo.setCodeType("backend");
            vo.setPath(String.format("src/main/java/%s/controller", packagePath));
            vo.setFileName(String.format("%sController.java", entityName));
        } else if (codeKey.contains("service.java")) {
            vo.setCodeType("backend");
            vo.setPath(String.format("src/main/java/%s/service", packagePath));
            vo.setFileName(String.format("%sService.java", entityName));
        } else if (codeKey.contains("serviceImpl.java")) {
            vo.setCodeType("backend");
            vo.setPath(String.format("src/main/java/%s/service/impl", packagePath));
            vo.setFileName(String.format("%sServiceImpl.java", entityName));
        } else if (codeKey.contains("mapper.java")) {
            vo.setCodeType("backend");
            vo.setPath(String.format("src/main/java/%s/mapper", packagePath));
            vo.setFileName(String.format("%sMapper.java", entityName));
        } else if (codeKey.contains("entity.java")) {
            vo.setCodeType("backend");
            vo.setPath(String.format("src/main/java/%s/model/entity", packagePath));
            vo.setFileName(String.format("%s.java", entityName));
        } else if (codeKey.contains("param.java")) {
            vo.setCodeType("backend");
            vo.setPath(String.format("src/main/java/%s/model/param", packagePath));
            vo.setFileName(String.format("%sParam.java", entityName));
        } else if (codeKey.contains("vo.java")) {
            vo.setCodeType("backend");
            vo.setPath(String.format("src/main/java/%s/model/vo", packagePath));
            vo.setFileName(String.format("%sVO.java", entityName));
        } else if (codeKey.contains("mapper.xml")) {
            vo.setCodeType("backend");
            vo.setPath(String.format("src/main/resources/mapper/%s", moduleName));
            vo.setFileName(String.format("%sMapper.xml", entityName));
        } else if (codeKey.contains("mysql.sql")) {
            vo.setCodeType("backend");
            vo.setPath(String.format("src/main/sql/%s", moduleName));
            vo.setPath("src/main/sql");
            vo.setFileName(String.format("%s.sql", StrUtil.lowerFirst(entityName)));
        } else if (codeKey.contains("api.js")) {
            vo.setCodeType("frontend");
            vo.setPath(String.format("src/api/%s", moduleName));
            vo.setFileName(String.format("%sApi.js", StrUtil.lowerFirst(entityName)));
        } else if (codeKey.contains("index.vue")) {
            vo.setCodeType("frontend");
            vo.setPath(String.format("src/views/%s/%s", moduleName, StrUtil.lowerFirst(entityName)));
            vo.setFileName("index.vue");
        } else if (codeKey.contains("detail.vue")) {
            vo.setCodeType("frontend");
            vo.setPath(String.format("src/views/%s/%s", moduleName, StrUtil.lowerFirst(entityName)));
            vo.setFileName("detail.vue");
        } else if (codeKey.contains("form.vue")) {
            vo.setCodeType("frontend");
            vo.setPath(String.format("src/views/%s/%s", moduleName, StrUtil.lowerFirst(entityName)));
            vo.setFileName("form.vue");
        }
    }

    /**
     * 将代码输出到zip流
     */
    private void zipCode(List<CodePreviewVO> codeList, ZipOutputStream zip) {
        if (CollectionUtils.isEmpty(codeList)) {
            return;
        }
        for (CodePreviewVO codeVO : codeList) {
            String fileName = codeVO.getCodeType() + File.separator + codeVO.getPath() + File.separator + codeVO.getFileName();
            try {
                // 添加文件到ZIP文件
                ZipEntry zipEntry = new ZipEntry(fileName);
                zip.putNextEntry(zipEntry);
                String code = codeVO.getContent();
                zip.write(code.getBytes(StandardCharsets.UTF_8));
                zip.flush();
                zip.closeEntry();
            } catch (IOException e) {
                log.error("生成代码文件Zip失败", e);
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "生成代码文件Zip失败");
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
        // 表注释作为实体描述，去掉表字 例如：用户表 -> 用户
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
        genConfig.setSourceType("TABLE");
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
        if ("deleted".equals(fieldConfig.getFieldName())) {
            fieldConfig.setShowInList(0);
        } else {
            fieldConfig.setShowInList(1);
        }
        if (BaseEntity.baseFieldSet.contains(fieldConfig.getFieldName())) {
            fieldConfig.setShowInForm(0);
        } else {
            fieldConfig.setShowInForm(1);
        }
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
        fieldConfigVO.setEllipsis(genField.getEllipsis() == 1);
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

    /**
     * 字段配置 vo -> entity
     */
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
        genField.setEllipsis(Boolean.TRUE.equals(fieldConfigVO.getEllipsis()) ? 1 : 0);
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
        genConfigInfo.setParentMenuCode(genConfig.getParentMenuCode());
        genConfigInfo.setAuthor(genConfig.getAuthor());
        genConfigInfo.setSourceType(genConfig.getSourceType());
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
        genConfig.setParentMenuCode(genConfigInfo.getParentMenuCode());
        genConfig.setAuthor(genConfigInfo.getAuthor());
        genConfig.setSourceType(genConfigInfo.getSourceType());
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
        bindMap.put("parentMenuCode", StrUtil.nullToEmpty(genConfig.getParentMenuCode()));
        bindMap.put("author", genConfig.getAuthor());
        bindMap.put("fieldList", fieldList);
        return bindMap;
    }

    /**
     * 获取模板资源(在resources中的文件)
     */
    private Map<String, String> getTemplateMap() {
        Map<String, String> templateMap = new LinkedHashMap<>();
        templateMap.put("entity.java", "templates/java/entity.java.ftl");
        templateMap.put("param.java", "templates/java/param.java.ftl");
        templateMap.put("vo.java", "templates/java/vo.java.ftl");
        templateMap.put("mapper.java", "templates/java/mapper.java.ftl");
        templateMap.put("service.java", "templates/java/service.java.ftl");
        templateMap.put("serviceImpl.java", "templates/java/serviceImpl.java.ftl");
        templateMap.put("controller.java", "templates/java/controller.java.ftl");
        templateMap.put("mapper.xml", "templates/xml/mapper.xml.ftl");
        templateMap.put("mysql.sql", "templates/sql/mysql.sql.ftl");
        templateMap.put("api.js", "templates/js/api.js.ftl");
        templateMap.put("index.vue", "templates/vue/index.vue.ftl");
        templateMap.put("detail.vue", "templates/vue/detail.vue.ftl");
        templateMap.put("form.vue", "templates/vue/form.vue.ftl");
        return templateMap;
    }

    /**
     * 解析创建表语句获取生成配置 sql -> entity
     */
    private GenConfig parseTable(MySqlCreateTableStatement createTableStatement) {
        // 生成默认的表配置
        GenConfig genConfig = new GenConfig();
        String tableName = removeQuotes(createTableStatement.getName().getSimpleName());
        genConfig.setTableName(tableName);
        // 表注释作为实体描述，去掉表字 例如：用户表 -> 用户
        String tableComment = removeQuotes(createTableStatement.getComment().toString());
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
        genConfig.setSourceType("SQL");
        return genConfig;
    }

    /**
     * 通过表创建语句获取字段配置列表 sql -> entity
     */
    private List<GenField> parseFieldList(MySqlCreateTableStatement createTableStatement, Long tableId) {
        // 列信息
        List<GenField> fieldList = new ArrayList<>();
        // 遍历表的元素列表，如果元素是SQLColumnDefinition类型，则获取列的名称、类型和注释。
        createTableStatement.getTableElementList().forEach(tableElement -> {
            if (tableElement instanceof SQLColumnDefinition) {
                SQLColumnDefinition columnDefinition = (SQLColumnDefinition) tableElement;
                // 列信息
                GenField fieldConfig = new GenField();
                fieldConfig.setTableId(tableId);
                fieldConfig.setColumnName(removeQuotes(columnDefinition.getName().getSimpleName()));
                fieldConfig.setColumnType(columnDefinition.getDataType().getName());
                // 字段名
                fieldConfig.setFieldName(StrUtil.toCamelCase(fieldConfig.getColumnName()));
                // 字段类型
                fieldConfig.setFieldType(JavaTypeEnum.getByColumnType(StrUtil.toLowerCase(fieldConfig.getColumnType())));
                if (ObjectUtil.isNotEmpty(columnDefinition.getComment())) {
                    fieldConfig.setFieldRemark(removeQuotes(columnDefinition.getComment().toString()));
                }
                // 默认非必填
                fieldConfig.setRequired(0);
                if ("deleted".equals(fieldConfig.getFieldName())) {
                    fieldConfig.setShowInList(0);
                } else {
                    fieldConfig.setShowInList(1);
                }
                if (BaseEntity.baseFieldSet.contains(fieldConfig.getFieldName())) {
                    fieldConfig.setShowInForm(0);
                } else {
                    fieldConfig.setShowInForm(1);
                }
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
                fieldList.add(fieldConfig);
            }
        });
        return fieldList;
    }

    /**
     * 去掉引号和反引号
     */
    private String removeQuotes(String str) {
//        return SQLUtils.normalize(str);
        if (str != null) {
            str = str.replace("`", "").replace("'", "").replace("\"", "");
        }
        return str;
    }

}
