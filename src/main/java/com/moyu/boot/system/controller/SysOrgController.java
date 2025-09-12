package com.moyu.boot.system.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.model.BaseResponse;
import com.moyu.boot.common.core.model.PageResult;
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
    public BaseResponse<PageResult<SysOrg>> pageList(@RequestBody SysOrgParam orgParam) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(orgParam.getPageNum(), orgParam.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageResult<SysOrg> page = sysOrgService.pageList(orgParam);
        return BaseResponse.success(page);
    }

    /**
     * 获取组织树
     */
    @Log(jsonLog = true, response = false)
    @PostMapping("/tree")
    public BaseResponse<List<Tree<String>>> tree() {
        List<Tree<String>> list = sysOrgService.tree();
        return BaseResponse.success(list);
    }

    /**
     * 获取详情
     */
//    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:detail')")
    @PostMapping("/detail")
    public BaseResponse<SysOrg> detail(@RequestBody SysOrgParam orgParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(orgParam.getId(), orgParam.getCode()), "id和code不能同时为空");
        return BaseResponse.success(sysOrgService.detail(orgParam));
    }

    /**
     * 添加
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:add')")
    @PostMapping("/add")
    public BaseResponse<String> add(@Validated @RequestBody SysOrgParam orgParam) {
        sysOrgService.add(orgParam);
        return BaseResponse.success();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:delete')")
    @PostMapping("/delete")
    public BaseResponse<String> delete(@RequestBody SysOrgParam orgParam) {
        Assert.notEmpty(orgParam.getIds(), "删除列表ids不能为空");
        sysOrgService.deleteByIds(orgParam);
        return BaseResponse.success();
    }

    /**
     * 删除树,会集联删除
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:deleteTree')")
    @PostMapping("/deleteTree")
    public BaseResponse<String> deleteTree(@RequestBody SysOrgParam orgParam) {
        Assert.notEmpty(orgParam.getCodes(), "删除列表codes不能为空");
        sysOrgService.deleteTree(orgParam);
        return BaseResponse.success();
    }

    /**
     * 编辑
     */
    @PreAuthorize("hasRole('ROOT') || hasAuthority('sys:org:edit')")
    @PostMapping("/edit")
    public BaseResponse<String> edit(@Validated @RequestBody SysOrgParam orgParam) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(orgParam.getId(), orgParam.getCode()), "id和code不能同时为空");
        sysOrgService.edit(orgParam);
        return BaseResponse.success();
    }

}
