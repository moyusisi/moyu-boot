package com.moyu.boot.plugin.thirdPartyApi.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.thirdPartyApi.model.param.ThirdPartyApiParam;
import com.moyu.boot.plugin.thirdPartyApi.model.vo.ThirdPartyApiVO;
import com.moyu.boot.plugin.thirdPartyApi.service.ThirdPartyApiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 三方集成接口控制器
 *
 * @author moyusisi
 * @since 2026-08-06
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/dev/thirdPartyApi")
public class ThirdPartyApiController {


    @Resource
    private ThirdPartyApiService thirdPartyApiService;

    /**
     * 三方集成接口分页列表
     */
    //@SaCheckPermission("dev:thirdPartyApi:page")
    @PostMapping("/page")
    public Result<PageData<ThirdPartyApiVO>> pageList(@RequestBody ThirdPartyApiParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<ThirdPartyApiVO> pageList = thirdPartyApiService.pageList(param);
        return Result.success(pageList);
    }

    /**
     * 三方集成接口详情
     */
    //@SaCheckPermission("dev:thirdPartyApi:detail")
    @PostMapping("/detail")
    public Result<ThirdPartyApiVO> detail(@RequestBody ThirdPartyApiParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        return Result.success(thirdPartyApiService.detail(param));
    }

    /**
     * 新增三方集成接口
     */
    //@SaCheckPermission("dev:thirdPartyApi:add")
    @PostMapping("/add")
    public Result<?> add(@Validated @RequestBody ThirdPartyApiParam param) {
        thirdPartyApiService.add(param);
        return Result.success();
    }

    /**
     * 修改三方集成接口
     */
    //@SaCheckPermission("dev:thirdPartyApi:edit")
    @PostMapping("/edit")
    public Result<?> edit(@Validated @RequestBody ThirdPartyApiParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        thirdPartyApiService.update(param);
        return Result.success();
    }

    /**
     * 删除数据
     */
    //@SaCheckPermission("dev:thirdPartyApi:delete")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody ThirdPartyApiParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        thirdPartyApiService.deleteByIds(param);
        return Result.success();
    }

}
