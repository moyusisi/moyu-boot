package com.moyu.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.boot.system.model.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shisong
 * @description 针对表【sys_menu(菜单资源表)】的数据库操作Mapper
 * @createDate 2024-12-10 21:05:13
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

}




