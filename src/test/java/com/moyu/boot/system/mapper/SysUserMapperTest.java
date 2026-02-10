package com.moyu.boot.system.mapper;

import com.moyu.boot.BaseTest;
import com.moyu.boot.system.model.entity.SysOrg;
import com.moyu.boot.system.model.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author shisong
 * @since 2026-02-10
 */
@Slf4j
class SysUserMapperTest extends BaseTest {

    @Autowired
    private SysUserMapper sysUserMapper;


    @Test
    public void testUpdate() {
        SysUser  sysUser = new SysUser();
        sysUser.setId(105L);
        sysUser.setPassword(null);
        sysUserMapper.updateById(sysUser);
    }

}