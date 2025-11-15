package com.moyu.boot.plugin.codeGen.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.plugin.codeGen.model.entity.GenConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表【gen_config(代码生成实体配置表)】的数据库操作Mapper
 *
 * @author shisong
 * @see GenConfig
 * @since 2025-09-14
 *
 */
@Mapper
public interface GenConfigMapper extends BaseMapper<GenConfig> {
}
