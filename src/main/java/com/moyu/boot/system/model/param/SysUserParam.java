package com.moyu.boot.system.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyu.boot.common.core.model.BasePageParam;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

/**
 * 用户信息参数
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUserParam extends BasePageParam {
    //********** 额外字段 **********//
    /**
     * 待删除的id列表
     */
    private Set<Long> ids;
    /**
     * 指定的account集合
     */
    private Set<String> codeSet;
    /**
     * 搜索关键词
     */
    private String searchKey;

    //********** db中存在的字段 **********//
    /**
     * 主键id
     */
    private Long id;

    /**
     * 账号
     */
    @NotBlank(message = "用户账号account不能为空")
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 姓名
     */
    @NotBlank(message = "用户姓名name不能为空")
    private String name;

    /**
     * 性别(字典 0未知 1男 2女)
     */
    @NotNull(message = "用户性别gender不能为空")
    private Integer gender;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate birthday;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机
     */
    private String phone;

    /**
     * 身份证号
     */
    private String idNo;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 员工编码
     */
    private String staffCode;

    /**
     * 员工入职日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate;

    /**
     * 直属组织编码
     */
    @NotBlank(message = "用户直属组织orgCode不能为空")
    private String orgCode;

    /**
     * 状态（0正常 1停用）
     */
    @NotNull(message = "用户状态status不能为空")
    @Min(value = 0, message = "用户状态status有效取值范围为[0,1]")
    @Max(value = 1, message = "用户状态status有效取值范围为[0,1]")
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}