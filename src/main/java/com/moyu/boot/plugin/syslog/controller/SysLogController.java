package com.moyu.boot.plugin.syslog.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.syslog.model.param.SysLogParam;
import com.moyu.boot.plugin.syslog.model.vo.SysLogVO;
import com.moyu.boot.plugin.syslog.service.SysLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统日志控制器
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/sys/log")
public class SysLogController {


    @Resource
    private SysLogService sysLogService;

    /**
     * 系统日志列表
     */
//    @PreAuthorize("hasAuthority('sys:log:list')")
    @PostMapping("/list")
    public Result<List<SysLogVO>> list(@RequestBody SysLogParam param) {
        List<SysLogVO> list = sysLogService.list(param);
        return Result.success(list);
    }

    /**
     * 系统日志分页列表
     */
    //@PreAuthorize("hasAuthority('sys:log:page')")
    @PostMapping("/page")
    public Result<PageData<SysLogVO>> pageList(@RequestBody SysLogParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<SysLogVO> pageList = sysLogService.pageList(param);
        return Result.success(pageList);
    }

    /**
     * 系统日志详情
     */
    //@PreAuthorize("hasAuthority('sys:log:detail')")
    @PostMapping("/detail")
    public Result<SysLogVO> detail(@RequestBody SysLogParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        return Result.success(sysLogService.detail(param));
    }

    /**
     * 新增系统日志
     */
    //@PreAuthorize("hasAuthority('sys:log:add')")
    @PostMapping("/add")
    public Result<?> add(@Validated @RequestBody SysLogParam param) {
        sysLogService.add(param);
        return Result.success();
    }

    /**
     * 修改系统日志
     */
    //@PreAuthorize("hasAuthority('sys:log:edit')")
    @PostMapping("/edit")
    public Result<?> edit(@Validated @RequestBody SysLogParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        sysLogService.update(param);
        return Result.success();
    }

    /**
     * 删除数据
     */
    //@PreAuthorize("hasAuthority('sys:log:delete')")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody SysLogParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        sysLogService.deleteByIds(param);
        return Result.success();
    }

}
