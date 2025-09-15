package com.moyu.boot.plugin.codegen.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.plugin.codegen.model.entity.GenTable;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表【gen_config(代码生成实体配置表)】的数据库操作Mapper
 *
 * @author shisong
 * @see GenTable
 * @since 2025-09-14
 *
 */
@Mapper
public interface GenTableMapper extends BaseMapper<GenTable> {
}
