package com.moyu.boot.system.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.annotation.SysLog;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysOrg;
import com.moyu.boot.system.model.param.SysOrgParam;
import com.moyu.boot.system.service.SysOrgService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 组织机构控制器
 *
 * @author shisong
 * @since 2024-11-28
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/sys/org")
public class SysOrgController {

    @Resource
    private SysOrgService sysOrgService;

    /**
     * 分页获取组织列表
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:page')")
    @PostMapping("/page")
    @SysLog(module = "system", value = "分页查询组织列表")
    public Result<PageData<SysOrg>> pageList(@RequestBody SysOrgParam orgParam) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(orgParam.getPageNum(), orgParam.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<SysOrg> page = sysOrgService.pageList(orgParam);
        return Result.success(page);
    }

    /**
     * 获取组织树
     */
    @Log(jsonLog = true, response = false)
    @PostMapping("/tree")
    public Result<List<Tree<String>>> tree() {
        List<Tree<String>> list = sysOrgService.tree();
        return Result.success(list);
    }

    /**
     * 获取详情
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:detail')")
    @SysLog(module = "system", value = "查询组织详情")
    @PostMapping("/detail")
    public Result<SysOrg> detail(@RequestBody SysOrgParam orgParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(orgParam.getId(), orgParam.getCode()), "id和code不能同时为空");
        return Result.success(sysOrgService.detail(orgParam));
    }

    /**
     * 添加
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:add')")
    @SysLog(module = "system", value = "新增组织机构")
    @PostMapping("/add")
    public Result<String> add(@Validated @RequestBody SysOrgParam orgParam) {
        sysOrgService.add(orgParam);
        return Result.success();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:delete')")
    @SysLog(module = "system", value = "删除组织机构")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody SysOrgParam orgParam) {
        Assert.notEmpty(orgParam.getIds(), "删除列表ids不能为空");
        sysOrgService.deleteByIds(orgParam);
        return Result.success();
    }

    /**
     * 删除树,会集联删除
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:deleteTree')")
    @SysLog(module = "system", value = "删除组织机构树")
    @PostMapping("/deleteTree")
    public Result<String> deleteTree(@RequestBody SysOrgParam orgParam) {
        Assert.notEmpty(orgParam.getCodes(), "删除列表codes不能为空");
        sysOrgService.deleteTree(orgParam);
        return Result.success();
    }

    /**
     * 编辑
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:edit')")
    @SysLog(module = "system", value = "修改组织机构")
    @PostMapping("/edit")
    public Result<String> edit(@Validated @RequestBody SysOrgParam orgParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(orgParam.getId(), orgParam.getCode()), "id和code不能同时为空");
        sysOrgService.update(orgParam);
        return Result.success();
    }

}
