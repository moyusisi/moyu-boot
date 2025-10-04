package ${packageName}.${moduleName}.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ${entityDesc}实体对象
 *
 * @author ${author}
 * @since ${.now?string["yyyy-MM-dd"]}
 */
@Getter
@Setter
@TableName("${tableName}")
public class ${entityName} extends BaseEntity {

<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.fieldName != "id" && fieldConfig.fieldName != "deleted"
            && fieldConfig.fieldName != "createTime" && fieldConfig.fieldName != "updateTime"
            && fieldConfig.fieldName != "createBy" && fieldConfig.fieldName != "updateBy">
    /**
    * ${fieldConfig.fieldRemark}
    */
    private ${fieldConfig.fieldType} ${fieldConfig.fieldName};
        </#if>
    </#list>
</#if>

}
