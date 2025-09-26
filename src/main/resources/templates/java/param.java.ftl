package ${packageName}.${moduleName}.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moyu.boot.common.core.model.BasePageParam;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * ${entityDesc}请求参数(查询、修改)
 *
 * @author ${author}
 * @since ${.now?string["yyyy-MM-dd"]}
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ${entityName}Param extends BasePageParam {

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

<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.fieldName != "id"
        && fieldConfig.fieldName != "createTime" && fieldConfig.fieldName != "updateTime"
        && fieldConfig.fieldName != "createBy" && fieldConfig.fieldName != "updateBy">
    /**
    * ${fieldConfig.fieldComment}
    */
            <#if fieldConfig.required == 1 >
                <#if fieldConfig.fieldType == 'String'>
    @NotBlank(message = "${fieldConfig.fieldName}不能为空")
                <#else>
    @NotNull(message = "${fieldConfig.fieldName}不能为空")
                </#if>
            </#if>
            <#if fieldConfig.maxLength?? >
    @Size(max=${fieldConfig.maxLength}, message="${fieldConfig.fieldName}长度不能超过${fieldConfig.maxLength}个字符")
            </#if>
            <#if fieldConfig.fieldType == 'LocalDateTime'>
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
            </#if>
    private ${fieldConfig.fieldType} ${fieldConfig.fieldName};
        </#if>
        <#-- 时间范围控制多一个字段 -->
        <#if fieldConfig.queryType == "BETWEEN">
    /**
    * ${fieldConfig.fieldComment}范围
    */
    private List<${fieldConfig.fieldType}> ${fieldConfig.fieldName}Range;
        </#if>
    </#list>
</#if>
}