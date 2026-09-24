package com.moyu.boot.plugin.seq.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.seq.model.vo.DaySnVO;
import com.moyu.boot.plugin.seq.service.DaySnService;
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
public class DaySnController {

    @Resource
    private DaySnService daySnService;

    /**
     * 获取指定key(seq:day:idKey)对应的ID
     */
    @PostMapping("/list")
    public Result<List<DaySnVO>> list(@RequestParam(required = false) String idKey) {
        List<DaySnVO> list = daySnService.list(idKey);
        return Result.success(list);
    }

    /**
     * 获取指定key(seq:day:idKey)对应的ID
     */
    @PostMapping("/currentId")
    public Result<Long> currentId(@RequestParam String idKey) {
        Long sn = daySnService.getIdValue(idKey);
        return Result.success(sn);
    }

    /**
     * 自增测试接口
     */
    @SaCheckPermission("dev:seq:inc")
    @PostMapping("/inc")
    public Result<String> inc(@RequestParam String prefix) {
        String sn = daySnService.nextId(prefix, 5);
        return Result.success(sn);
    }
}
