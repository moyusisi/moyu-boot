package com.moyu.boot.system.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.BaseResponse;
import com.moyu.boot.common.core.model.PageResult;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysRoleParam;
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
    @PostMapping("/list")
    public BaseResponse<List<SysRole>> list(@RequestBody SysRoleParam roleParam) {
        List<SysRole> list = sysRoleService.list(roleParam);
        return BaseResponse.getSuccessResponse(list);
    }

    /**
     * 分页获取角色列表
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:page')")
    @PostMapping("/page")
    public BaseResponse<PageResult<SysRole>> pageList(@RequestBody SysRoleParam roleParam) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(roleParam.getPageNum(), roleParam.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageResult<SysRole> page = sysRoleService.pageList(roleParam);
        return BaseResponse.getSuccessResponse(page);
    }

    /**
     * 获取详情
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:detail')")
    @PostMapping("/detail")
    public BaseResponse<SysRole> detail(@RequestBody SysRoleParam roleParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(roleParam.getId(), roleParam.getCode()), "id和code不能同时为空");
        return BaseResponse.getSuccessResponse(sysRoleService.detail(roleParam));
    }

    /**
     * 添加
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:add')")
    @PostMapping("/add")
    public BaseResponse<String> add(@Validated @RequestBody SysRoleParam roleParam) {
        sysRoleService.add(roleParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:delete')")
    @PostMapping("/delete")
    public BaseResponse<String> delete(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getIds(), "删除列表ids不能为空");
        sysRoleService.deleteByIds(roleParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 编辑
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:edit')")
    @PostMapping("/edit")
    public BaseResponse<String> edit(@Validated @RequestBody SysRoleParam roleParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(roleParam.getId(), roleParam.getCode()), "id和code不能同时为空");
        sysRoleService.edit(roleParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 获取菜单树，用于给角色授权时选择(treeNode不包含button)
     */
    @PostMapping("/menuTreeForGrant")
    public BaseResponse<List<Tree<String>>> menuTreeForGrant(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色code不能为空");
        return BaseResponse.getSuccessResponse(sysRoleService.treeForGrant(roleParam));
    }

    /**
     * 给角色授权菜单
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:grantMenu')")
    @PostMapping("/grantMenu")
    public BaseResponse<List<Tree<String>>> grantMenu(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色code不能为空");
        Assert.notEmpty(roleParam.getModule(), "模块module不能为空");
        Assert.notEmpty(roleParam.getGrantMenuList(), "授权列表grantMenuList不能为空");
        sysRoleService.grantMenu(roleParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 查询指定角色的用户列表(仅直接通过 role_has_user 关系指定的用户，即全局角色用户)
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:userList')")
    @PostMapping("/userList")
    public BaseResponse<List<SysUser>> userList(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "分组code不能为空");
        List<SysUser> list = sysRoleService.roleUserList(roleParam);
        return BaseResponse.getSuccessResponse(list);
    }

    /**
     * 角色新增用户
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:addUser')")
    @PostMapping("/roleAddUser")
    public BaseResponse<?> roleAddUser(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色code不能为空");
        Assert.notEmpty(roleParam.getCodeSet(), "指定集合codeSet不能为空");
        sysRoleService.roleAddUser(roleParam);
        return BaseResponse.getSuccessResponse();
    }

    /**
     * 角色删除用户
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:role:deleteUser')")
    @PostMapping("/roleDeleteUser")
    public BaseResponse<?> roleDeleteUser(@RequestBody SysRoleParam roleParam) {
        Assert.notEmpty(roleParam.getCode(), "角色code不能为空");
        Assert.notEmpty(roleParam.getCodeSet(), "指定集合codeSet不能为空");
        sysRoleService.roleDeleteUser(roleParam);
        return BaseResponse.getSuccessResponse();
    }

}
