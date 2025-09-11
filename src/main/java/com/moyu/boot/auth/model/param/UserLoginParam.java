package com.moyu.boot.auth.model.param;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 用户登陆的请求参数对象
 *
 * @author shisong
 * @since 2025-01-22
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginParam {

    /**
     * 账号
     */
    @NotBlank(message = "用户账号account不能为空")
    private String account;

    /**
     * 密码 不允许序列化只允许反序列化
     */
    @NotBlank(message = "密码password不能为空")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}
