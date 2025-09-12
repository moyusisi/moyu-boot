package com.moyu.boot.system.controller;

import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shisong02
 * @since 2023-02-21
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Log
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public Result<?> testIndex() {
        return Result.success("返回汉字不乱码");
    }

}
