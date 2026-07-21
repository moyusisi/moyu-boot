package com.moyu.boot.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.system.model.param.SysApiParam;
import com.moyu.boot.system.model.vo.SysApiVO;
import com.moyu.boot.system.service.SysApiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 接口信息控制器
 *
 * @author moyusisi
 * @since 2026-07-20
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/sys/api")
public class SysApiController {


    @Resource
    private SysApiService sysApiService;

    /**
     * 接口信息列表
     */
    //@SaCheckPermission("sys:api:list")
    @PostMapping("/list")
    public Result<List<SysApiVO>> list(@RequestBody SysApiParam param) {
        List<SysApiVO> list = sysApiService.list(param);
        return Result.success(list);
    }

    /**
     * 接口信息分页列表
     */
    //@SaCheckPermission("sys:api:page")
    @PostMapping("/page")
    public Result<PageData<SysApiVO>> pageList(@RequestBody SysApiParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<SysApiVO> pageList = sysApiService.pageList(param);
        return Result.success(pageList);
    }

    /**
     * 接口信息详情
     */
    //@SaCheckPermission("sys:api:detail")
    @PostMapping("/detail")
    public Result<SysApiVO> detail(@RequestBody SysApiParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        return Result.success(sysApiService.detail(param));
    }

    /**
     * 新增接口信息
     */
    @SaCheckPermission("sys:api:add")
    @PostMapping("/add")
    public Result<?> add(@Validated @RequestBody SysApiParam param) {
        sysApiService.add(param);
        return Result.success();
    }

    /**
     * 修改接口信息
     */
    @SaCheckPermission("sys:api:edit")
    @PostMapping("/edit")
    public Result<?> edit(@Validated @RequestBody SysApiParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        sysApiService.update(param);
        return Result.success();
    }

    /**
     * 删除数据
     */
    @SaCheckPermission("sys:api:delete")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody SysApiParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        sysApiService.deleteByIds(param);
        return Result.success();
    }

}
