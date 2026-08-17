package com.moyu.boot.plugin.ThirdPartyApp.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.ThirdPartyApp.model.param.ThirdPartyAppParam;
import com.moyu.boot.plugin.ThirdPartyApp.model.vo.ThirdPartyAppVO;
import com.moyu.boot.plugin.ThirdPartyApp.service.ThirdPartyAppService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 三方应用控制器
 *
 * @author moyusisi
 * @since 2026-08-17
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/dev/thirdApp")
public class ThirdPartyAppController {


    @Resource
    private ThirdPartyAppService thirdPartyAppService;

    /**
     * 三方应用列表
     */
    //@SaCheckPermission("dev:thirdApp:list")
    @PostMapping("/list")
    public Result<List<ThirdPartyAppVO>> list(@RequestBody ThirdPartyAppParam param) {
        List<ThirdPartyAppVO> list = thirdPartyAppService.list(param);
        return Result.success(list);
    }

    /**
     * 三方应用分页列表
     */
    //@SaCheckPermission("dev:thirdApp:page")
    @PostMapping("/page")
    public Result<PageData<ThirdPartyAppVO>> pageList(@RequestBody ThirdPartyAppParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<ThirdPartyAppVO> pageList = thirdPartyAppService.pageList(param);
        return Result.success(pageList);
    }

    /**
     * 三方应用详情
     */
    //@SaCheckPermission("dev:thirdApp:detail")
    @PostMapping("/detail")
    public Result<ThirdPartyAppVO> detail(@RequestBody ThirdPartyAppParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        return Result.success(thirdPartyAppService.detail(param));
    }

    /**
     * 新增三方应用
     */
    //@SaCheckPermission("dev:thirdApp:add")
    @PostMapping("/add")
    public Result<?> add(@Validated @RequestBody ThirdPartyAppParam param) {
        thirdPartyAppService.add(param);
        return Result.success();
    }

    /**
     * 修改三方应用
     */
    //@SaCheckPermission("dev:thirdApp:edit")
    @PostMapping("/edit")
    public Result<?> edit(@Validated @RequestBody ThirdPartyAppParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        thirdPartyAppService.update(param);
        return Result.success();
    }

    /**
     * 删除数据
     */
    //@SaCheckPermission("dev:thirdApp:delete")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody ThirdPartyAppParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        thirdPartyAppService.deleteByIds(param);
        return Result.success();
    }

}
