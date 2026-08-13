package com.moyu.boot.common.web.filter;

/**
 * @author shisong
 * @since 2026-08-13
 */

import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * MDC 链路追踪过滤器
 * Spring会自动识别 Filter 类型 Bean，注册到 Servlet 过滤器链
 */
@Component
public class MdcTraceFilter extends OncePerRequestFilter {

    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取traceId，无则生成
        String traceId = request.getHeader(HEADER_TRACE_ID);
        if (StrUtil.isBlank(traceId)) {
            // 生成链路ID
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        try {
            // 填充MDC上下文
            MDC.put("traceId", traceId);
            // 响应头回写traceId，前端/下游可获取
            response.setHeader(HEADER_TRACE_ID, traceId);
            // 放行请求
            filterChain.doFilter(request, response);
        } finally {
            // 强制清空MDC 防止线程池复用，残留旧上下文
            MDC.clear();
        }
    }
}