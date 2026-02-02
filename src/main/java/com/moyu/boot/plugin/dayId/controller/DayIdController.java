package com.moyu.boot.plugin.dayId.controller;

import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.dayId.model.vo.DayIdVO;
import com.moyu.boot.plugin.dayId.service.DayIdService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 日内标识控制器
 *
 * @author moyusisi
 * @since 2025-11-15
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/seq/day")
public class DayIdController {

    @Resource
    private DayIdService dayIdService;

    /**
     * 获取指定key(seq:day:idKey)对应的ID
     */
    @PostMapping("/list")
    public Result<List<DayIdVO>> list(@RequestParam(required = false) String keyword) {
        List<DayIdVO> list = dayIdService.list(keyword);
        return Result.success(list);
    }

    /**
     * 获取指定key(seq:day:idKey)对应的ID
     */
    @PostMapping("/currentId")
    public Result<String> currentId(@RequestParam String idKey) {
        String sn = dayIdService.getId(idKey);
        return Result.success(sn);
    }

    /**
     * 自增测试接口
     */
    @PostMapping("/inc")
    public Result<String> inc(@RequestParam String prefix) {
        String sn = dayIdService.nextId(prefix, 5);
        return Result.success(sn);
    }
}
