package com.moyu.boot.plugin.authSession.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.annotation.SysLog;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.authSession.model.param.AuthSessionParam;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionAnalysisVO;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionVO;
import com.moyu.boot.plugin.authSession.model.vo.SignTokenVO;
import com.moyu.boot.plugin.authSession.service.AuthSessionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
     * 会话统计
     */
    @PostMapping("/analyse")
    public Result<AuthSessionAnalysisVO> analyse() {
        AuthSessionAnalysisVO vo = authSessionService.analyse();
        return Result.success(vo);
    }

    /**
     * 分页查询会话列表
     */
    @SysLog(module = "system", business = "会话管理", value = "查询会话列表", response = true)
    @Log(jsonLog = true, response = false)
    @SaCheckPermission(value = "auth:session:page", orRole = "ROOT")
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
    @SaCheckPermission(value = "auth:session:delete", orRole = "ROOT")
    @PostMapping("/delete")
    public Result<?> delete(@RequestBody AuthSessionParam param) {
        Assert.notEmpty(param.getCodes(), "删除列表codes不能为空");
        authSessionService.removeSession(param);
        return Result.success();
    }

    /**
     * 分页查询会话列表
     */
    @SysLog(module = "system", business = "会话管理", value = "查询会话的令牌列表", response = true)
    @Log(jsonLog = true, response = false)
    @SaCheckPermission(value = "auth:session:tokenList")
    @PostMapping("/tokenList")
    public Result<List<SignTokenVO>> tokenList(@RequestBody AuthSessionParam param) {
        Assert.notEmpty(param.getLoginId(), "登陆标识loginId不能为空");
        List<SignTokenVO> tokenList = authSessionService.tokenList(param.getLoginId());
        return Result.success(tokenList);
    }

    /**
     * 移除token(强退指定token)
     */
    @SysLog(module = "system", business = "会话管理", value = "强退令牌", response = true)
    @SaCheckPermission(value = "auth:session:deleteToken", orRole = "ROOT")
    @PostMapping("/deleteToken")
    public Result<?> deleteToken(@RequestBody AuthSessionParam param) {
        Assert.notEmpty(param.getCodes(), "删除列表codes不能为空");
        authSessionService.removeToken(param);
        return Result.success();
    }

    /**
     * 续签token的activeTime
     */
    @SysLog(module = "system", business = "会话管理", value = "续签token[更新最后操作时间]", response = true)
    @Log(jsonLog = true, response = false)
    @SaCheckPermission(value = "auth:session:renewActive")
    @PostMapping("/renewActive")
    public Result<?> renewActive(@RequestBody AuthSessionParam param) {
        Assert.notEmpty(param.getTokenValue(), "tokenValue不能为空");
        authSessionService.renewActive(param.getTokenValue());
        return Result.success();
    }

}
