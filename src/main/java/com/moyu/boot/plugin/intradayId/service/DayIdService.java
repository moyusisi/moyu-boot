package com.moyu.boot.plugin.intradayId.service;

/**
 * 日内标识生成服务(单日维度生成连续编号)
 * <p>
 * 日内标识的组成结构如下(每部分用-分开):
 * <pre>
 * 自定义前缀 - 日期(固定8位) - 递增序列(默认4位)
 * xxxx - yyyyMMdd - 0001
 * </pre>
 * 如：202512120001
 * <p>
 *
 * @author shisong
 * @since 2026-01-29
 */
public interface DayIdService {

    /**
     * 获取日内标识(当日下一个ID,递增序列默认4位)
     */
    String nextId();

    /**
     * 获取指定递增序列长度的日内标识(size)
     */
    String nextId(Integer size);

    /**
     * 获取指定前缀的日内标识下一个ID
     * 前缀相同则共享递增序列
     */
    String nextId(String prefix);

    /**
     * 获取指定前缀的日内标识下一个ID
     * 前缀相同则共享递增序列
     */
    String nextId(String prefix, Integer size);

}
