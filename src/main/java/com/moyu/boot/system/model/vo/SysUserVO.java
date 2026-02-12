package com.moyu.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息视图对象
 *
 * @author moyusisi
 * @since 2026-02-12
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUserVO {
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 用户唯一id
     */
    private String userId;
    /**
     * 账号
     */
    private String account;
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
    private String name;
    /**
     * 性别(字典 0未知 1男 2女)
     */
    private Integer gender;
    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date entryDate;
    /**
     * 直属组织编码
     */
    private String orgCode;
    /**
     * 直属组织名称
     */
    private String orgName;
    /**
     * 组织机构层级路径,逗号分隔,父节点在后
     */
    private String orgPath;
    /**
     * 登陆IP
     */
    private String loginIp;
    /**
     * 登陆时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    /**
     * 上次登陆IP
     */
    private String lastLoginIp;
    /**
     * 上次登陆时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;
    /**
     * 上次密码更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pwdUpdateTime;
    /**
     * 状态（0正常 1停用）
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 修改人
     */
    private String updateBy;
}