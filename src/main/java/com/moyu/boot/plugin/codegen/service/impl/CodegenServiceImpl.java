package com.moyu.boot.plugin.codegen.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codegen.controller.config.CodegenProperties;
import com.moyu.boot.plugin.codegen.mapper.DbTableMapper;
import com.moyu.boot.plugin.codegen.model.param.TableQueryParam;
import com.moyu.boot.plugin.codegen.model.vo.TableVO;
import com.moyu.boot.plugin.codegen.service.CodegenService;
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
public class CodegenServiceImpl implements CodegenService {

    @Resource
    private DbTableMapper dbTableMapper;

    @Resource
    private CodegenProperties codegenProperties;

    @Override
    public PageData<TableVO> tablePageList(TableQueryParam param) {
        Page<TableVO> page = new Page<>(param.getPageNum(), param.getPageSize());
        // 设置排除的表
        param.setExcludeTables(codegenProperties.getExcludeTables());

        return dbTableMapper.getTablePage(page, param);
    }
}
