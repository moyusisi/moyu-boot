package com.moyu.boot.plugin.codegen.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codegen.model.entity.GenConfig;
import com.moyu.boot.plugin.codegen.model.param.GenConfigParam;
import com.moyu.boot.plugin.codegen.model.vo.GenConfigInfo;

import java.util.Set;

/**
 * 针对表【gen_config(代码生成实体配置表)】的数据库操作Service
 *
 * @author shisong
 * @since 2025-09-14
 */
public interface GenConfigService extends IService<GenConfig> {

    /**
     * 分页获取记录列表
     */
    PageData<GenConfig> pageList(GenConfigParam param);

    /**
     * 查询代码配置详情(包括字段配置)，无则新生成(仅生成未保存)
     */
    GenConfigInfo getConfigDetail(String tableName);

    /**
     * 保存代码生成配置(包括字段配置)
     */
    void saveConfig(GenConfigInfo genConfigInfo);

    /**
     * 删除代码生成配置(包括字段配置)
     */
    void deleteConfig(String tableName);

    /**
     * 导入表，从表结构生成表配置(包括字段配置)
     */
    void importTable(Set<String> tableNameSet);

}
