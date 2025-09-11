package com.moyu.boot;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 集成测试基础类
 *
 * @author shisong
 * @since 2024-11-25
 */
@Slf4j
@SpringBootTest(classes = SystemApplication.class)
public class BaseTest {

    @Test
    public void test() {
        log.info("测试基类正确执行，不要改");
    }
}
