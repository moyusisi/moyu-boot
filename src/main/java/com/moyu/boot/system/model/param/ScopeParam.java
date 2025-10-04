package com.moyu.boot.system.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.moyu.boot.common.core.model.BasePageParam;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 分组信息请求参数(查询、修改)
 *
 * @author moyusisi
 * @since 2025-10-04
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScopeParam extends BasePageParam {

    //********** 额外字段 **********//
    /**
     * 待删除的id集合
     */
    private Set<Long> ids;

    /**
     * 搜索关键词
     */
    private String searchKey;

    //********** db中存在的字段 **********//
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
    * 名称
    */
    @NotBlank(message = "name不能为空")
    @Size(max=64, message="name长度不能超过64个字符")
    private String name;
    /**
    * 编码
    */
    @Size(max=64, message="code长度不能超过64个字符")
    private String code;
    /**
    * 直属组织编码
    */
    @Size(max=64, message="orgCode长度不能超过64个字符")
    private String orgCode;
    /**
    * 直属组织名称
    */
    @Size(max=64, message="orgName长度不能超过64个字符")
    private String orgName;
    /**
    * 数据范围(字典 0无限制 1本人数据 2本机构 3本机构及以下 4自定义)
    */
    private Integer dataScope;
    /**
    * 自定义scope集合,逗号分隔
    */
    @Size(max=1024, message="scopeSet长度不能超过1024个字符")
    private String scopeSet;
    /**
    * 组织机构层级路径,逗号分隔,父节点在后
    */
    @Size(max=1024, message="orgPath长度不能超过1024个字符")
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
    @Size(max=65535, message="extJson长度不能超过65535个字符")
    private String extJson;
    /**
    * 备注
    */
    @Size(max=65535, message="remark长度不能超过65535个字符")
    private String remark;
    /**
    * 删除标志（0未删除  1已删除）
    */
    private Integer deleted;
    /**
    * 创建时间范围
    */
    private List<Date> createTimeRange;
}