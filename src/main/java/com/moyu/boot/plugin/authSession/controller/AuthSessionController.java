package com.moyu.boot.plugin.authSession.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.annotation.SysLog;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.authSession.model.param.AuthSessionParam;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionAnalysisVO;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionVO;
import com.moyu.boot.plugin.authSession.service.AuthSessionService;
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
    private AuthSessionService authSessionService;

    /**
     * 分页查询会话列表
     */
    @PostMapping("/analyse")
    public Result<AuthSessionAnalysisVO> analyse() {
        AuthSessionAnalysisVO vo = authSessionService.analyse();
        return Result.success(vo);
    }

    /**
     * 分页查询会话列表
     */
    @SysLog(module = "system", business = "会话管理", value = "查询会话列表")
    @PreAuthorize("hasRole('ROOT') || hasAuthority('auth:session:page')")
    @PostMapping("/page")
    public Result<PageData<AuthSessionVO>> pageList(@RequestBody AuthSessionParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<AuthSessionVO> pageList = authSessionService.pageList(param);
        return Result.success(pageList);
    }

    /**
     * 移除session(强退所有)
     */
    @SysLog(module = "system", business = "会话管理", value = "强退会话", response = true)
    @PreAuthorize("hasRole('ROOT') || hasAuthority('auth:session:delete')")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody AuthSessionParam param) {
        Assert.notEmpty(param.getCodes(), "删除列表codes不能为空");
        authSessionService.removeSession(param);
        return Result.success();
    }

    /**
     * 移除token(强退指定token)
     */
    @SysLog(module = "system", business = "会话管理", value = "强退令牌", response = true)
    @PreAuthorize("hasRole('ROOT') || hasAuthority('auth:session:deleteToken')")
    @PostMapping("/deleteToken")
    public Result<?> deleteToken(@RequestBody AuthSessionParam param) {
        Assert.notEmpty(param.getCodes(), "删除列表codes不能为空");
        authSessionService.removeToken(param);
        return Result.success();
    }
}
