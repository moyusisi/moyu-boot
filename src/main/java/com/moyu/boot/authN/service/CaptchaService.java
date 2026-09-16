package com.moyu.boot.authN.service;

import com.moyu.boot.authN.model.vo.CaptchaVO;

/**
 * 图片验证码服务
 *
 * @author shisong
 * @since 2026-09-16
 */
public interface CaptchaService {

    /**
     * 生成验证码
     */
    CaptchaVO generate();

    /**
     * 校验验证码
     *
     * @return 成功返回true
     */
    boolean verify(String captchaId, String captchaCode);
}
