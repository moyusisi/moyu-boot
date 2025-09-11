package com.moyu.boot.system.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.BaseResponse;
import com.moyu.boot.common.core.model.PageResult;
import com.moyu.boot.system.model.entity.SysGroup;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysGroupParam;
import com.moyu.boot.system.service.SysGroupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分组(角色组、分组)控制器
 *
 * @author shisong
 * @since 2024-12-20
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/sys/group")
public class SysGroupController {

    @Resource
    private SysGroupService sysGroupService;

    /**
     * 分页获取角色列表
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:page')")
    @PostMapping("/page")
    public BaseResponse<PageResult<SysGroup>> pageList(@RequestBody SysGroupParam groupParam) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(groupParam.getPageNum(), groupParam.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageResult<SysGroup> page = sysGroupService.pageList(groupParam);
        return BaseResponse.getSuccessResponse(page);
    }

    /**
     * 获取详情
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:detail')")
    @PostMapping("/detail")
    public BaseResponse<SysGroup> detail(@RequestBody SysGroupParam groupParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(groupParam.getId(), groupParam.getCode()), "id和code不能同时为空");
        return BaseResponse.getSuccessResponse(sysGroupService.detail(groupParam));
    }

    /**
     * 添加
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:add')")
    @PostMapping("/add")
    public BaseResponse<String> add(@Validated @RequestBody SysGroupParam groupParam) {
        sysGroupService.add(groupParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:delete')")
    @PostMapping("/delete")
    public BaseResponse<String> delete(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getIds(), "删除列表ids不能为空");
        sysGroupService.deleteByIds(groupParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 编辑
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:edit')")
    @PostMapping("/edit")
    public BaseResponse<String> edit(@Validated @RequestBody SysGroupParam groupParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(groupParam.getId(), groupParam.getCode()), "id和code不能同时为空");
        sysGroupService.edit(groupParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 查询指定分组的角色列表
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:roleList')")
    @PostMapping("/roleList")
    public BaseResponse<List<SysRole>> roleList(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        List<SysRole> list = sysGroupService.groupRoleList(groupParam);
        return BaseResponse.getSuccessResponse(list);
    }

    /**
     * 分组内新增角色
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:addRole')")
    @PostMapping("/addRole")
    public BaseResponse<?> addRole(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        Assert.notEmpty(groupParam.getCodeSet(), "指定集合codeSet不能为空");
        sysGroupService.groupAddRole(groupParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 分组内移除角色
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:deleteRole')")
    @PostMapping("/deleteRole")
    public BaseResponse<?> deleteRole(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        Assert.notEmpty(groupParam.getCodeSet(), "指定集合codeSet不能为空");
        sysGroupService.groupDeleteRole(groupParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 查询指定分组的角色列表
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:userList')")
    @PostMapping("/userList")
    public BaseResponse<List<SysUser>> userList(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        List<SysUser> list = sysGroupService.groupUserList(groupParam);
        return BaseResponse.getSuccessResponse(list);
    }

    /**
     * 分组内新增角色
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:addUser')")
    @PostMapping("/addUser")
    public BaseResponse<?> addUser(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        Assert.notEmpty(groupParam.getCodeSet(), "指定集合codeSet不能为空");
        sysGroupService.groupAddUser(groupParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 分组内移除角色
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:deleteUser')")
    @PostMapping("/deleteUser")
    public BaseResponse<?> deleteUser(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        Assert.notEmpty(groupParam.getCodeSet(), "指定集合codeSet不能为空");
        sysGroupService.groupDeleteUser(groupParam);
        return BaseResponse.getSuccessResponse();
    }

}
