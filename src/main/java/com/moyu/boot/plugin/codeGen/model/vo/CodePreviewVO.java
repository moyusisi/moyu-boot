package com.moyu.boot.plugin.codeGen.model.vo;


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
     * 代码key，与模板文件名对应。 e.g. service.java、index.vue
     */
    private String codeKey;
    /**
     * 代码分类，标识生成的代码文件是前端还是后端。 e.g. frontend、backend
     */
    private String codeType;
    /**
     * 生成的代码文件相对路径(以src为起点) e.g. src/main/java/com/moyu/boot/system/model/entity
     */
    private String path;

    /**
     * 生成文件名称 e.g. SysUser.java
     */
    private String fileName;

    /**
     * 生成文件内容
     */
    private String content;

}