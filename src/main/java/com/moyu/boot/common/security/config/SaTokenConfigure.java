package com.moyu.boot.common.security.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.google.gson.Gson;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.model.Result;
import com.moyu.boot.common.security.service.TokenService;
import com.moyu.boot.common.security.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 配置类
 *
 * @author shisong
 * @since 2025-11-11
 */
@Slf4j
@Configuration
public class SaTokenConfigure {

    @Resource
    private SecurityProperties properties;

    // Sa-Token 参数配置，此配置会与配置文件中的配置合并(代码配置优先) 参考文档：https://sa-token.cc/doc.html#/use/config
    @Resource
    public void configSaToken(SaTokenConfig config) {
        // token 名称（同时也是 cookie 名称）
        config.setTokenName(TokenService.TOKEN_NAME);
        // 指定 token 提交时的前缀
        config.setTokenPrefix(TokenService.TOKEN_PREFIX);
        // token 有效期（单位：秒），默认2天，-1代表永不过期
        config.setTimeout(2 * 24 * 60 * 60);
        // token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
        config.setActiveTimeout(-1);
        // 是否允许同一账号多地同时登录（为 false 时新登录挤掉旧登录）
        config.setIsConcurrent(true);
        // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token）
        config.setIsShare(false);
        // 同一账号最大登录数量（只有在 isConcurrent=true, isShare=false 时才有意义）
        config.setMaxLoginCount(5);
        // token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
        config.setTokenStyle("simple-uuid");
        // 是否输出操作日志
        config.setIsLog(true);
    }

    // Sa-Token 整合 jwt https://sa-token.cc/doc.html#/plugin/jwt-extend
    @Bean
    @ConditionalOnProperty(value = "custom.security.session.type", havingValue = "jwt")
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    // 注册 Sa-Token全局过滤器 https://sa-token.cc/doc.html#/up/global-filter
    @Bean
    public SaServletFilter getSaServletFilter() {
        // 放行白名单
        List<String> whiteList = new ArrayList<>();
        // 如果没有开启认证，则全放行，否则按照白名单放行
        if (Boolean.FALSE.equals(properties.getEnabled())) {
            whiteList.add("/**");
        } else {
            whiteList.addAll(properties.getWhiteList());
        }
        return new SaServletFilter()
                // 指定 拦截路由 与 放行路由
                .addInclude(properties.getAuthList().toArray(new String[0]))
                // 放行路由
                .addExclude(whiteList.toArray(new String[0]))
                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    // log.info("===== 进入Sa-Token全局认证 =====");
                    // 登录认证 -- 拦截所有路由，并排除/user/doLogin 用于开放登录
                    // SaRouter.match("/api/**").check(r -> StpUtil.checkLogin());
                    StpUtil.checkLogin();
                })

                // 异常处理函数：过滤器中抛出的异常无法进入全局@ExceptionHandler
                .setError(e -> {
                    log.info("===== 进入Filter层异常处理 =====");
                    // 未认证时默认返回
                    Result<?> result = new Result<>(ResultCodeEnum.USER_LOGIN_EXPIRED);
                    if (e instanceof NotLoginException) {
                        // 处理登录异常，区分未认证的具体场景
                        result = LoginUtils.handleNotLogin((NotLoginException) e);
                    }
                    String responseBody = new Gson().toJson(result);
                    // 获取原始请求对象
                    HttpServletRequest request = (HttpServletRequest) SaHolder.getRequest().getSource();
                    HttpServletResponse response = (HttpServletResponse) SaHolder.getResponse().getSource();
                    // 记录日志
                    String ip = ServletUtil.getClientIP(request);
                    if (ObjectUtil.isNotEmpty(ip)) {
                        log.info("From Ip:{}, User-Agent:{}", ip, ServletUtil.getHeaderIgnoreCase(request, "User-Agent"));
                    }
                    log.info("Filter层，未认证访问{}，处理返回:{}", request.getRequestURI(), responseBody);
                    // 设置响应头
//                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    return new Gson().toJson(result);
                })

                // 前置函数：在每次认证函数之前执行（BeforeAuth 不受 includeList 与 excludeList 的限制，所有请求都会进入）
                .setBeforeAuth(r -> {
                    // ---------- 设置一些安全响应头 ----------
                    SaHolder.getResponse()
                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff")
                    ;
                });
    }
}
