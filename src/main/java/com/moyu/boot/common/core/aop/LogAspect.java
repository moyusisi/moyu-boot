package com.moyu.boot.common.core.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyu.boot.common.core.annotation.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 打印参数和返回值的AOP, 拦截 Log 注解
 * 一般会在Facade上, service的处理的内容比较多,并且不会循环调用的方法上打出运行时间
 *
 * @author song.shi
 * @since 2016-06-01
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class LogAspect {

    /**
     * 使用spring容器中的jackson转换器
     */
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 拦截方法上的注解
     */
    @Around(value = "@annotation(log)")
    public Object aroundMethod(ProceedingJoinPoint pjp, Log log) throws Throwable {
        return logHandler(pjp, log);
    }

    /**
     * 拦截类上的注解
     */
    @Around(value = "@within(log)")
    public Object aroundClass(ProceedingJoinPoint pjp, Log log) throws Throwable {
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Method method = ms.getMethod();
        // 获取方法上的注解
        Log methodLog = AnnotationUtils.findAnnotation(method, Log.class);
        // 若方法上也有注解, 不再处理, 因为拦截方法的时候已经处理过了
        if (methodLog != null) {
            return pjp.proceed();
        }
        return logHandler(pjp, log);
    }

    private Object logHandler(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        // 切点处的方法签名, 有参数形如 Hello.hello(..); 无参数形如: Hello.hello()
        String signature = joinPoint.getSignature().toShortString();
        // 请求参数
        String request = "";
        if (log.jsonLog()) {
            request = objectMapper.writeValueAsString(joinPoint.getArgs());
        } else {
            request = Arrays.toString(joinPoint.getArgs());
        }
        // 打印请求参数
        if (log.request() && !log.exceptionOnly()) {
            logger.info("{}的请求参数为:{}", signature, request);
        }
        long startTime = System.currentTimeMillis();
        Object returnObject = null;
        try {
            returnObject = joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable throwable) {
            if (log.request() && log.exceptionOnly()) {
                logger.info("{}的请求参数为:{}", signature, request);
            }
            throw throwable;
        }
        // 打印响应结果
        if (log.response() && !log.exceptionOnly()) {
            if (log.jsonLog()) {
                logger.info("{}返回结果为:{}", signature, objectMapper.writeValueAsString((returnObject)));
            } else {
                logger.info("{}返回结果为:{}", signature, returnObject);
            }
        }
        // 打印运行时间
        if (log.runTime() && !log.exceptionOnly()) {
            logger.info("{}运行时间为:{}ms", signature, System.currentTimeMillis() - startTime);
        }
        return returnObject;
    }
}
