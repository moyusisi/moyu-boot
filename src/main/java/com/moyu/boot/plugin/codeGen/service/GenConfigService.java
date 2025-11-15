package com.moyu.boot.plugin.codeGen.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codeGen.model.entity.GenConfig;
import com.moyu.boot.plugin.codeGen.model.param.GenConfigParam;
import com.moyu.boot.plugin.codeGen.model.vo.CodePreviewVO;
import com.moyu.boot.plugin.codeGen.model.vo.GenConfigInfo;
import com.moyu.boot.plugin.codeGen.model.vo.TableMetaData;

import java.util.List;
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
     * 查询代码配置详情(包括字段配置)
     */
    GenConfigInfo configDetail(GenConfigParam param);

    /**
     * 保存代码生成配置(包括字段配置)
     */
    void saveConfig(GenConfigInfo genConfigInfo);

    /**
     * 通过ids删除记录(包括字段配置)
     */
    void deleteByIds(GenConfigParam param);

    /**
     * 分页查询数据库中的表
     */
    PageData<TableMetaData> tablePageList(GenConfigParam param);

    /**
     * 导入表，从表结构生成表配置(包括字段配置)
     */
    void importTable(Set<String> tableNameSet);

    /**
     * 导入SQL，从SQL成表配置(包括字段配置)
     */
    void importSql(String sql);

    /**
     * 重置表，重新生成表配置
     */
    void resetTable(GenConfigParam param);

    /**
     * 生成代码预览
     */
    List<CodePreviewVO> previewCode(GenConfigParam param);

    /**
     * 生成zip代码包字节流
     */
    byte[] downloadZip(GenConfigParam param);

}
