package com.moyu.boot.plugin.codegen.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codegen.model.entity.GenConfig;
import com.moyu.boot.plugin.codegen.model.param.GenConfigParam;

import java.util.List;

/**
 * 针对表【gen_config(代码生成实体配置表)】的数据库操作Service
 *
 * @author shisong
 * @since 2025-09-14
 */
public interface GenConfigService extends IService<GenConfig> {

    /**
     * 获取记录列表
     */
    List<GenConfig> list(GenConfigParam param);

    /**
     * 分页获取记录列表
     */
    PageData<GenConfig> pageList(GenConfigParam param);

    /**
     * 获取记录详情
     */
    GenConfig detail(GenConfigParam param);

    /**
     * 添加记录
     */
    void add(GenConfigParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(GenConfigParam param);

    /**
     * 修改记录
     */
    void edit(GenConfigParam param);

}
