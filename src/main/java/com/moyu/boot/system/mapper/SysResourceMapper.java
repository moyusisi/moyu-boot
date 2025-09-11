package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.system.model.entity.SysResource;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shisong
 * @description 针对表【sys_resource(资源权限表)】的数据库操作Mapper
 * @createDate 2024-12-10 21:05:13
 * @Entity com.moyu.system.sys.model.entity.SysResource
 */
@Mapper
public interface SysResourceMapper extends BaseMapper<SysResource> {

}




