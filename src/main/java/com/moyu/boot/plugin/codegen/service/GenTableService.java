package com.moyu.boot.plugin.codegen.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codegen.model.entity.GenTable;
import com.moyu.boot.plugin.codegen.model.param.GenTableParam;

import java.util.List;

/**
 * 针对表【gen_config(代码生成实体配置表)】的数据库操作Service
 *
 * @author shisong
 * @since 2025-09-14
 */
public interface GenTableService extends IService<GenTable> {

    /**
     * 获取记录列表
     */
    List<GenTable> list(GenTableParam param);

    /**
     * 分页获取记录列表
     */
    PageData<GenTable> pageList(GenTableParam param);

    /**
     * 获取记录详情
     */
    GenTable detail(GenTableParam param);

    /**
     * 添加记录
     */
    void add(GenTableParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(GenTableParam param);

    /**
     * 修改记录
     */
    void edit(GenTableParam param);

}
