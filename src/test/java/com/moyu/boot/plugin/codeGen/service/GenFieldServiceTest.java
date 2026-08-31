package com.moyu.boot.plugin.codeGen.service;

import com.moyu.boot.BaseTest;
import com.moyu.boot.plugin.codeGen.model.entity.GenField;
import com.mybatisflex.core.query.QueryWrapper;
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
        List<GenField> fieldConfigList = genFieldService.list(QueryWrapper.create()
                .eq(GenField::getTableId, 1L)
                .orderBy(GenField::getFieldSort, true)
        );
        log.info("查询结果:{}", fieldConfigList.size());
    }

}