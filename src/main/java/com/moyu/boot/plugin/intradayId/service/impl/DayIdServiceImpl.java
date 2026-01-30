package com.moyu.boot.plugin.intradayId.service.impl;

import cn.hutool.core.util.StrUtil;
import com.moyu.boot.plugin.intradayId.service.DayIdService;
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class DayIdServiceImpl implements DayIdService {

    // 固定前缀
    private static final String INTRADAY_SEQ = "day:seq:";

    // 日期格式
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String nextId() {
        String today = DTF.format(LocalDate.now());
        Long seq = generatorId(null, today);
        // 4. 格式化补零，返回日内标识
        return today + String.format("%04d", seq);
    }

    @Override
    public String nextId(Integer size) {
        String today = DTF.format(LocalDate.now());
        Long seq = generatorId(null, today);
        // 4. 格式化补零，返回日内标识
        return today + String.format("%0" + size + "d", seq);
    }

    @Override
    public String nextId(String prefix) {
        String today = DTF.format(LocalDate.now());
        Long seq = generatorId(prefix, today);
        // 4. 格式化补零，返回日内标识
        return prefix + today + String.format("%04d", seq);
    }

    @Override
    public String nextId(String prefix, Integer size) {
        String today = DTF.format(LocalDate.now());
        Long seq = generatorId(prefix, today);
        // 4. 格式化补零，返回日内标识
        return prefix + today + String.format("%0" + size + "d", seq);
    }

    /**
     * 生成日内递增标识（纯数字，无补零）
     *
     * @return 当日递增数字（例如 1、2、3...）
     */
    public Long generatorId(String prefix, String today) {
        // 1. 构造当日的key,格式为: day:seq:[prefix]today
        String key = INTRADAY_SEQ + (StrUtil.isEmpty(prefix) ? today : prefix + today);
        // 2. redis原子递增（初始值为 0，第一次递增后返回 1，后续依次+1）
        Long increment = stringRedisTemplate.opsForValue().increment(key, 1);
        // 3. 设置过期时间（仅第一次递增时设置，避免重复设置）
        if (increment != null && increment == 1) {
            stringRedisTemplate.expire(key, 24, TimeUnit.HOURS);
        }
        // 4. 日内递增序列值
        return increment;
    }

}
