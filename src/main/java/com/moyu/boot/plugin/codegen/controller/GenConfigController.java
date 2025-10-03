package com.moyu.boot.plugin.codegen.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.moyu.boot.common.core.annotation.Log;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.plugin.codegen.model.entity.GenConfig;
import com.moyu.boot.plugin.codegen.model.param.GenConfigParam;
import com.moyu.boot.plugin.codegen.model.vo.CodePreviewVO;
import com.moyu.boot.plugin.codegen.model.vo.GenConfigInfo;
import com.moyu.boot.plugin.codegen.model.vo.TableMetaData;
import com.moyu.boot.plugin.codegen.service.GenConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 代码生成配置控制器
 *
 * @author shisong
 * @since 2025-09-15
 */
@Log(jsonLog = true)
@Slf4j
@RestController
@RequestMapping("/api/gen/config")
public class GenConfigController {

    @Resource
    private GenConfigService genConfigService;

    /**
     * 分页获取代码生成配置列表
     */
    @PostMapping("/page")
    public Result<PageData<GenConfig>> pageList(@RequestBody GenConfigParam param) {
        Assert.isTrue(ObjectUtil.isAllNotEmpty(param.getPageNum(), param.getPageSize()), "分页参数pageNum,pageSize都不能为空");
        PageData<GenConfig> page = genConfigService.pageList(param);
        return Result.success(page);
    }

    /**
     * 查询生成代码配置(无则生成)
     */
    @PostMapping("/detail")
    public Result<GenConfigInfo> configDetail(@RequestBody GenConfigParam param) {
        Assert.isTrue(!ObjectUtil.isAllEmpty(param.getId(), param.getTableName()), "id和tableName不能同时为空");
        GenConfigInfo genConfigInfo = genConfigService.configDetail(param);
        return Result.success(genConfigInfo);
    }

    /**
     * 保存生成代码配置
     */
    @PostMapping("/save")
    public Result<GenConfigInfo> saveConfig(@RequestBody GenConfigInfo param) {
        Assert.notNull(param.getId(), "id不能为空");
        genConfigService.saveConfig(param);
        return Result.success();
    }

    /**
     * 删除生成代码配置
     */
    @PostMapping("/delete")
    public Result<?> deleteConfig(@RequestBody GenConfigParam param) {
        Assert.notEmpty(param.getIds(), "删除列表ids不能为空");
        genConfigService.deleteByIds(param);
        return Result.success();
    }

    /**
     * 分页查询数据库中的表，以便导入
     */
    @PostMapping("/tablePage")
    public Result<PageData<TableMetaData>> tablePageList(@RequestBody GenConfigParam param) {
        PageData<TableMetaData> page = genConfigService.tablePageList(param);
        return Result.success(page);
    }

    /**
     * 导入表
     */
    @PostMapping("/import")
    public Result<?> importTable(@RequestBody GenConfigParam param) {
        Assert.notEmpty(param.getTableNameSet(), "tableNameSet不能为空");
        genConfigService.importTable(param.getTableNameSet());
        return Result.success();
    }

    /**
     * 从SQL导入
     */
    @PostMapping("/importSql")
    public Result<?> importSql(@RequestBody GenConfigParam param) {
        Assert.notEmpty(param.getSql(), "导入的sql不能为空");
        genConfigService.importSql(param.getSql());
        return Result.success();
    }

    /**
     * 重置表，重新生成配置
     */
    @PostMapping("/resetTable")
    public Result<?> resetTable(@RequestBody GenConfigParam param) {
        Assert.notNull(param.getId(), "id不能为空");
        genConfigService.resetTable(param);
        return Result.success();
    }

    /**
     * 预览生成的代码
     */
    @PostMapping("/preview")
    public Result<List<CodePreviewVO>> preview(@RequestBody GenConfigParam param) {
        Assert.notNull(param.getId(), "id不能为空");
        List<CodePreviewVO> codeList = genConfigService.previewCode(param);
        return Result.success(codeList);
    }

    /**
     * 预览生成的代码
     */
    @Log(response = false)
    @PostMapping("/download")
    public void downloadZip(@RequestBody GenConfigParam param, HttpServletResponse response) {
        Assert.notEmpty(param.getIds(), "ids不能为空");
        byte[] data = genConfigService.downloadZip(param);
        buildResponse(response, data);
    }

    /**
     * 构造响应结果
     */
    private void buildResponse(HttpServletResponse response, byte[] data) {
        // 设置响应头信息
        response.setStatus(HttpServletResponse.SC_OK);
        // 允许下载文件名暴露给外部，否则控制台能看到但response中取不到(默认只有6种Header可以暴露给外部)
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        // 设置下载文件名
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=download.zip");
        response.addHeader("Content-Length", "" + data.length);
        // 设置响应内容类型为ZIP
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 将ZIP文件数据写入到HTTP响应中
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            log.error("写入zip文件失败", e);
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, "下载失败");
        }
    }
}
