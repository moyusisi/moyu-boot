package com.moyu.boot.common.security.aop;

import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 准备数据范围切面
 *
 * @author shisong
 * @since 2025-10-23
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 200)
@Component
public class PreDataScopeAspect {

    /**
     * 切点
     */
    @Pointcut("@annotation(com.moyu.boot.common.core.annotation.PreDataScope)")
    public void pointcut() {
    }

    /**
     * 切面处理并保存日志
     */
    @Before("pointcut() && @annotation(annotation)")
    public void logHandler(com.moyu.boot.common.core.annotation.PreDataScope annotation) {
        // 先从上下文中获取当前用户
        // 当前登陆用户
        Optional<LoginUser> optUser = SecurityUtils.getLoginUser();
        if (optUser.isPresent()) {
            LoginUser loginUser = optUser.get();
            // 获取权限标识
            String perm = annotation.value();
            LoginUser.DataScopeInfo dataScopeInfo = loginUser.getPermScopeMap().get(perm);
            if (dataScopeInfo != null) {
                loginUser.setDataScope(dataScopeInfo.getDataScope());
                loginUser.setScopeSet(dataScopeInfo.getScopeSet());
            }
        }
    }

}
