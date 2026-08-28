package ${packageName}.${moduleName}.mapper;

import ${packageName}.${moduleName}.model.entity.${entityName};
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表${tableName}(${tableComment})的数据库操作Mapper
 *
 * @author ${author}
 * @since ${.now?string["yyyy-MM-dd"]}
 */
@Mapper
public interface ${entityName}Mapper extends BaseMapper<${entityName}> {

}

