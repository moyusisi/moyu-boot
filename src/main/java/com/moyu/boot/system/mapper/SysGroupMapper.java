package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.system.model.entity.SysGroup;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shisong
 * @description 针对表【sys_group(分组信息表)】的数据库操作Mapper
 * @createDate 2024-12-20 14:29:15
 * @Entity com.moyu.system.sys.model.entity.SysGroup
 */
@Mapper
public interface SysGroupMapper extends BaseMapper<SysGroup> {

}




