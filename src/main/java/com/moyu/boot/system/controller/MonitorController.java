package com.moyu.boot.system.controller;

import com.moyu.boot.common.core.model.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shisong02
 * @since 2023-02-21
 */
@RestController
@RequestMapping(value = "/monitor")
public class MonitorController {

    @RequestMapping(value = "/alive", method = RequestMethod.GET)
    public Result<?> alive() {
        return Result.success("服务运行正常");
    }
}
