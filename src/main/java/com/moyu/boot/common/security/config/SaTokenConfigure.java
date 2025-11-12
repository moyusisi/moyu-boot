package com.moyu.boot.common.security.config;


import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.jwt.StpLogicJwtForStateless;
import cn.dev33.satoken.stp.StpLogic;
import com.moyu.boot.common.security.service.TokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Sa-Token 配置类
 *
 * @author shisong
 * @since 2025-11-11
 */
@Configuration
public class SaTokenConfigure {

    // Sa-Token 参数配置，此配置会与配置文件中的配置合并(代码配置优先) 参考文档：https://sa-token.cc
    @Resource
    public void configSaToken(SaTokenConfig config) {
        // token 名称（同时也是 cookie 名称）
        config.setTokenName(TokenService.TOKEN_NAME);
        // 指定 token 提交时的前缀
        config.setTokenPrefix(TokenService.TOKEN_PREFIX);
        // token 有效期（单位：秒），默认3天，-1代表永不过期
        config.setTimeout(3 * 24 * 60 * 60);
        // token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
        config.setActiveTimeout(-1);
        // 是否允许同一账号多地同时登录（为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
        config.setIsConcurrent(true);
        // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token）
        config.setIsShare(false);
        // token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
        config.setTokenStyle("simple-uuid");
        // 是否输出操作日志
        config.setIsLog(true);
    }

    // Sa-Token 整合 jwt https://sa-token.cc/doc.html#/plugin/jwt-extend
    @Bean
    @ConditionalOnProperty(value = "custom.security.session.type", havingValue = "jwt")
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForStateless();
    }
}
