package com.moyu.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysGroup;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysGroupParam;
import com.moyu.boot.system.model.vo.SysGroupVO;
import com.moyu.boot.system.model.vo.SysRoleVO;

import java.util.List;

/**
 * 角色组服务类Service
 *
 * @author shisong
 * @since 2024-12-20 14:29:15
 */
public interface SysGroupService extends IService<SysGroup> {

    /**
     * 未分配任何分组时为默认分组，默认分组包含用户直接关联的所有角色(ROLE_HAS_USER)
     */
    default String defaultGroup() {
        return "g_default";
    }

    /**
     * 获取记录列表(不分页，不限制数据权限)
     */
    List<SysGroup> list(SysGroupParam param);

    /**
     * 分页获取记录列表
     */
    PageData<SysGroupVO> pageList(SysGroupParam param);

    /**
     * 获取记录详情
     */
    SysGroupVO detail(SysGroupParam param);

    /**
     * 添加记录
     */
    void add(SysGroupParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(SysGroupParam param);

    /**
     * 修改记录
     */
    void update(SysGroupParam param);

    ////// group 通过 relation 管理的数据

    /**
     * group内角色列表
     */
    List<SysRoleVO> groupRoleList(SysGroupParam param);

    /**
     * group内用户列表
     */
    List<SysUser> groupUserList(SysGroupParam param);

    /**
     * 用户所属的group列表
     */
    List<SysGroup> userGroupList(String username);

    /**
     * group新增角色
     */
    void groupAddRole(SysGroupParam param);

    /**
     * group删除角色
     */
    void groupDeleteRole(SysGroupParam param);

    /**
     * group新增用户
     */
    void groupAddUser(SysGroupParam param);

    /**
     * group删除用户
     */
    void groupDeleteUser(SysGroupParam param);

    /**
     * 用户默认的分组(根据用户生成,并非持久化的分组)
     */
    SysGroup userDefaultGroup(String username);

}
