package com.moyu.boot.plugin.seq.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 日内序列号视图对象
 *
 * @author shisong
 * @since 2025-11-15
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DaySnVO {

    /**
     * 日内标识的idKey
     */
    private String idKey;

    /**
     * seq:day:idKey对应的value
     */
    private String idValue;

    /**
     * 序列号
     */
    private String sn;

}
