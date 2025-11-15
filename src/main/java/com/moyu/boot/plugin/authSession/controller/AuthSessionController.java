package com.moyu.boot.plugin.authSession.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.authSession.model.param.AuthSessionParam;
import com.moyu.boot.plugin.authSession.model.service.AuthSessionService;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionVO;
import com.moyu.boot.plugin.sysLog.model.param.SysLogParam;
import com.moyu.boot.plugin.sysLog.service.SysLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 会话管理控制器
 *
 * @author moyusisi
 * @since 2025-11-15
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/auth/session")
public class AuthSessionController {

    @Resource
    private SysLogService sysLogService;

    @Resource
    private AuthSessionService authSessionService;

    /**
     * 分野查询会话列表
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('auth:session:page')")
    @PostMapping("/page")
    public Result<PageData<AuthSessionVO>> pageList(@RequestBody AuthSessionParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<AuthSessionVO> pageList = authSessionService.pageList(param);
        return Result.success(pageList);
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
