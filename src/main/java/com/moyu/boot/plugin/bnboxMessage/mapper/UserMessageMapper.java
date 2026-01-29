package com.moyu.boot.plugin.bnboxMessage.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.moyu.boot.common.mybatis.annotation.DataPermission;
import com.moyu.boot.plugin.bnboxMessage.model.entity.UserMessage;
import com.moyu.boot.plugin.bnboxMessage.model.vo.UserMessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 针对表user_message(站内信接收表)的数据库操作Mapper
 *
 * @author moyusisi
 * @since 2026-01-14
 */
@Mapper
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    /**
     * 这是一个使用数据权限的例子，也是一个使用使用 Wrapper 自定义 SQL 的例子
     * <a href="https://baomidou.com/guides/wrapper/#%E4%BD%BF%E7%94%A8-wrapper-%E8%87%AA%E5%AE%9A%E4%B9%89-sql">参考这里</a>
     */
    @DataPermission(userColumn = "create_by")
    @Select("SELECT * FROM user_message ${ew.customSqlSegment} LIMIT 5")
    List<UserMessageVO> selectLimit(@Param(Constants.WRAPPER) Wrapper<UserMessageVO> wrapper);
}

