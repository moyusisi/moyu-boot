package com.moyu.boot.plugin.inboxMessage.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moyu.boot.common.mybatis.annotation.DataPermission;
import com.moyu.boot.plugin.inboxMessage.model.entity.InboxMessage;
import com.moyu.boot.plugin.inboxMessage.model.param.InboxMessageParam;
import com.moyu.boot.plugin.inboxMessage.model.vo.InboxMessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 针对表inbox_message(站内消息表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2026-01-13
 */
@Mapper
public interface InboxMessageMapper extends BaseMapper<InboxMessage> {

    /**
     * 获取站内消息分页数据
     * 仅示例自定义方法的写法，实际mybatis-plus提供了分页查询方法
     * 注意：自定义 Mapper 方法中使用分页，返回类型必须是 IPage(或List)。
     *
     * @param page 分页对象(包含页码、页大小)
     * @param param 查询参数
     * @return {@link Page< InboxMessageVO >} 站内消息分页列表
     */
    Page<InboxMessageVO> getDevMessagePage(Page<InboxMessageVO> page, InboxMessageParam param);

   /**
   * 这是一个使用数据权限的例子，也是一个使用使用 Wrapper 自定义 SQL 的例子
   * <a href="https://baomidou.com/guides/wrapper/#%E4%BD%BF%E7%94%A8-wrapper-%E8%87%AA%E5%AE%9A%E4%B9%89-sql">参考这里</a>
   */
   @DataPermission(userColumn = "send_by")
   @Select("SELECT * FROM inbox_message ${ew.customSqlSegment} LIMIT 5")
   List<InboxMessageVO> selectLimit(@Param(Constants.WRAPPER) Wrapper<InboxMessageVO> wrapper);
}

