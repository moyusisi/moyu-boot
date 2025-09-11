package com.moyu.boot.common.security.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import com.moyu.boot.common.security.model.LoginUser;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JWT工具类
 *
 * @author shisong
 * @since 2025-01-23
 */
@Slf4j
public class JwtUtils {
    /**
     * jwt秘钥，至少256-bit(32-byte)，如：7nPXLm0zLVdqKM5QTb03ahcRiWzoC2UC
     * RandomUtil.randomString(32)
     */
    public static final String SECRET = "7nPXLm0zLVdqKM5QTb03ahcRiWzoC2UC";

    /**
     * 生成token
     * 参考 <a href="https://hutool.cn/docs/#/jwt/%E6%A6%82%E8%BF%B0">这里</a>
     */
    public static String createToken(LoginUser loginUser) {
        String token = JWT.create()
                .setSubject(loginUser.getUsername())
                .setJWTId(IdUtil.fastSimpleUUID())
                .setPayload("orgCode", loginUser.getOrgCode())
                .setPayload("groupCode", loginUser.getGroupCode())
                .setPayload("dataScope", loginUser.getDataScope())
                .setPayload("scopes", loginUser.getScopes())
                .setPayload("perms", loginUser.getPerms())
                .setPayload("roles", loginUser.getRoles())
                .setKey(SECRET.getBytes())
                .sign();
        return token;
    }

    /**
     * 从http请求中获取token
     */
    public static LoginUser getLoginUserFromToken(String token) {
        // 解析jwt token
        JWT jwt = JWT.of(token);
        // 根据token获取claims
        String username = (String) jwt.getPayload(JWTPayload.SUBJECT);
        String orgCode = (String) jwt.getPayload("orgCode");
        String groupCode = (String) jwt.getPayload("groupCode");
        Integer dataScope = ObjectUtil.isEmpty(jwt.getPayload("dataScope")) ? null : ((Number) jwt.getPayload("dataScope")).intValue();
        Set<String> scopes = ObjectUtil.isEmpty(jwt.getPayload("scopes")) ? new HashSet<>() : new HashSet<>((List<String>) jwt.getPayload("scopes"));
        Set<String> perms = ObjectUtil.isEmpty(jwt.getPayload("perms")) ? new HashSet<>() : new HashSet<>((List<String>) jwt.getPayload("perms"));
        Set<String> roles = ObjectUtil.isEmpty(jwt.getPayload("roles")) ? new HashSet<>() : new HashSet<>((List<String>) jwt.getPayload("roles"));
        // 转换成登录用户
        LoginUser loginUser = LoginUser.builder().enabled(true).username(username)
                .orgCode(orgCode).groupCode(groupCode).dataScope(dataScope).scopes(scopes)
                .perms(perms).roles(roles).build();
        // 初始化authorities后才可使用springSecurity鉴权
        loginUser.initAuthorities();
        // 返回当前登陆用户
        return loginUser;
    }

    /**
     * 仅验证签名是否有效
     */
    public static String getId(String token) {
        return (String) JWT.of(token).getPayload(JWTPayload.JWT_ID);
    }

    /**
     * 仅验证签名是否有效
     */
    public static boolean verifyToken(String token) {
        // 默认验证HS265的算法
        return JWT.of(token).setKey(SECRET.getBytes()).verify();
    }

    /**
     * 验证签名和时间，一般时间线是：(签发时间)---(生效时间)---(当前时间)---(失效时间)
     */
    public static boolean checkToken(String token) {
        return JWT.of(token).setKey(SECRET.getBytes()).validate(0);
    }
}
