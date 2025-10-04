package ${packageName}.${moduleName}.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ${entityDesc}视图对象
 *
 * @author ${author}
 * @since ${.now?string["yyyy-MM-dd"]}
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ${entityName}VO {

<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.fieldName == "id">
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
        <#else>
    /**
     * ${fieldConfig.fieldRemark}
     */
            <#if fieldConfig.fieldType == 'LocalDateTime'>
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
            <#elseif fieldConfig.fieldType == "Date">
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
            </#if>
    private ${fieldConfig.fieldType} ${fieldConfig.fieldName};
        </#if>
    </#list>
</#if>
}