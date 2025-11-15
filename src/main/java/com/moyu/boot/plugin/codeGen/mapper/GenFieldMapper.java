package com.moyu.boot.plugin.codeGen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.plugin.codeGen.model.entity.GenField;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shisong
 * @description 针对表【gen_field_config(代码生成字段配置表)】的数据库操作Mapper
 * @createDate 2025-09-15 16:08:55
 */
@Mapper
public interface GenFieldMapper extends BaseMapper<GenField> {

}




