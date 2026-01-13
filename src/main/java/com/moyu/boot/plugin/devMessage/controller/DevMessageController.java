package com.moyu.boot.plugin.devMessage.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.devMessage.model.param.DevMessageParam;
import com.moyu.boot.plugin.devMessage.model.vo.DevMessageVO;
import com.moyu.boot.plugin.devMessage.service.DevMessageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 站内消息控制器
 *
 * @author moyusisi
 * @since 2026-01-13
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/dev/message")
public class DevMessageController {


    @Resource
    private DevMessageService devMessageService;

    /**
     * 站内消息列表
     */
//    @PreAuthorize("hasAuthority('dev:message:list')")
    @PostMapping("/list")
    public Result<List<DevMessageVO>> list(@RequestBody DevMessageParam param) {
        List<DevMessageVO> list = devMessageService.list(param);
        return Result.success(list);
    }

    /**
     * 站内消息分页列表
     */
    //@PreAuthorize("hasAuthority('dev:message:page')")
    @PostMapping("/page")
    public Result<PageData<DevMessageVO>> pageList(@RequestBody DevMessageParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<DevMessageVO> pageList = devMessageService.pageList(param);
        return Result.success(pageList);
    }

    /**
     * 站内消息详情
     */
    //@PreAuthorize("hasAuthority('dev:message:detail')")
    @PostMapping("/detail")
    public Result<DevMessageVO> detail(@RequestBody DevMessageParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        return Result.success(devMessageService.detail(param));
    }

    /**
     * 新增站内消息
     */
    //@PreAuthorize("hasAuthority('dev:message:add')")
    @PostMapping("/add")
    public Result<?> add(@Validated @RequestBody DevMessageParam param) {
        devMessageService.add(param);
        return Result.success();
    }

    /**
     * 阅读站内信消息
     */
//    @PreAuthorize("hasAuthority('dev:message:read')")
    @PostMapping("/read")
    public Result<DevMessageVO> read(@RequestBody DevMessageParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        return Result.success(devMessageService.read(param));
    }

    /**
     * 删除数据
     */
    //@PreAuthorize("hasAuthority('dev:message:delete')")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody DevMessageParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        devMessageService.deleteByIds(param);
        return Result.success();
    }

}
