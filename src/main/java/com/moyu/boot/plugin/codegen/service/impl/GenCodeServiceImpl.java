package com.moyu.boot.plugin.codegen.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codegen.config.CodegenProperties;
import com.moyu.boot.plugin.codegen.mapper.DataBaseMapper;
import com.moyu.boot.plugin.codegen.model.param.TableQueryParam;
import com.moyu.boot.plugin.codegen.model.vo.TableMetaData;
import com.moyu.boot.plugin.codegen.service.GenCodeService;
import com.moyu.boot.plugin.codegen.service.GenConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 代码生成器服务类实现
 *
 * @author shisong
 * @since 2025-09-14
 */
@Slf4j
@Service
public class GenCodeServiceImpl implements GenCodeService {

    @Resource
    private DataBaseMapper dataBaseMapper;

    @Resource
    private GenConfigService genConfigService;

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

}
