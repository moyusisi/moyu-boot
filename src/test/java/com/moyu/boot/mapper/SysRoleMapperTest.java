package com.moyu.boot.mapper;

import com.moyu.boot.BaseTest;
import com.moyu.boot.system.mapper.SysRoleMapper;
import com.moyu.boot.system.model.entity.SysRole;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @author shisong
 * @since 2024-11-26
 */
@Slf4j
class SysRoleMapperTest extends BaseTest {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Test
    public void testSelect() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(SysRole::getId, 1);
        queryWrapper.like(SysRole::getCode, "abc");
        SysRole account = sysRoleMapper.selectOneByQuery(queryWrapper);
        log.info("查询结果:{}", account);
        log.info("查询结果:{}", sysRoleMapper.selectAll());
    }

}