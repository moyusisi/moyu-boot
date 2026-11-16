package com.moyu.boot.common.authZ.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.google.gson.Gson;
import com.moyu.boot.common.authZ.constant.AuthConstants;
import com.moyu.boot.common.authZ.model.LoginUser;
import com.moyu.boot.common.authZ.util.ExceptionWrapperUtils;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.model.Result;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
public class SaTokenConfigure implements WebMvcConfigurer {

    @Resource
    private AuthProperties properties;

    // Sa-Token 参数配置，此配置会与配置文件中的配置合并(代码配置优先) 参考文档：https://sa-token.cc/doc.html#/use/config
    @Resource
    public void configSaToken(SaTokenConfig config) {
        // token 名称（同时也是 cookie 名称）
        config.setTokenName(AuthConstants.TOKEN_NAME);
        // 指定 token 提交时的前缀
        config.setTokenPrefix(AuthConstants.TOKEN_PREFIX);
        // token 有效期（单位：秒），默认3天，-1代表永不过期
        config.setTimeout(60 * 60 * 24 * 3);
        // token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
        config.setActiveTimeout(60 * 60 * 8);
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
        // 关闭控制台banner
        config.setIsPrint(false);
    }

    // Sa-Token 整合 jwt https://sa-token.cc/doc.html#/plugin/jwt-extend
    @Bean
    @ConditionalOnProperty(value = "custom.auth.token-type", havingValue = "jwt")
    public StpLogic getStpLogicJwt() {
        // 仅Token风格替换，仍然需要Redis
        return new StpLogicJwtForSimple();
    }

    // 注册拦截器 https://sa-token.com/use/route-check.html
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 放行白名单
        List<String> whiteList = new ArrayList<>();
        // 如果没有开启认证，则全放行，否则按照白名单放行
        if (Boolean.FALSE.equals(properties.getEnabled())) {
            whiteList.add("/**");
        } else {
            whiteList.addAll(properties.getWhiteList());
        }
        // 注册拦截鉴权，但关闭拦截器注解鉴权，因为已经使用了AOP注解鉴权
        registry.addInterceptor(
                        // 执行顺序为：Filter -> 拦截器(beforeAuth -> 注解鉴权 -> auth) -> AOP注解鉴权
                        new SaInterceptor(auth -> {
                            // 确保匹配到 HandlerMethod 再鉴权， 404请求不会进到这里
                            if (auth instanceof HandlerMethod) {
                                StpUtil.checkLogin();
                            }
                        }).isAnnotation(false) // 关闭拦截器注解鉴权(使用AOP注解鉴权)
                )
                // 指定 拦截路由 与 放行路由
                .addPathPatterns(properties.getAuthList().toArray(new String[0]))
                .excludePathPatterns(whiteList.toArray(new String[0]));
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
                    // Filter中不校验登陆，仅处理已登录用户信息(在SaInterceptor中校验登陆)
                    if (StpUtil.isLogin()) {
                        // SaStorage 为请求作用域，存储的数据只在一次请求内有效。
                        SaStorage storage = SaHolder.getStorage();
                        // 向上下文中设置已认证用户.(避免覆盖)
                        if (ObjectUtil.isEmpty(storage.get(AuthConstants.LOGIN_USER))) {
                            // 从会话中获取登陆用户信息
                            LoginUser loginUser = (LoginUser) StpUtil.getTokenSession().get(AuthConstants.LOGIN_USER);
                            // 处理数据范围
                            dataScopeHandler(loginUser);
                            // loginUser放入本次请求作用域存储
                            storage.set(AuthConstants.LOGIN_USER, loginUser);
                        }
                    } else {
                        // 获取原始请求对象
                        HttpServletRequest request = (HttpServletRequest) SaHolder.getRequest().getSource();
                        // 记录日志
                        String ip = JakartaServletUtil.getClientIP(request);
                        if (ObjectUtil.isNotEmpty(ip)) {
                            log.info("Filter层，From Ip:{}, User-Agent:{}", ip, JakartaServletUtil.getHeaderIgnoreCase(request, "User-Agent"));
                        }
                        log.info("Filter层，未认证访问{}", request.getRequestURI());
                    }
                })
                // 异常处理函数：过滤器中抛出的异常无法被Advice处理
                .setError(e -> {
                    // 获取原始请求对象
                    HttpServletRequest request = (HttpServletRequest) SaHolder.getRequest().getSource();
                    HttpServletResponse response = (HttpServletResponse) SaHolder.getResponse().getSource();
                    // 设置响应头
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    // 默认返回
                    Result<?> result = new Result<>(ResultCodeEnum.SYSTEM_ERROR);
                    if (e instanceof NotLoginException) {
//                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        log.info("===== 进入Filter层异常处理，鉴权未登录:{} =====", e.getMessage());
                        // 处理登录异常，区分未认证的具体场景
                        result = ExceptionWrapperUtils.handleNotLogin((NotLoginException) e);
                    } else {
                        log.error("===== 进入Filter层异常处理 =====", e);
                    }
                    String responseBody = new Gson().toJson(result);
                    // 记录日志
                    String ip = JakartaServletUtil.getClientIP(request);
                    if (ObjectUtil.isNotEmpty(ip)) {
                        log.info("Filter层，From Ip:{}, User-Agent:{}", ip, JakartaServletUtil.getHeaderIgnoreCase(request, "User-Agent"));
                    }
                    log.info("Filter层，未认证访问{}，处理返回:{}", request.getRequestURI(), responseBody);
                    return responseBody;
                })

                // 前置函数：在每次认证函数之前执行（BeforeAuth 不受 includeList 与 excludeList 的限制，所有请求都会进入）
                .setBeforeAuth(r -> {
                    // ---------- 设置一些安全响应头 ----------
                    SaHolder.getResponse()
                            // 是否可以在iframe显示： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以
                            //.setHeader("X-Frame-Options", "SAMEORIGIN") // 不设置标识允许iframe嵌套
                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff")
                    ;
                });
    }

    @PostConstruct
    public void saTokenPostConstruct() {
        SaFirewallStrategy.instance.registerHookToFirst((req, res, extArg) -> {
            // requestPath 为null空指针情况，提前处理
            if (req.getRequestPath() == null) {
                throw new FirewallCheckException("非法请求：null");
            }
        });
    }

    private void dataScopeHandler(LoginUser loginUser) {
        // 获取原始请求对象
        HttpServletRequest request = (HttpServletRequest) SaHolder.getRequest().getSource();
        String apiUrl = request.getServletPath();
        LoginUser.DataScopeInfo dataScopeInfo = loginUser.getDataScopeMap().get(apiUrl);
        if (dataScopeInfo != null) {
            loginUser.setDataScope(dataScopeInfo.getDataScope());
            loginUser.setScopeSet(dataScopeInfo.getScopeSet());
        }
    }
}
