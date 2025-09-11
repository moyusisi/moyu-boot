package com.moyu.boot.system.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.BaseResponse;
import com.moyu.boot.common.core.model.PageResult;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysUserParam;
import com.moyu.boot.system.service.SysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户控制器
 *
 * @author shisong
 * @since 2024-12-20
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 分页获取角色列表
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:user:page')")
    @PostMapping("/page")
    public BaseResponse<PageResult<SysUser>> pageList(@RequestBody SysUserParam userParam) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(userParam.getPageNum(), userParam.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageResult<SysUser> page = sysUserService.pageList(userParam);
        return BaseResponse.getSuccessResponse(page);
    }

    /**
     * 获取详情
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:user:detail')")
    @PostMapping("/detail")
    public BaseResponse<SysUser> detail(@RequestBody SysUserParam userParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(userParam.getId(), userParam.getAccount()), "id和account不能同时为空");
        return BaseResponse.getSuccessResponse(sysUserService.detail(userParam));
    }

    /**
     * 添加
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:user:add')")
    @PostMapping("/add")
    public BaseResponse<String> add(@Validated @RequestBody SysUserParam sysUserParam) {
        sysUserService.add(sysUserParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:user:delete')")
    @PostMapping("/delete")
    public BaseResponse<String> delete(@RequestBody SysUserParam sysUserParam) {
        Assert.notEmpty(sysUserParam.getIds(), "删除列表ids不能为空");
        sysUserService.deleteByIds(sysUserParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 编辑
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:user:edit')")
    @PostMapping("/edit")
    public BaseResponse<?> edit(@Validated @RequestBody SysUserParam userParam) {
        sysUserService.edit(userParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 修改密码
     **/
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:user:edit')")
    @PostMapping("/updatePwd")
    public BaseResponse<?> updatePassword(@RequestBody SysUserParam userParam) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(userParam.getAccount(), userParam.getPassword()), "account、password都不能为空");
        sysUserService.updatePassword(userParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 重置密码
     **/
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:user:edit')")
    @PostMapping("/resetPwd")
    public BaseResponse<?> resetPassword(@RequestBody SysUserParam sysUserParam) {
        Assert.notEmpty(sysUserParam.getAccount(), "account不能为空");
        sysUserService.resetPassword(sysUserParam);
        return BaseResponse.getSuccessResponse();
    }

}
