package com.moyu.boot.plugin.intradayId.controller;

import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.intradayId.service.DayIdService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 日内标识控制器
 *
 * @author moyusisi
 * @since 2025-11-15
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/dev/seq")
public class DayIdController {

    @Resource
    private DayIdService dayIdService;

    /**
     * 自增测试接口
     */
    @PostMapping("/inc")
    public Result<String> inc() {
        String sn = dayIdService.nextId("SN", 5);
        return Result.success(sn);
    }
}
