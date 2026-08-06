package com.moyu.boot.plugin.jasypt.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.Assert;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.annotation.SysLog;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.jasypt.model.param.EncryptorParam;
import com.moyu.boot.plugin.jasypt.service.JasyptService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 加解密控制器
 *
 * @author moyusisi
 * @since 2026-08-06
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/dev/jasypt")
public class JasyptController {

    @Resource
    private JasyptService jasyptService;

    /**
     * 加密
     */
    @SysLog(module = "system", business = "加解密工具", value = "加密", response = true)
    @Log(jsonLog = true, response = false)
    @SaCheckPermission(value = "dev:jasypt:encrypt")
    @PostMapping("/encrypt")
    public Result<String> encrypt(@Validated @RequestBody EncryptorParam param) {
        Assert.notEmpty(param.getPlainText(), "要加密的内容不能为空");
        return Result.success(jasyptService.encrypt(param));
    }

    /**
     * 解密
     */
    @SysLog(module = "system", business = "加解密工具", value = "加密", response = true)
    @Log(jsonLog = true, response = false)
    @SaCheckPermission(value = "dev:jasypt:decrypt")
    @PostMapping("/decrypt")
    public Result<String> decrypt(@Validated @RequestBody EncryptorParam param) {
        Assert.notEmpty(param.getEncryptedText(), "要解密的内容不能为空");
        return Result.success(jasyptService.decrypt(param));
    }


}
