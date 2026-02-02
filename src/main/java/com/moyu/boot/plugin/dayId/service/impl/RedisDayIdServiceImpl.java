package com.moyu.boot.plugin.dayId.service.impl;

import cn.hutool.core.util.StrUtil;
import com.moyu.boot.plugin.dayId.model.vo.DayIdVO;
import com.moyu.boot.plugin.dayId.service.DayIdService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis的日内标识生成服务(单日维度生成连续编号)
 *
 * @author shisong
 * @since 2026-01-29
 */
@Component
public class RedisDayIdServiceImpl implements DayIdService {

    // 固定前缀
    private static final String INTRADAY_SEQ = "seq:day:";

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
     * 读取当前Id序号
     */
    @Override
    public String getId(String idKey) {
        if (StrUtil.isEmpty(idKey)) {
            return null;
        }
        // 构造fullKey,格式为: seq:day:idKey
        String fullKey = INTRADAY_SEQ + idKey;
        // 从redis读取值并返回
        return stringRedisTemplate.opsForValue().get(fullKey);
    }

    @Override
    public List<DayIdVO> list(String keyword) {
        List<DayIdVO> voList = new ArrayList<>();
        // 构造模糊匹配表达式：前缀 + * [+ keyword + *]
        String keyPattern = INTRADAY_SEQ + (StrUtil.isBlank(keyword) ? "*" : "*" + keyword + "*");
        // 获取所有匹配前缀的 key 集合（Set 类型，避免重复）
        Set<String> matchKeys = stringRedisTemplate.keys(keyPattern);
        // 遍历匹配到的key并取值
        for (String key : matchKeys) {
            DayIdVO vo = new DayIdVO();
            String value = stringRedisTemplate.opsForValue().get(key);
            vo.setIdKey(StrUtil.subAfter(key, INTRADAY_SEQ, false));
            vo.setIdValue(value);
            vo.setSeq(vo.getIdKey() + String.format("%04d", Long.valueOf(vo.getIdValue())));
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 生成日内递增标识（纯数字，无补零）
     *
     * @return 当日递增数字（例如 1、2、3...）
     */
    private Long generatorId(String prefix, String today) {
        // 1. 构造当日的key,格式为: seq:day:[prefix]today
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
