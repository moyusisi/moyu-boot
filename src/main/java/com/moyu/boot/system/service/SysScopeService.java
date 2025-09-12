package com.moyu.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysScope;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysScopeParam;

import java.util.List;
import java.util.Set;

/**
 * @author shisong
 * @description 针对表【sys_scope(数据权限分组表)】的数据库操作Service
 * @createDate 2025-02-27 10:19:59
 */
public interface SysScopeService extends IService<SysScope> {

    /**
     * 分页获取记录列表
     */
    PageData<SysScope> pageList(SysScopeParam scopeParam);

    /**
     * 获取记录详情
     */
    SysScope detail(SysScopeParam scopeParam);

    /**
     * 添加记录
     */
    void add(SysScopeParam scopeParam);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(SysScopeParam scopeParam);

    /**
     * 修改记录
     */
    void edit(SysScopeParam scopeParam);

    ////// scope 通过 relation 管理的数据

    /**
     * scope内用户列表
     */
    List<SysUser> scopeUserList(SysScopeParam scopeParam);

    /**
     * scope新增用户
     */
    void scopeAddUser(SysScopeParam scopeParam);

    /**
     * scope删除用户
     */
    void scopeDeleteUser(SysScopeParam scopeParam);

    /**
     * 获取用户所有的数据权限
     */
    Set<String> userDataScopes(String userId);
}
