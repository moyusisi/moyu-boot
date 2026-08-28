package ${packageName}.${moduleName}.model.entity;

import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.mybatis.handler.CustomInsertListener;
import com.moyu.boot.common.mybatis.handler.CustomUpdateListener;
import com.mybatisflex.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ${entityDesc}表(${tableName})实体对象
 *
 * @author ${author}
 * @since ${.now?string["yyyy-MM-dd"]}
 */
@Getter
@Setter
@Table(value = "${tableName}", onInsert = CustomInsertListener.class, onUpdate = CustomUpdateListener.class)
public class ${entityName} extends BaseEntity {

<#if fieldList??>
    <#list fieldList as fieldConfig>
        <#if fieldConfig.fieldName == "id">
    /**
     * 主键id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
        <#elseif fieldConfig.fieldName == "deleted">
    /**
     * 删除标志（0未删除  1已删除）
     * isLogicDelete逻辑删除标记 deleteById时会更新,查询时会过滤
     */
    @Column(isLogicDelete = true)
    private Integer deleted;
        <#elseif fieldConfig.fieldName != "createTime" && fieldConfig.fieldName != "updateTime"
            && fieldConfig.fieldName != "createBy" && fieldConfig.fieldName != "updateBy">
    /**
     * ${fieldConfig.fieldRemark}
     */
    private ${fieldConfig.fieldType} ${fieldConfig.fieldName};
        </#if>
    </#list>
</#if>

}
