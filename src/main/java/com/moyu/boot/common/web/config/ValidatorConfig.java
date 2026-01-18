package com.moyu.boot.common.web.config;


import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 校验器配置
 *
 * @author shisong
 * @since 2025-01-31
 */
@Configuration
public class ValidatorConfig {

    // 不手动定义 Validator Bean，也会校验，只是默认不开启快速失败模式
    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure()
                // 快速失败模式，遇到第一个校验失败则立即返回，false 表示校验所有参数
                .failFast(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
