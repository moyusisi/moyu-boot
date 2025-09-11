package com.moyu.boot;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.base.CaseFormat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 本地测试类
 *
 * @author shisong
 * @since 2024-11-25
 */
@Slf4j
public class UnitTest {

    @Test
    public void test() {
        log.info("测试基类正确执行，不要改");
    }

    @Test
    public void testCamelCase() {
        String underscoreName = "Hello_world_DTO";
        String camelCaseName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, underscoreName);
        log.info(camelCaseName);
    }

    @SneakyThrows
    @Test
    public void testId() {
        // 24位: 67c7b60fd19001d4b33539b6
        log.info(IdUtil.objectId());
        // 20位随机NanoId: R06PP2RUZeS9j6g0bNxyW
        log.info(IdUtil.nanoId());
        // 指定位数: g4Pq_XFDRq
        log.info(IdUtil.nanoId(10));
        // 32位: 512e1c1d55b14cd2ad107e8150d0ac26
        log.info(IdUtil.simpleUUID());
        // 32位有横线: c577c28c-aa92-460f-a3c1-bfaa0c59d2ba
        log.info(IdUtil.fastUUID());
        // 19个数 Long: 1897111148578750464
        log.info("Long:{}", IdUtil.getSnowflakeNextId());
        // 随机: Jg3Q3QvK3V
        log.info(RandomUtil.randomString(10));
    }

}
