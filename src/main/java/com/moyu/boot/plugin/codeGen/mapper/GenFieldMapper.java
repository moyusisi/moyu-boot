package com.moyu.boot.plugin.codeGen.mapper;

import com.moyu.boot.plugin.codeGen.model.entity.GenField;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表gen_field_config(代码生成字段配置表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2025-09-15 16:08:55
 */
@Mapper
public interface GenFieldMapper extends BaseMapper<GenField> {

}




