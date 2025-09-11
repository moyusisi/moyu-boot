package com.moyu.boot.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.moyu.boot.system.model.entity.SysRole;
import com.moyu.boot.system.model.vo.UserInfo;

import java.util.List;

/**
 * 用户中心服务类，当前用户服务类
 *
 * @author shisong
 */
public interface UserCenterService {

    /**
     * 获取登陆用户详情
     */
    UserInfo currentUserInfo(String username);

    /**
     * 获取用户菜单树
     */
    List<Tree<String>> userMenu(String username);

    /**
     * 获取用户组织机构树
     */
    List<Tree<String>> userOrgTree(String username);

    /**
     * 获取用户所有的角色列表
     */
    List<SysRole> userRoleList(String username, String searchKey);

    /**
     * 当前用户切换岗位重新生成token
     */
    String switchUserGroup(String groupCode);
}
