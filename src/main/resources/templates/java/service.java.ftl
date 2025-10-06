package ${packageName}.${moduleName}.service;

import com.moyu.boot.common.core.model.PageData;
import ${packageName}.${moduleName}.model.entity.${entityName};
import ${packageName}.${moduleName}.model.param.${entityName}Param;
import ${packageName}.${moduleName}.model.vo.${entityName}VO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ${entityDesc}服务类Service
 *
 * @author ${author}
 * @since ${.now?string["yyyy-MM-dd"]}
 */
public interface ${entityName}Service extends IService<${entityName}> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<${entityName}VO> list(${entityName}Param param);

    /**
     * 分页获取记录列表
     */
    PageData<${entityName}VO> pageList(${entityName}Param param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
     ${entityName}VO detail(${entityName}Param param);

    /**
     * 添加记录
     */
    void add(${entityName}Param param);

    /**
     * 修改记录(通过主键id更新)
     */
    void update(${entityName}Param param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(${entityName}Param param);
}
