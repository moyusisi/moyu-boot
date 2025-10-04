package com.moyu.boot.system.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.system.model.entity.Scope;
import com.moyu.boot.system.model.param.ScopeParam;
import com.moyu.boot.system.model.vo.ScopeVO;
import com.moyu.boot.system.service.ScopeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分组信息控制器
 *
 * @author moyusisi
 * @since 2025-10-04
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/system/scope")
public class ScopeController {


    @Resource
    private ScopeService scopeService;

    /**
     * 分组信息列表
     */
//    @PreAuthorize("hasAuthority('system:scope:list')")
    @PostMapping("/list")
    public Result<List<ScopeVO>> list(@RequestBody ScopeParam param) {
        List<ScopeVO> list = scopeService.list(param);
        return Result.success(list);
    }

    /**
     * 分组信息分页列表
     */
    //@PreAuthorize("hasAuthority('system:scope:page')")
    @PostMapping("/page")
    public Result<PageData<ScopeVO>> pageList(@RequestBody ScopeParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<ScopeVO> pageList = scopeService.pageList(param);
        return Result.success(pageList);
    }

    /**
     * 分组信息详情
     */
    //@PreAuthorize("hasAuthority('system:scope:detail')")
    @PostMapping("/detail")
    public Result<ScopeVO> detail(@RequestBody ScopeParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        return Result.success(scopeService.detail(param));
    }

    /**
     * 新增分组信息
     */
    //@PreAuthorize("hasAuthority('system:scope:add')")
    @PostMapping("/add")
    public Result<?> add(@Validated @RequestBody ScopeParam param) {
        scopeService.add(param);
        return Result.success();
    }

    /**
     * 修改分组信息
     */
    //@PreAuthorize("hasAuthority('system:scope:edit')")
    @PostMapping("/edit")
    public Result<?> edit(@Validated @RequestBody ScopeParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        scopeService.update(param);
        return Result.success();
    }

    /**
     * 删除数据
     */
    //@PreAuthorize("hasAuthority('system:scope:delete')")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody ScopeParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        scopeService.deleteByIds(param);
        return Result.success();
    }

}
