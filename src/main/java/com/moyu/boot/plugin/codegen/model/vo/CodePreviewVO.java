package com.moyu.boot.plugin.codegen.model.vo;


import lombok.Data;

/**
 * 代码预览VO
 *
 * @author shisong
 * @since 2025-09-22
 */
@Data
public class CodePreviewVO {
    /**
     * 文件路径 e.g. com.moyu.boot.system.model.entity
     */
    private String filePath;

    private String filePackage;

    /**
     * 生成文件名称 e.g. SysUser.java
     */
    private String fileName;

    /**
     * 生成文件内容
     */
    private String content;

}