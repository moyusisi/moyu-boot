package com.moyu.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位信息表(sys_group)实体对象
 *
 * @author moyusisi
 * @since 2025-10-06
 */
@Getter
@Setter
@TableName(value = "sys_group")
public class SysGroup extends BaseEntity {
    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 直属组织
     */
    private String orgCode;

    /**
     * 直属组织名称
     */
    private String orgName;

    /**
     * 数据范围(字典 0无限制 1本人数据 2本机构 3本机构及以下 4自定义)
     *
     * @see DataScopeEnum
     */
    private Integer dataScope;

    /**
     * 自定义scope集合,逗号分隔
     */
    private String scopeSet;

    /**
     * 组织机构层级路径,逗号分隔,父节点在后
     */
    private String orgPath;

    /**
     * 排序顺序
     */
    private Integer sortNum;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 扩展信息
     */
    private String extJson;

    /**
     * 备注
     */
    private String remark;

}