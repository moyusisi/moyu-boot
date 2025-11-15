package com.moyu.boot.plugin.sysLog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moyu.boot.common.mybatis.annotation.DataPermission;
import com.moyu.boot.plugin.sysLog.model.entity.SysLog;
import com.moyu.boot.plugin.sysLog.model.param.SysLogParam;
import com.moyu.boot.plugin.sysLog.model.vo.SysLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 针对表sys_log(系统日志表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {

    /**
     * 获取系统日志分页数据
     * 仅示例自定义方法的写法，实际mybatis-plus提供了分页查询方法
     * 注意：自定义 Mapper 方法中使用分页，返回类型必须是 IPage(或List)。
     *
     * @param page 分页对象(包含页码、页大小)
     * @param param 查询参数
     * @return {@link Page<SysLogVO>} 系统日志分页列表
     */
    Page<SysLogVO> getSysLogPage(Page<SysLogVO> page, SysLogParam param);

   /**
   * 这是一个使用数据权限的例子，也是一个使用使用 Wrapper 自定义 SQL 的例子
   * <a href="https://baomidou.com/guides/wrapper/#%E4%BD%BF%E7%94%A8-wrapper-%E8%87%AA%E5%AE%9A%E4%B9%89-sql">参考这里</a>
   */
   @DataPermission(userColumn = "create_by")
   @Select("SELECT * FROM sys_log ${ew.customSqlSegment} LIMIT 5")
   List<SysLogVO> selectLimit(@Param(Constants.WRAPPER) Wrapper<SysLogVO> wrapper);
}

