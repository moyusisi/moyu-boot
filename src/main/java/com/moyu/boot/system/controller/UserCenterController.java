package com.moyu.boot.system.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.BaseResponse;
import com.moyu.boot.common.security.util.SecurityUtils;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.param.SysGroupParam;
import com.moyu.boot.system.model.param.SysRoleParam;
import com.moyu.boot.system.model.vo.UserInfo;
import com.moyu.boot.system.service.UserCenterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户中心控制器，当前登陆用户控制器
 *
 * @author shisong
 * @since 2024-12-20
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/sys/userCenter")
public class UserCenterController {

    @Resource
    private UserCenterService userCenterService;

    /**
     * 获取当前登陆用户信息
     */
    @PostMapping("/userInfo")
    public BaseResponse<UserInfo> currentUserInfo() {
        // 当前登陆用户username
        String username = SecurityUtils.getLoginUser().getUsername();
        return BaseResponse.getSuccessResponse(userCenterService.currentUserInfo(username));
    }

    /**
     * 获取当前登陆用户的菜单
     */
    @RequestMapping("/userMenu")
    public BaseResponse<List<Tree<String>>> userMenu() {
        // 当前登陆用户username
        String username = SecurityUtils.getLoginUser().getUsername();
        return BaseResponse.getSuccessResponse(userCenterService.userMenu(username));
    }

    /**
     * 获取用户所属公司的组织树
     */
    @Log(jsonLog = true, response = false)
    @PostMapping("/userOrgTree")
    public BaseResponse<List<Tree<String>>> userOrgTree() {
        // 当前登陆用户username
        String username = SecurityUtils.getLoginUser().getUsername();
        List<Tree<String>> list = userCenterService.userOrgTree(username);
        return BaseResponse.getSuccessResponse(list);
    }

    /**
     * 获取当前用户拥有的角色列表
     */
    @PostMapping("/userRoleList")
    public BaseResponse<List<SysRole>> userRoleList(@RequestBody SysRoleParam roleParam) {
        // 当前登陆用户username
        String username = SecurityUtils.getLoginUser().getUsername();
        return BaseResponse.getSuccessResponse(userCenterService.userRoleList(username, roleParam.getSearchKey()));
    }

    /**
     * 获取当前用户拥有的角色列表
     */
    @PostMapping("/switchUserGroup")
    public BaseResponse<String> switchUserGroup(@RequestBody SysGroupParam groupParam) {
        Assert.notEmpty(groupParam.getCode(), "岗位code不能为空");
        return BaseResponse.getSuccessResponse(userCenterService.switchUserGroup(groupParam.getCode()));
    }

}
