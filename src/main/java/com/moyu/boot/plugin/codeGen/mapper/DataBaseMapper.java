package com.moyu.boot.plugin.codeGen.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moyu.boot.plugin.codeGen.model.bo.ColumnMetaData;
import com.moyu.boot.plugin.codeGen.model.param.GenConfigParam;
import com.moyu.boot.plugin.codeGen.model.vo.TableMetaData;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * @author shisong
 * @since 2025-09-14
 */
@Mapper
public interface DataBaseMapper extends BaseMapper {

    /**
     * 获取表分页列表
     * 注意：自定义 Mapper 方法中使用分页，返回类型必须是 IPage(或List)。
     * 参考：https://baomidou.com/plugins/pagination/
     *
     * @param page  分页参数
     * @param param 查询参数
     */
    Page<TableMetaData> getTablePage(Page<TableMetaData> page, GenConfigParam param);

    /**
     * 获取表元数据
     */
    List<TableMetaData> getTableListByNames(Collection<String> tableNameSet);

    /**
     * 获取表元数据
     */
    TableMetaData getTableMetaData(String tableName);

    /**
     * 获取字段元数据列表
     */
    List<ColumnMetaData> getTableColumns(String tableName);

}
