package com.moyu.boot.plugin.InboxMessage.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.common.security.util.SecurityUtils;
import com.moyu.boot.plugin.InboxMessage.model.param.InboxMessageParam;
import com.moyu.boot.plugin.InboxMessage.model.vo.InboxMessageVO;
import com.moyu.boot.plugin.InboxMessage.model.vo.UserMessageVO;
import com.moyu.boot.plugin.InboxMessage.service.InboxMessageService;
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
public class InboxMessageController {


    @Resource
    private InboxMessageService inboxMessageService;

    /**
     * 站内消息列表
     */
//    @PreAuthorize("hasAuthority('dev:message:list')")
    @PostMapping("/list")
    public Result<List<InboxMessageVO>> list(@RequestBody InboxMessageParam param) {
        List<InboxMessageVO> list = inboxMessageService.list(param);
        return Result.success(list);
    }

    /**
     * 站内消息分页列表
     */
    //@PreAuthorize("hasAuthority('dev:message:page')")
    @PostMapping("/page")
    public Result<PageData<InboxMessageVO>> pageList(@RequestBody InboxMessageParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<InboxMessageVO> pageList = inboxMessageService.pageList(param);
        return Result.success(pageList);
    }

    /**
     * 站内消息详情
     */
    //@PreAuthorize("hasAuthority('dev:message:detail')")
    @PostMapping("/detail")
    public Result<InboxMessageVO> detail(@RequestBody InboxMessageParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        return Result.success(inboxMessageService.detail(param));
    }

    /**
     * 新增站内消息
     */
    //@PreAuthorize("hasAuthority('dev:message:add')")
    @PostMapping("/add")
    public Result<?> add(@Validated @RequestBody InboxMessageParam param) {
        inboxMessageService.add(param);
        return Result.success();
    }

    /**
     * 删除数据
     */
    //@PreAuthorize("hasAuthority('dev:message:delete')")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody InboxMessageParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        inboxMessageService.deleteByIds(param);
        return Result.success();
    }

    /**
     * 触达记录、阅读列表
     */
    //@PreAuthorize("hasAuthority('dev:message:page')")
    @PostMapping("/userMessagePage")
    public Result<PageData<UserMessageVO>> userMessagePage(@RequestBody InboxMessageParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<UserMessageVO> page = inboxMessageService.userMessagePage(param);
        return Result.success(page);
    }

    /**
     * 阅读消息(需登录)
     */
    @SaCheckLogin
    @PostMapping("/read")
    public Result<InboxMessageVO> read(@RequestBody InboxMessageParam param) {
        Assert.isTrue(ObjectUtil.isNotEmpty(param.getId()), "id不能为空");
        Assert.notEmpty(SecurityUtils.getUsername(), "用户ID不能为空");
        return Result.success(inboxMessageService.read(param));
    }

    /**
     * 未读消息数量(需登录)
     */
    @SaCheckLogin
    @PostMapping("/unreadCount")
    public Result<Long> unreadCount(@RequestBody InboxMessageParam param) {
        Assert.notEmpty(SecurityUtils.getUsername(), "用户ID不能为空");
        Long unreadCount = inboxMessageService.unreadCount(param);
        return Result.success(unreadCount);
    }

    /**
     * 未读/已读列表(需登录)
     */
    @SaCheckLogin
    @PostMapping("/readPage")
    public Result<PageData<UserMessageVO>> userReadPage(@RequestBody InboxMessageParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        Assert.notEmpty(SecurityUtils.getUsername(), "用户ID不能为空");
        PageData<UserMessageVO> page = inboxMessageService.userReadPage(param);
        return Result.success(page);
    }
}
