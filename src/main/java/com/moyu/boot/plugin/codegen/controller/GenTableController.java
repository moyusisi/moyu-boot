package com.moyu.boot.plugin.codegen.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.codegen.model.entity.GenTable;
import com.moyu.boot.plugin.codegen.model.param.GenTableParam;
import com.moyu.boot.plugin.codegen.service.GenTableService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 代码生成实体配置表控制器
 * @author shisong
 * @since 2025-09-15
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/gen/table")
public class GenTableController {

    @Resource
    private GenTableService genTableService;

    /**
     * 分页获取实体配置列表
     */
    @PostMapping("/page")
    public Result<PageData<GenTable>> pageList(@RequestBody GenTableParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<GenTable> page = genTableService.pageList(param);
        return Result.success(page);
    }

    /**
     * 获取详情
     */
    @PostMapping("/detail")
    public Result<GenTable> detail(@RequestBody GenTableParam param) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(param.getId(), param.getTableName()), "id和tableName不能同时为空");
        return Result.success(genTableService.detail(param));
    }

    /**
     * 添加
     */
    @PostMapping("/add")
    public Result<String> add(@Validated @RequestBody GenTableParam param) {
        genTableService.add(param);
        return Result.success();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody GenTableParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        genTableService.deleteByIds(param);
        return Result.success();
    }

    /**
     * 编辑
     */
    @PostMapping("/edit")
    public Result<String> edit(@Validated @RequestBody GenTableParam param) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(param.getId(), param.getTableName()), "id和tableName不能同时为空");
        genTableService.edit(param);
        return Result.success();
    }

}
