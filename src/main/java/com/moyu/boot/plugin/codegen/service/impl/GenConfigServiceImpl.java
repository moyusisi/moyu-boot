package com.moyu.boot.plugin.codegen.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.plugin.codegen.mapper.GenConfigMapper;
import com.moyu.boot.plugin.codegen.model.entity.GenConfig;
import com.moyu.boot.plugin.codegen.service.GenConfigService;

/**
 * 针对表【gen_config(代码生成实体配置表)】的数据库操作Service实现
 *
 * @author shisong
 * @since 2025-09-14
 */
public class GenConfigServiceImpl extends ServiceImpl<GenConfigMapper, GenConfig> implements GenConfigService {

}
