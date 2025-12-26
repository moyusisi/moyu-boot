package com.moyu.boot.system.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.annotation.SysLog;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.system.model.entity.SysResource;
import com.moyu.boot.system.model.param.SysResourceParam;
import com.moyu.boot.system.service.SysResourceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 资源菜单权限控制器
 *
 * @author shisong
 * @since 2024-12-11
 */
@Log(jsonLog = true)
@RestController
@RequestMapping("/api/sys/resource")
public class SysResourceController {


    @Resource
    private SysResourceService sysResourceService;

    /**
     * 资源列表
     */
    @PostMapping("/list")
    public Result<List<SysResource>> list(@RequestBody SysResourceParam resourceParam) {
        List<SysResource> list = sysResourceService.list(resourceParam);
        return Result.success(list);
    }

    /**
     * 资源分页列表
     */
    @Log(jsonLog = true, response = false)
    @SysLog(module = "system", value = "查询资源列表")
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:resource:page')")
    @PostMapping("/page")
    public Result<PageData<SysResource>> pageList(@RequestBody SysResourceParam resourceParam) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(resourceParam.getPageNum(), resourceParam.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<SysResource> list = sysResourceService.pageList(resourceParam);
        return Result.success(list);
    }

    /**
     * 获取资源树(可指定module、status)
     */
    @SysLog(module = "system", value = "获取资源树")
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:resource:tree')")
    @Log(jsonLog = true, response = false)
    @PostMapping("/tree")
    public Result<List<Tree<String>>> tree(@RequestBody SysResourceParam resourceParam) {
        List<Tree<String>> treeList = sysResourceService.tree(resourceParam);
        return Result.success(treeList);
    }

    /**
     * 获取资源详情
     */
    @SysLog(module = "system", value = "查询资源详情")
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:resource:detail')")
    @PostMapping("/detail")
    public Result<SysResource> detail(@RequestBody SysResourceParam resourceParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(resourceParam.getId(), resourceParam.getCode()), "id和code不能同时为空");
        return Result.success(sysResourceService.detail(resourceParam));
    }

    /**
     * 添加资源
     */
    @SysLog(module = "system", value = "添加资源")
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:resource:add')")
    @PostMapping("/add")
    public Result<String> add(@RequestBody SysResourceParam resourceParam) {
        sysResourceService.add(resourceParam);
        return Result.success();
    }

    /**
     * 删除资源
     */
    @SysLog(module = "system", value = "删除资源")
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:resource:delete')")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody SysResourceParam resourceParam) {
        Assert.notEmpty(resourceParam.getIds(), "删除列表ids不能为空");
        sysResourceService.deleteByIds(resourceParam);
        return Result.success();
    }

    /**
     * 删除资源树,会集联删除
     */
    @SysLog(module = "system", value = "集联删除资源树")
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:resource:deleteTree')")
    @PostMapping("/deleteTree")
    public Result<String> deleteTree(@RequestBody SysResourceParam resourceParam) {
        Assert.notEmpty(resourceParam.getCodes(), "删除列表codes不能为空");
        sysResourceService.deleteTree(resourceParam);
        return Result.success();
    }

    /**
     * 编辑资源
     */
    @SysLog(module = "system", value = "修改资源信息")
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:resource:edit')")
    @PostMapping("/edit")
    public Result<String> edit(@RequestBody SysResourceParam resourceParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(resourceParam.getId(), resourceParam.getCode()), "id和code不能同时为空");
        sysResourceService.update(resourceParam);
        return Result.success();
    }

    /**
     * 获取菜单树选择器
     */
    @PostMapping("/menuTreeSelector")
    public Result<List<Tree<String>>> menuTreeSelector(@RequestBody SysResourceParam resourceParam) {
        return Result.success(sysResourceService.menuTreeSelector(resourceParam));
    }

}
