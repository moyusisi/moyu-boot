package com.moyu.boot.system.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.annotation.SysLog;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysRoleParam;
import com.moyu.boot.system.model.vo.SysRoleVO;
import com.moyu.boot.system.service.SysRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色信息控制器
 *
 * @author shisong
 * @since 2024-12-15
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/sys/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 获取角色列表
     */
    @SysLog(module = "system", value = "查询全部角色列表")
    @PostMapping("/list")
    public Result<List<SysRoleVO>> list(@RequestBody SysRoleParam roleParam) {
        List<SysRoleVO> list = sysRoleService.list(roleParam);
        return Result.success(list);
    }

    /**
     * 分页获取角色列表
     */
    @SysLog(module = "system", value = "分页查询角色列表")
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:page')")
    @PostMapping("/page")
    public Result<PageData<SysRoleVO>> pageList(@RequestBody SysRoleParam roleParam) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(roleParam.getPageNum(), roleParam.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<SysRoleVO> page = sysRoleService.pageList(roleParam);
        return Result.success(page);
    }

    /**
     * 获取详情
     */
    @SysLog(module = "system", value = "查询角色详情", response = true)
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:detail')")
    @PostMapping("/detail")
    public Result<SysRoleVO> detail(@RequestBody SysRoleParam roleParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(roleParam.getId(), roleParam.getCode()), "id和code不能同时为空");
        return Result.success(sysRoleService.detail(roleParam));
    }

    /**
     * 添加
     */
    @SysLog(module = "system", value = "新增角色", response = true)
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:add')")
    @PostMapping("/add")
    public Result<String> add(@Validated @RequestBody SysRoleParam roleParam) {
        sysRoleService.add(roleParam);
        return Result.success();
    }

    /**
     * 删除
     */
    @SysLog(module = "system", value = "删除角色", response = true)
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:delete')")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getIds(), "删除列表ids不能为空");
        sysRoleService.deleteByIds(roleParam);
        return Result.success();
    }

    /**
     * 编辑
     */
    @SysLog(module = "system", value = "修改角色", response = true)
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:edit')")
    @PostMapping("/edit")
    public Result<String> edit(@Validated @RequestBody SysRoleParam roleParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(roleParam.getId(), roleParam.getCode()), "id和code不能同时为空");
        sysRoleService.update(roleParam);
        return Result.success();
    }

    /**
     * 获取菜单树，用于给角色授权时选择(treeNode不包含button)
     */
    @PostMapping("/menuTreeForGrant")
    @SysLog(module = "system", value = "获取菜单树")
    public Result<List<Tree<String>>> menuTreeForGrant(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色code不能为空");
        return Result.success(sysRoleService.treeForGrant(roleParam));
    }

    /**
     * 给角色授权菜单
     */
    @SysLog(module = "system", value = "给角色授权菜单资源", response = true)
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:grantMenu')")
    @PostMapping("/grantMenu")
    public Result<List<Tree<String>>> grantMenu(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色code不能为空");
        Assert.notEmpty(roleParam.getModule(), "模块module不能为空");
        Assert.notEmpty(roleParam.getGrantMenuList(), "授权列表grantMenuList不能为空");
        sysRoleService.grantMenu(roleParam);
        return Result.success();
    }

    /**
     * 查询指定角色的用户列表(仅直接通过 role_has_user 关系指定的用户，即全局角色用户)
     */
    @SysLog(module = "system", value = "查询角色包含的用户列表")
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:userList')")
    @PostMapping("/userList")
    public Result<List<SysUser>> userList(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "分组code不能为空");
        List<SysUser> list = sysRoleService.roleUserList(roleParam);
        return Result.success(list);
    }

    /**
     * 角色新增用户
     */
    @SysLog(module = "system", value = "角色中新增用户", response = true)
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:addUser')")
    @PostMapping("/roleAddUser")
    public Result<?> roleAddUser(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色code不能为空");
        Assert.notEmpty(roleParam.getCodeSet(), "指定集合codeSet不能为空");
        sysRoleService.roleAddUser(roleParam);
        return Result.success();
    }

    /**
     * 角色删除用户
     */
    @SysLog(module = "system", value = "角色中删除用户", response = true)
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:deleteUser')")
    @PostMapping("/roleDeleteUser")
    public Result<?> roleDeleteUser(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色code不能为空");
        Assert.notEmpty(roleParam.getCodeSet(), "指定集合codeSet不能为空");
        sysRoleService.roleDeleteUser(roleParam);
        return Result.success();
    }

}
