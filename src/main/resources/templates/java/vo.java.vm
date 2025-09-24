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

import java.math.BigDecimal;
import java.util.Date;

/**
 * $!{entityDesc}视图对象
 *
 * @author ${author}
 * @since ${date}
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ${entityName}VO {

    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

#foreach($fieldConfig in ${fieldList})
    #if($fieldConfig.fieldName.equals("id"))
    /**
     * 主键id
     * 注意Long值传递给前端精度丢失问题（JS最大精度整数是Math.pow(2,53)）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    #else
        #if("$!fieldConfig.fieldComment" != "")
    /**
     * ${fieldConfig.fieldComment}
     */
        #end
        #if($fieldConfig.fieldType == 'LocalDateTime')
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        #end
    private ${fieldConfig.fieldType} ${fieldConfig.fieldName};
    #end
#end
}