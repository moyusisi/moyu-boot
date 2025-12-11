package com.moyu.boot.common.core.enums;

import com.moyu.boot.common.core.model.IResultCode;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * 响应码枚举，参考阿里巴巴开发手册错误码
 * <p>
 * 00000 正常
 * A**** 用户端错误
 * B**** 系统执行出错
 * C**** 调用第三方服务出错
 * D**** 自定义提示异常
 * <p>
 * 可通过 {@code BaseException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "uid不能为空")} 这种方式对错误进行具体描述
 *
 * @author song.shi
 * @since 2016-04-01
 */
public enum ResultCodeEnum implements IResultCode, Serializable {

    /**
     * 成功
     */
    SUCCESS("00000", "成功"),

    /**
     * 一级宏观错误码
     */
    USER_ERROR("A0001", "用户端错误"),
    /**
     * 二级宏观错误码
     */
    USER_REGISTRATION_ERROR("A0100", "用户注册错误"),
    /**
     * 二级宏观错误码
     */
    USER_LOGIN_EXCEPTION("A0200", "用户登录异常"),
    USER_ACCOUNT_NOT_EXIST("A0201", "用户账户不存在"),
    USER_ACCOUNT_FROZEN("A0202", "用户账户被冻结"),
    USER_ACCOUNT_DISABLED("A0203", "用户账户已作废"),
    USER_PASSWORD_ERROR("A0210", "用户名或密码错误"),
    USER_LOGIN_CHECK_ERROR("A0220", "用户身份校验失败"),
    // 一般需要重新登录
    USER_LOGIN_EXPIRED("A0230", "用户登录已过期"),
    USER_LOGIN_REPLACED("A0231", "用户被顶替下线"),
    USER_LOGIN_KICKOUT("A0232", "用户被强制下线"),
    /**
     * 二级宏观错误码
     */
    ACCESS_PERMISSION_EXCEPTION("A0300", "访问权限异常"),
    ACCESS_UNAUTHORIZED("A0301", "访问未授权"),
    /**
     * 二级宏观错误码
     */
    INVALID_PARAMETER_ERROR("A0400", "用户请求参数错误"),
    /**
     * 二级宏观错误码
     */
    UPLOAD_FILE_EXCEPTION("A0700", "上传文件异常"),
    DELETE_FILE_EXCEPTION("A0710", "删除文件异常"),

    /**
     * 一级宏观错误码
     */
    SYSTEM_ERROR("B0001", "系统执行出错"),
    /**
     * 二级宏观错误码
     */
    SYSTEM_EXECUTION_TIMEOUT("B0100", "系统执行超时"),
    /**
     * 二级宏观错误码
     */
    SYSTEM_DISASTER_RECOVERY_FUNCTION_TRIGGERED("B0200", "系统容灾功能被触发"),
    SYSTEM_RATE_LIMITING("B0210", "系统限流"),
    SYSTEM_FUNCTION_DEGRADATION("B0220", "系统功能降级"),
    /**
     * 一级宏观错误码
     */
    THIRD_PARTY_SERVICE_ERROR("C0001", "调用第三方服务出错"),
    /**
     * 二级宏观错误码
     */
    MIDDLEWARE_SERVICE_ERROR("C0100", "中间件服务出错"),
    RPC_SERVICE_ERROR("C0110", "RPC 服务出错"),

    RPC_SERVICE_NOT_FOUND("C0111", "RPC 服务未找到"),
    INTERFACE_NOT_EXIST("C0113", "接口不存在"),

    MESSAGE_SERVICE_ERROR("C0120", "消息服务出错"),
    CACHE_SERVICE_ERROR("C0130", "缓存服务出错"),
    CONFIGURATION_SERVICE_ERROR("C0140", "配置服务出错"),
    NETWORK_RESOURCE_SERVICE_ERROR("C0150", "网络资源服务出错"),
    /**
     * 二级宏观错误码
     */
    THIRD_PARTY_SYSTEM_EXECUTION_TIMEOUT("C0200", "第三方系统执行超时"),
    RPC_EXECUTION_TIMEOUT("C0210", "RPC 执行超时"),
    MESSAGE_DELIVERY_TIMEOUT("C0220", "消息投递超时"),
    CACHE_SERVICE_TIMEOUT("C0230", "缓存服务超时"),
    CONFIGURATION_SERVICE_TIMEOUT("C0240", "配置服务超时"),
    DATABASE_SERVICE_TIMEOUT("C0250", "数据库服务超时"),
    /**
     * 二级宏观错误码
     */
    DATABASE_SERVICE_ERROR("C0300", "数据库服务出错"),
    TABLE_NOT_EXIST("C0311", "表不存在"),
    COLUMN_NOT_EXIST("C0312", "列不存在"),
    DATABASE_DEADLOCK("C0331", "数据库死锁"),
    PRIMARY_KEY_CONFLICT("C0341", "主键冲突"),
    /**
     * 二级宏观错误码
     */
    THIRD_PARTY_DISASTER_RECOVERY_SYSTEM_TRIGGERED("C0400", "第三方容灾系统被触发"),
    THIRD_PARTY_SYSTEM_RATE_LIMITING("C0401", "第三方系统限流"),
    THIRD_PARTY_FUNCTION_DEGRADATION("C0402", "第三方功能降级"),
    /**
     * 二级宏观错误码
     */
    NOTIFICATION_SERVICE_ERROR("C0500", "通知服务出错"),

    /**
     * 自定义提示异常,描述信息可以直接展示给用户看
     */
    CUSTOM_ERROR("D0001", "自定义提示异常");

    private final String code;

    private final String message;

    ResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResultCodeEnum.class.getSimpleName() + "[", "]")
                .add("code=" + code)
                .add("message='" + message + "'")
                .toString();
    }
}
