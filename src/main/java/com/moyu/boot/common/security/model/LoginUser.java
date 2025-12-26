package com.moyu.boot.common.security.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 登陆用户对象
 *
 * @author shisong
 * @see org.springframework.security.core.userdetails.User
 * @since 2024-12-27
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements UserDetails, CredentialsContainer {
    private static final long serialVersionUID = 1L;

    /**
     * 用户姓名
     */
    private String name;
    /**
     * 当前组织机构
     */
    private String orgCode;
    /**
     * 当前岗位(可能为空)
     */
    private String groupCode;
    /**
     * 当前岗位所在组织机构，决定数据权限
     */
    private String groupOrgCode;
    /**
     * 数据权限范围
     */
    private Integer dataScope;
    /**
     * 自定义数据权限集合
     */
    private Set<String> scopeSet;

    /**
     * 角色集合
     */
    private Set<String> roles;

    /**
     * 权限集合(仅接口的权限标记)
     */
    private Set<String> perms;

    /**
     * 接口权限对应的数据范围
     */
    private Map<String, LoginUser.DataScopeInfo> permScopeMap;

    /**
     * 默认字段
     *
     * @see org.springframework.security.core.userdetails.User
     */
    private String username;

    /**
     * 不允许序列化只允许反序列化
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 授权集合，角色标识、权限标识均位于此集合。SecurityExpressionRoot中的hasRole等方法会使用此字段
     *
     * @see org.springframework.security.access.expression.SecurityExpressionRoot
     */
    @JsonIgnore
    private Collection<GrantedAuthority> authorities;

    /**
     * 账户是否未过期,过期无法验证
     */
    @JsonIgnore
    private boolean accountNonExpired;
    /**
     * 用户是否解锁,锁定的用户无法进行身份验证
     */
    @JsonIgnore
    private boolean accountNonLocked;
    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     */
    @JsonIgnore
    private boolean credentialsNonExpired;
    /**
     * 是否可用 ,禁用的用户不能身份验证
     */
    @JsonIgnore
    private boolean enabled;

    /**
     * 数据范围信息
     **/
    @Data
    public static class DataScopeInfo {

        /**
         * 数据范围(字典 0无限制 1本人数据 2本机构 3本机构及以下 4自定义)
         *
         * @see com.moyu.boot.common.core.enums.DataScopeEnum
         */
        private Integer dataScope;

        /**
         * 数据范围集合
         */
        private Set<String> scopeSet;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(String... authorities) {
        this.authorities = AuthorityUtils.createAuthorityList(authorities);
    }

    public void setAuthorities(Collection<String> authorities) {
        this.authorities = AuthorityUtils.createAuthorityList(authorities.toArray(new String[0]));
    }

    /**
     * 根据perms和roles生成授权列表
     */
    public void initAuthorities() {
        Set<String> authorities = new HashSet<>();
        if (!CollectionUtils.isEmpty(roles)) {
            roles.forEach(role -> {
                // SecurityExpressionRoot#hasRole中会根据前缀判断
                authorities.add("ROLE_" + role);
            });
        }
        if (!CollectionUtils.isEmpty(perms)) {
            authorities.addAll(perms);
        }
        this.authorities = AuthorityUtils.createAuthorityList(authorities.toArray(new String[0]));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
