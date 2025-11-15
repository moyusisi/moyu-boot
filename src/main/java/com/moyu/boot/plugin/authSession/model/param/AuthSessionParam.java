package com.moyu.boot.plugin.authSession.model.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyu.boot.common.core.model.BasePageParam;
import lombok.Data;

import java.util.Set;

/**
 * 会话管理的请求参数
 *
 * @author moyusisi
 * @since 2025-10-22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthSessionParam extends BasePageParam {

    /**
     * 待删除的唯一键集合
     */
    private Set<String> codes;

    /**
     * 搜索关键词
     */
    private String searchKey;

    /**
     * 账号
     */
    private String account;

    /**
     * 姓名
     */
    private String name;

}