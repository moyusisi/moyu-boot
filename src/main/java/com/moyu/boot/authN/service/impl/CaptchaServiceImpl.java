package com.moyu.boot.authN.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.moyu.boot.authN.model.vo.CaptchaVO;
import com.moyu.boot.authN.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 图片验证码服务实现类
 *
 * @author shisong
 * @since 2026-09-16
 */
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public CaptchaVO generate() {
        // 创建扭曲干扰验证码画布（也可用 LineCaptcha / CircleCaptcha / GifCaptcha）
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 80);
        // 自定义验证码内容为四则运算方式
        captcha.setGenerator(new RandomGenerator("0123456789", 4));
        captcha.createCode();
        // 获取验证码
        String captchaCode = captcha.getCode();
        // 生成唯一ID
        String captchaId = IdUtil.objectId();
        // 存入Redis
        stringRedisTemplate.opsForValue().set("CAPTCHA:" + captchaId, captchaCode, 300, TimeUnit.SECONDS);
        // 图片转Base64返回前端
        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaId(captchaId);
        vo.setCaptchaBase64(captcha.getImageBase64Data());
        return vo;
    }

    @Override
    public boolean verify(String captchaId, String captchaCode) {
        // 为空验证失败
        if (StrUtil.isEmpty(captchaId) || StrUtil.isEmpty(captchaCode)) {
            return false;
        }
        String redisKey = "CAPTCHA:" + captchaId;
        // 从Redis取出验证码
        String realCode = stringRedisTemplate.opsForValue().get(redisKey);

        // 校验失败：过期 / 答案错误
        if (realCode == null || !realCode.equals(captchaCode)) {
            return false;
        }
        // 校验通过，立即删除（一次性使用，防止重复刷接口）
        stringRedisTemplate.delete(redisKey);
        return true;
    }
}
