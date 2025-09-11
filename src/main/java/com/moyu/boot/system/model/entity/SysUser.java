package com.moyu.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Date;

/**
 * 用户信息表
 *
 * @TableName sys_user
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUser extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    @JsonIgnore
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
    private String name;

    /**
     * 性别(字典 0未知 1男 2女)
     */
    private Integer gender;

    /**
     * 生日
     */
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
    private LocalDate entryDate;

    /**
     * 直属组织机构编码
     */
    private String orgCode;

    /**
     * 直属组织机构名称
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
    private Date loginTime;

    /**
     * 上次登陆IP
     */
    private String lastLoginIp;

    /**
     * 上次登陆时间
     */
    private Date lastLoginTime;

    /**
     * 上次密码更新时间
     */
    private Date pwdUpdateTime;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}