package com.moyu.boot.system.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.annotation.SysLog;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysGroupParam;
import com.moyu.boot.system.model.vo.SysGroupVO;
import com.moyu.boot.system.model.vo.SysRoleVO;
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
    @SysLog(module = "system", value = "分页查询岗位列表")
    @PostMapping("/page")
    public Result<PageData<SysGroupVO>> pageList(@RequestBody SysGroupParam groupParam) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(groupParam.getPageNum(), groupParam.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<SysGroupVO> page = sysGroupService.pageList(groupParam);
        return Result.success(page);
    }

    /**
     * 获取详情
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:detail')")
    @SysLog(module = "system", value = "查询岗位详情", response = true)
    @PostMapping("/detail")
    public Result<SysGroupVO> detail(@RequestBody SysGroupParam groupParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(groupParam.getId(), groupParam.getCode()), "id和code不能同时为空");
        return Result.success(sysGroupService.detail(groupParam));
    }

    /**
     * 添加
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:add')")
    @SysLog(module = "system", value = "新增岗位", response = true)
    @PostMapping("/add")
    public Result<String> add(@Validated @RequestBody SysGroupParam groupParam) {
        sysGroupService.add(groupParam);
        return Result.success();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:delete')")
    @SysLog(module = "system", value = "删除岗位", response = true)
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getIds(), "删除列表ids不能为空");
        sysGroupService.deleteByIds(groupParam);
        return Result.success();
    }

    /**
     * 编辑
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:edit')")
    @SysLog(module = "system", value = "修改岗位", response = true)
    @PostMapping("/edit")
    public Result<String> edit(@Validated @RequestBody SysGroupParam groupParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(groupParam.getId(), groupParam.getCode()), "id和code不能同时为空");
        sysGroupService.update(groupParam);
        return Result.success();
    }

    /**
     * 查询指定分组的角色列表
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:roleList')")
    @PostMapping("/roleList")
    @SysLog(module = "system", value = "查询岗位内的角色列表")
    public Result<List<SysRoleVO>> roleList(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        List<SysRoleVO> list = sysGroupService.groupRoleList(groupParam);
        return Result.success(list);
    }

    /**
     * 分组内新增角色
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:addRole')")
    @PostMapping("/addRole")
    @SysLog(module = "system", value = "岗位内添加角色", response = true)
    public Result<?> addRole(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        Assert.notEmpty(groupParam.getCodeSet(), "指定集合codeSet不能为空");
        sysGroupService.groupAddRole(groupParam);
        return Result.success();
    }

    /**
     * 分组内移除角色
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:deleteRole')")
    @SysLog(module = "system", value = "岗位内移除角色", response = true)
    @PostMapping("/deleteRole")
    public Result<?> deleteRole(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        Assert.notEmpty(groupParam.getCodeSet(), "指定集合codeSet不能为空");
        sysGroupService.groupDeleteRole(groupParam);
        return Result.success();
    }

    /**
     * 查询指定分组的角色列表
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:userList')")
    @SysLog(module = "system", value = "查询岗位内的用户列表")
    @PostMapping("/userList")
    public Result<List<SysUser>> userList(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        List<SysUser> list = sysGroupService.groupUserList(groupParam);
        return Result.success(list);
    }

    /**
     * 分组内新增角色
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:addUser')")
    @SysLog(module = "system", value = "岗位内的添加用户", response = true)
    @PostMapping("/addUser")
    public Result<?> addUser(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        Assert.notEmpty(groupParam.getCodeSet(), "指定集合codeSet不能为空");
        sysGroupService.groupAddUser(groupParam);
        return Result.success();
    }

    /**
     * 分组内移除角色
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:group:deleteUser')")
    @SysLog(module = "system", value = "岗位内的移除用户", response = true)
    @PostMapping("/deleteUser")
    public Result<?> deleteUser(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "分组code不能为空");
        Assert.notEmpty(groupParam.getCodeSet(), "指定集合codeSet不能为空");
        sysGroupService.groupDeleteUser(groupParam);
        return Result.success();
    }

}
