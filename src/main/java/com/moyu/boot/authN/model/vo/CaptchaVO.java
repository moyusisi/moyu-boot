package com.moyu.boot.authN.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 验证码信息
 *
 * @author shisong
 * @since 2026-09-16
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaptchaVO {

    /**
     * 验证码缓存ID
     */
    private String captchaId;

    /**
     * 验证码图片Base64字符串
     */
    private String captchaBase64;
}
