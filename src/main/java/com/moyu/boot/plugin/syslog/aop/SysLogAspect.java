package com.moyu.boot.plugin.syslog.aop;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyu.boot.common.security.util.SecurityUtils;
import com.moyu.boot.plugin.syslog.model.entity.SysLog;
import com.moyu.boot.plugin.syslog.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 持久化日志记录切面(比打印日志的注解@Log优先级低)
 *
 * @author shisong
 * @since 2025-10-23
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 120)
@Component
public class SysLogAspect {

    @Resource
    private SysLogService sysLogService;
    /**
     * 使用spring容器中的jackson转换器,与spring的序列化保持一致
     */
    @Resource
    private ObjectMapper objectMapper;
    /**
     * httpRequest
     */
    @Resource
    private HttpServletRequest request;

    /**
     * 切点
     */
    @Pointcut("@annotation(com.moyu.boot.common.core.annotation.SysLog)")
    public void logPointcut() {
    }

    /**
     * 切面处理并保存日志
     *
     * @param joinPoint 切点
     */
    @Around("logPointcut() && @annotation(logAnnotation)")
    public Object logHandler(ProceedingJoinPoint joinPoint, com.moyu.boot.common.core.annotation.SysLog logAnnotation) throws Throwable {
        // 先从上下文中获取当前用户，防止被清理(可能为空)
        String username = SecurityUtils.getUsername();
        Object result = null;
        Exception exception = null;
        long start = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            try {
                this.saveLog(joinPoint, logAnnotation, result, exception, username, start);
            } catch (Exception e) {
                log.error("日志插件出现异常", e);
            }
        }
        return result;
    }

    private void saveLog(final JoinPoint joinPoint, com.moyu.boot.common.core.annotation.SysLog logAnnotation, Object result, final Exception e, String username, long start) throws Exception {
        SysLog sysLog = new SysLog();
        // 操作人
        sysLog.setCreateBy(username);
        // 业务时间
        sysLog.setStartTime(new Date(start));
        long end = System.currentTimeMillis();
        sysLog.setEndTime(new Date(end));
        sysLog.setExecutionTime(end - start);

        // 业务操作
        String business = logAnnotation.business();
        String operate = logAnnotation.operate();
        if (StrUtil.isEmpty(business)) {
            // business为空时，取值类名
            business = joinPoint.getSignature().getDeclaringType().getSimpleName();
        }
        if (StrUtil.isEmpty(operate)) {
            // business为空时，取值方法名
            operate = joinPoint.getSignature().getName();
        }
        sysLog.setModule(logAnnotation.module());
        sysLog.setBusiness(business);
        sysLog.setOperate(operate);
        sysLog.setContent(logAnnotation.value());

        // 数据参数
        sysLog.setRequestUrl(request.getRequestURI());
        if (logAnnotation.request()) {
            List<Object> params = filterArgs(joinPoint.getArgs());
            String requestContent = objectMapper.writeValueAsString(params);
            sysLog.setRequestContent(StrUtil.sub(requestContent, 0, 65535));
        }
        if (logAnnotation.response() && result != null) {
            String responseContent = objectMapper.writeValueAsString(result);
            sysLog.setResponseContent(StrUtil.sub(responseContent, 0, 65535));
        }
        if (e != null) {
            sysLog.setResponseContent("发生异常");
        }
        // 保存日志到数据库
        sysLogService.save(sysLog);
    }

    /**
     * 过滤参数列表，去掉那些不应被处理的对象
     */
    private List<Object> filterArgs(Object[] args) {
        List<Object> params = new ArrayList<>();
        if (args != null) {
            for (Object param : args) {
                if (!shouldSkip(param)) {
                    params.add(param);
                }
            }
        }
        return params;
    }

    /**
     * 判断是否应跳过(有些对象不应处理)
     */
    private boolean shouldSkip(Object obj) {
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            // 是否有继承关系
            return MultipartFile.class.isAssignableFrom(clazz.getComponentType());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection<?> collection = (Collection<?>) obj;
            return collection.stream().anyMatch(item -> item instanceof MultipartFile);
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map<?, ?> map = (Map<?, ?>) obj;
            return map.values().stream().anyMatch(value -> value instanceof MultipartFile);
        }
        return obj instanceof MultipartFile || obj instanceof HttpServletRequest || obj instanceof HttpServletResponse;
    }

}
