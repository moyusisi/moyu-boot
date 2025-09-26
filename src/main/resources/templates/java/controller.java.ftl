package ${packageName}.${moduleName}.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import ${packageName}.${moduleName}.model.entity.${entityName};
import ${packageName}.${moduleName}.model.param.${entityName}Param;
import ${packageName}.${moduleName}.model.vo.${entityName}VO;
import ${packageName}.${moduleName}.service.${entityName}Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * ${entityDesc}控制器
 *
 * @author ${author}
 * @since ${.now?string["yyyy-MM-dd"]}
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/${moduleName}/${entityName?uncap_first}")
public class ${entityName}Controller {


    @Resource
    private ${entityName}Service ${entityName?uncap_first}Service;

    /**
     * ${entityDesc}列表
     */
//    @PreAuthorize("hasAuthority('${moduleName}:${entityName?uncap_first}:list')")
    @PostMapping("/list")
    public Result<List<${entityName}VO>> list(@RequestBody ${entityName}Param param) {
        List<${entityName}VO> list = ${entityName?uncap_first}Service.list(param);
        return Result.success(list);
    }

    /**
     * ${entityDesc}分页列表
     */
    //@PreAuthorize("hasAuthority('${moduleName}:${entityName?uncap_first}:page')")
    @PostMapping("/page")
    public Result<PageData<${entityName}VO>> pageList(@RequestBody ${entityName}Param param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<${entityName}VO> pageList = ${entityName?uncap_first}Service.pageList(param);
        return Result.success(pageList);
    }

    /**
     * ${entityDesc}详情
     */
    //@PreAuthorize("hasAuthority('${moduleName}:${entityName?uncap_first}:detail')")
    @PostMapping("/detail")
    public Result<${entityName}VO> detail(@RequestBody ${entityName}Param param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        return Result.success(${entityName?uncap_first}Service.detail(param));
    }

    /**
     * 新增${entityDesc}
     */
    //@PreAuthorize("hasAuthority('${moduleName}:${entityName?uncap_first}:add')")
    @PostMapping("/add")
    public Result<?> add(@Validated @RequestBody ${entityName}Param param) {
        ${entityName?uncap_first}Service.add(param);
        return Result.success();
    }

    /**
     * 修改${entityDesc}
     */
    //@PreAuthorize("hasAuthority('${moduleName}:${entityName?uncap_first}:edit')")
    @PostMapping("/edit")
    public Result<?> edit(@Validated @RequestBody ${entityName}Param param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        ${entityName?uncap_first}Service.update(param);
        return Result.success();
    }

    /**
     * 删除数据
     */
    //@PreAuthorize("hasAuthority('${moduleName}:${entityName?uncap_first}:delete')")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody ${entityName}Param param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        ${entityName?uncap_first}Service.deleteByIds(param);
        return Result.success();
    }

}
