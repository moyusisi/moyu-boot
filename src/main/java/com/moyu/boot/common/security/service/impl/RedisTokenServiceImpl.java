package com.moyu.boot.common.security.service.impl;


import com.moyu.boot.common.security.model.LoginUser;
import com.moyu.boot.common.security.service.TokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * redis 令牌服务类，需要使用redis
 *
 * @author shisong
 * @since 2025-11-11
 */
@Service
@ConditionalOnProperty(value = "custom.security.session.type", havingValue = "redis")
public class RedisTokenServiceImpl implements TokenService {

    @Override
    public String generateToken(LoginUser loginUser) {
        return "";
    }

    @Override
    public Authentication parseToken() {
        return null;
    }
}
