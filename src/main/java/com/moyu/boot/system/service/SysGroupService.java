package com.moyu.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysGroup;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysGroupParam;

import java.util.List;
import java.util.Set;

/**
 * @author shisong
 * @description 针对表【sys_pos(岗位信息表)】的数据库操作Service
 * @createDate 2024-12-20 14:29:15
 */
public interface SysGroupService extends IService<SysGroup> {

    /**
     * 获取记录列表
     */
    List<SysGroup> list(SysGroupParam groupParam);

    /**
     * 分页获取记录列表
     */
    PageData<SysGroup> pageList(SysGroupParam groupParam);

    /**
     * 获取记录详情
     */
    SysGroup detail(SysGroupParam groupParam);

    /**
     * 添加记录
     */
    void add(SysGroupParam groupParam);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(SysGroupParam groupParam);

    /**
     * 修改记录
     */
    void edit(SysGroupParam groupParam);

    ////// group 通过 relation 管理的数据

    /**
     * group内角色列表
     */
    List<SysRole> groupRoleList(SysGroupParam groupParam);

    /**
     * group内用户列表
     */
    List<SysUser> groupUserList(SysGroupParam groupParam);

    /**
     * 用户所属的group列表
     */
    List<SysGroup> userGroupList(String username);

    /**
     * group新增角色
     */
    void groupAddRole(SysGroupParam groupParam);

    /**
     * group删除角色
     */
    void groupDeleteRole(SysGroupParam groupParam);

    /**
     * group新增用户
     */
    void groupAddUser(SysGroupParam groupParam);

    /**
     * group删除用户
     */
    void groupDeleteUser(SysGroupParam groupParam);

    /**
     * 获取指定group的数据范围集合
     */
    Set<String> groupDataScopes(String groupCode);
}
