package com.moyu.boot.mapper;

import com.moyu.boot.BaseTest;
import com.moyu.boot.system.model.entity.SysOrg;
import com.moyu.boot.system.mapper.SysOrgMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @author shisong
 * @since 2024-11-26
 */
@Slf4j
class SysOrgMapperTest extends BaseTest {

    @Resource
    private SysOrgMapper sysOrgMapper;

    @Test
    public void testSelect() {
        SysOrg org = sysOrgMapper.selectById(1);
        log.info("查询结果:{}", org);
    }

}