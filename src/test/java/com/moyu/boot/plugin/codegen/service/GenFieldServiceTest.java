package com.moyu.boot.plugin.codegen.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.moyu.boot.BaseTest;
import com.moyu.boot.plugin.codegen.model.entity.GenField;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shisong
 * @since 2025-09-17
 */
@Slf4j
class GenFieldServiceTest extends BaseTest {

    @Resource
    private GenFieldService genFieldService;

    @Test
    public void testSelect() {
        List<GenField> fieldConfigList = genFieldService.list(Wrappers.lambdaQuery(GenField.class)
                .eq(GenField::getTableId, 1L)
                .orderByAsc(GenField::getFieldSort)
        );
        log.info("查询结果:{}", fieldConfigList.size());
    }

}