package com.moyu.boot.plugin.codegen.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codegen.model.param.TableQueryParam;
import com.moyu.boot.plugin.codegen.model.vo.TableVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shisong
 * @since 2025-09-14
 */
@Mapper
public interface DbTableMapper extends BaseMapper {

    /**
     * 获取表分页列表
     *
     * @param page  分页参数
     * @param param 查询参数
     */
    PageData<TableVO> getTablePage(Page<TableVO> page, TableQueryParam param);

}
