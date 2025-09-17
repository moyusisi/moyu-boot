package com.moyu.boot.plugin.codegen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.plugin.codegen.mapper.GenFieldMapper;
import com.moyu.boot.plugin.codegen.model.entity.GenField;
import com.moyu.boot.plugin.codegen.service.GenFieldService;
import org.springframework.stereotype.Service;

/**
 * @author shisong
 * @description 针对表【gen_field_config(代码生成字段配置表)】的数据库操作Service实现
 * @createDate 2025-09-15 16:08:55
 */
@Service
public class GenFieldServiceImpl extends ServiceImpl<GenFieldMapper, GenField> implements GenFieldService {

}




