package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moyu.boot.system.model.entity.SysConfig;
import com.moyu.boot.system.model.param.SysConfigParam;
import com.moyu.boot.system.model.vo.SysConfigVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表sys_config(系统配置表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2026-05-28
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 获取系统配置分页数据
     * 仅示例自定义方法的写法，实际mybatis-plus提供了分页查询方法
     * 注意：自定义 Mapper 方法中使用分页，返回类型必须是 IPage(或List)。
     *
     * @param page  分页对象(包含页码、页大小)
     * @param param 查询参数
     * @return {@link Page<SysConfigVO>} 系统配置分页列表
     */
    Page<SysConfigVO> getSysConfigPage(Page<SysConfigVO> page, SysConfigParam param);
}

