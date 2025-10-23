package com.moyu.boot.plugin.syslog.aop;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 持久化日志记录切面
 *
 * @author shisong
 * @since 2025-10-23
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SysLogAspect {

    /**
     * 切点
     */
    @Pointcut("@annotation(com.moyu.boot.common.core.annotation.SysLog)")
    public void logPointcut() {
    }

}
