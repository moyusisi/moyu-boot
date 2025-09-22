package com.moyu.boot.plugin.codegen.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.codegen.model.entity.GenConfig;
import com.moyu.boot.plugin.codegen.model.param.GenConfigParam;
import com.moyu.boot.plugin.codegen.model.param.TableQueryParam;
import com.moyu.boot.plugin.codegen.model.vo.GenConfigInfo;
import com.moyu.boot.plugin.codegen.model.vo.TableMetaData;
import com.moyu.boot.plugin.codegen.service.GenConfigService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 代码生成配置控制器
 *
 * @author shisong
 * @since 2025-09-15
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/gen/config")
public class GenConfigController {

    @Resource
    private GenConfigService genConfigService;

    /**
     * 分页获取代码生成配置列表
     */
    @PostMapping("/page")
    public Result<PageData<GenConfig>> pageList(@RequestBody GenConfigParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<GenConfig> page = genConfigService.pageList(param);
        return Result.success(page);
    }

    /**
     * 查询生成代码配置(无则生成)
     */
    @PostMapping("/detail")
    public Result<GenConfigInfo> configDetail(@RequestBody TableQueryParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getTableName()), "tableName不能为空");
        GenConfigInfo genConfigInfo = genConfigService.getConfigDetail(param.getTableName());
        return Result.success(genConfigInfo);
    }

    /**
     * 保存生成代码配置
     */
    @PostMapping("/save")
    public Result<GenConfigInfo> saveConfig(@RequestBody GenConfigInfo param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getTableName()), "tableName不能为空");
        genConfigService.saveConfig(param);
        return Result.success();
    }

    /**
     * 删除生成代码配置
     */
    @PostMapping("/delete")
    public Result<GenConfigInfo> deleteConfig(@RequestBody GenConfigParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        genConfigService.deleteByIds(param);
        return Result.success();
    }

    /**
     * 导入表
     */
    @PostMapping("/import")
    public Result<PageData<TableMetaData>> importTable(@RequestBody GenConfigParam param) {
        Assert.notEmpty(param.getTableNameSet(), "tableNameSet不能为空");
        genConfigService.importTable(param.getTableNameSet());
        return Result.success();
    }

}
