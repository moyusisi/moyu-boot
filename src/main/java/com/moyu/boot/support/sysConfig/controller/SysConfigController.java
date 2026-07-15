package com.moyu.boot.support.sysConfig.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.annotation.SysLog;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.support.sysConfig.model.param.SysConfigParam;
import com.moyu.boot.support.sysConfig.model.vo.SysConfigVO;
import com.moyu.boot.support.sysConfig.service.SysConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统配置控制器
 *
 * @author moyusisi
 * @since 2026-05-28
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/sys/config")
public class SysConfigController {


    @Resource
    private SysConfigService sysConfigService;

    /**
     * 系统配置列表
     */
    //@SaCheckPermission("sys:config:list")
    @PostMapping("/list")
    public Result<List<SysConfigVO>> list(@RequestBody SysConfigParam param) {
        List<SysConfigVO> list = sysConfigService.list(param);
        return Result.success(list);
    }

    /**
     * 系统配置分页列表
     */
    //@SaCheckPermission("sys:config:page")
    @PostMapping("/page")
    public Result<PageData<SysConfigVO>> pageList(@RequestBody SysConfigParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<SysConfigVO> pageList = sysConfigService.pageList(param);
        return Result.success(pageList);
    }

    /**
     * 系统配置详情
     */
    //@SaCheckPermission("sys:config:detail")
    @PostMapping("/detail")
    public Result<SysConfigVO> detail(@RequestBody SysConfigParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        return Result.success(sysConfigService.detail(param));
    }

    /**
     * 新增系统配置
     */
    //@SaCheckPermission("sys:config:add")
    @PostMapping("/add")
    public Result<?> add(@Validated @RequestBody SysConfigParam param) {
        sysConfigService.add(param);
        return Result.success();
    }

    /**
     * 修改系统配置
     */
    //@SaCheckPermission("sys:config:edit")
    @PostMapping("/edit")
    public Result<?> edit(@Validated @RequestBody SysConfigParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        sysConfigService.update(param);
        return Result.success();
    }

    /**
     * 删除数据
     */
    @SaCheckPermission("sys:config:delete")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody SysConfigParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        sysConfigService.deleteByIds(param);
        return Result.success();
    }

    /**
     * 刷新系统配置缓存
     */
    @SysLog(value = "刷新系统配置缓存", logType = 2, module = "system", request = false, response = true)
    @SaCheckPermission("sys:config:refresh")
    @PostMapping("/refresh")
    public Result<?> refresh() {
        sysConfigService.refreshCache();
        return Result.success();
    }

}
