package com.moyu.boot.plugin.codegen.service;


import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codegen.model.param.TableQueryParam;
import com.moyu.boot.plugin.codegen.model.vo.TableVO;

/**
 * 代码生成服务类
 *
 * @author shisong
 * @since 2025-09-14
 */
public interface CodegenService {

    /**
     * 分页查询数据表列表
     */
    PageData<TableVO> tablePageList(TableQueryParam param);

}
