package com.moyu.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysUserParam;

import java.util.List;

/**
 * @author shisong
 * @description 针对表【sys_user(用户信息表)】的数据库操作Service
 * @createDate 2024-12-25 20:35:45
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 获取记录列表
     */
    List<SysUser> list(SysUserParam param);

    /**
     * 分页获取记录列表
     */
    PageData<SysUser> pageList(SysUserParam param);

    /**
     * 获取记录详情
     */
    SysUser detail(SysUserParam param);

    /**
     * 添加记录
     */
    void add(SysUserParam param);

    /**
     * 修改记录
     */
    void update(SysUserParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(SysUserParam param);

    /**
     * 重置用户密码
     **/
    void updatePassword(SysUserParam userParam);

    /**
     * 重置用户密码
     **/
    void resetPassword(SysUserParam userParam);

}
