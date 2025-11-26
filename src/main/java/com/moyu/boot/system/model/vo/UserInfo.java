package com.moyu.boot.system.model.vo;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

/**
 * 当前登陆用户信息视图对象
 *
 * @author shisong
 * @since 2025-01-07
 */
@Getter
@Setter
@ToString
@Builder
public class UserInfo {

    /**
     * 账号
     */
    private String account;
    /**
     * 用户所属组织结构
     */
    private String orgCode;
    /**
     * 姓名
     */
    private String name;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 用户角色集合 ["root","admin"]
     */
    private Set<String> roles;
    /**
     * 用户权限标识集合
     */
    private Set<String> perms;

    /**
     * 当前岗位(可能为空)
     */
    private String groupCode;
    /**
     * 当前岗位所属组织机构
     */
    private String groupOrgCode;
    /**
     * 数据权限范围
     */
    private Integer dataScope;
    /**
     * 自定义数据权限集合
     */
    private Set<String> scopes;

    /**
     * 岗位列表
     */
    private List<GroupInfo> groupInfoList;

}
