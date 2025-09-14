package com.moyu.boot.plugin.codegen.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * 注意：自定义 Mapper 方法中使用分页，返回类型必须是 IPage(或List)。
     * 参考：https://baomidou.com/plugins/pagination/
     *
     * @param page  分页参数
     * @param param 查询参数
     */
    Page<TableVO> getTablePage(Page<TableVO> page, TableQueryParam param);

}
