package com.moyu.boot.plugin.intradayId.service.impl;

import com.moyu.boot.plugin.intradayId.service.IntradayIdService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis的日内标识生成服务(单日维度生成连续编号)
 *
 * @author shisong
 * @since 2026-01-29
 */
@Component
public class IntradayIdServiceImpl implements IntradayIdService {

    // 固定前缀
    private static final String INTRADAY_SEQ = "intraday:seq:";

    // 日期格式
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 注入自定义配置的 RedisTemplate
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String nextId() {
        String today = DTF.format(LocalDate.now());
        String key = INTRADAY_SEQ + today;
        // Redis 原子递增（初始值为 0，第一次递增后返回 1，后续依次+1）
        Long increment = redisTemplate.opsForValue().increment(key, 1);
        // 设置过期时间（仅第一次递增时设置，避免重复设置）
        if (increment != null && increment == 1) {
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }
        // 格式化补零：不足指定长度左侧补 0
        return today + String.format("%04d", increment);
    }

    @Override
    public String nextId(Integer size) {
        return "";
    }

    @Override
    public String nextId(String prefix) {
        return "";
    }

    @Override
    public String nextId(String prefix, Integer size) {
        return "";
    }
}
