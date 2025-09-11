package com.moyu.boot.system.constant;


import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * 系统常量
 *
 * @author shisong
 * @since 2024-12-27
 */
public interface SysConstants {
    /**
     * 逗号分隔
     */
    Splitter COMMA_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();
    /**
     * 逗号连接器
     */
    Joiner COMMA_JOINER = Joiner.on(",").skipNulls();

    /**
     * 组织机构根节点ID
     */
    String ROOT_NODE_ID = "0";

    /**
     * 系统默认密码
     */
    String DEFAULT_PASSWORD = "qwer@123!";

    // 自动生成code时的前缀
    String GROUP_PREFIX = "G";
    String SCOPE_PREFIX = "S";
    String ROLE_PREFIX = "R";
}
