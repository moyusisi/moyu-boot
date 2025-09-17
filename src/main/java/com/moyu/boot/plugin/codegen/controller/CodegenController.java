package com.moyu.boot.plugin.codegen.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.codegen.model.param.TableQueryParam;
import com.moyu.boot.plugin.codegen.model.vo.GenConfigInfo;
import com.moyu.boot.plugin.codegen.model.vo.TableMetaData;
import com.moyu.boot.plugin.codegen.service.CodegenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 代码生成器控制器
 *
 * @author shisong
 * @since 2025-09-14
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/gen/code")
public class CodegenController {

    @Resource
    private CodegenService codegenService;

    /**
     * 代码生成分页列表
     */
    @PostMapping("/page")
    public Result<PageData<TableMetaData>> pageList(@RequestBody TableQueryParam param) {
        PageData<TableMetaData> page = codegenService.tablePageList(param);
        return Result.success(page);
    }

    /**
     * 查询某个表的配置(无则生成)
     */
    @PostMapping("/config")
    public Result<GenConfigInfo> config(@RequestBody TableQueryParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getTableName()), "tableName不能为空");
        GenConfigInfo genConfigInfo = codegenService.configDetail(param.getTableName());
        return Result.success(genConfigInfo);
    }

}
