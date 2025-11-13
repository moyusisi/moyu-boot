package com.moyu.boot.common.core.exception;


import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.model.IResultCode;

import java.util.StringJoiner;

/**
 * <p>使用举例:</p>
 * <p>1. 构造方法: {@link #BusinessException(String, String)}, {@link #BusinessException(IResultCode)}和{@link #BaseException(ResultCodeEnum, String)}
 * <pre>
 * throw new BaseException(code, message); // 自定义响应码和响应描述信息
 * throw new BaseException(ResultCodeEnum.SYSTEM_ERROR); // 抛出某类型的异常
 * throw new BaseException(ResultCodeEnum.BUSINESS_ERROR, "rpc调用异常");  // 抛出某类型的异常, 并加上具体描述, 最终描述信息将变成: "业务异常:rpc调用异常"
 * throw new BaseException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "userId不能为空"); // 抛出某类型的异常, 并加上具体描述, 最终描述信息将变成: "参数错误:userId不能为空"
 * </pre></p>
 *
 * <p>2. 异常的使用场景
 * <pre>
 * try { ... } catch (BaseException e) {
 *     Response response = Response(e.getCode(), e.getReturnMessage());
 * }
 *
 * try {
 *     Preconditions.checkNotNull(request, "请求参数不能为空");    // 抛出 NullPointerException
 *     Preconditions.checkArgument(!Strings.isNullOrEmpty(mobile), "mobile不能为空");   // 抛出 IllegalArgumentException
 * } catch (Exception e) {
 *     throw new BaseException(ResultCodeEnum.INVALID_PARAMETER_ERROR, e.getMessage());
 * }
 * </pre></p>
 *
 * @author song.shi
 * @since 2016-04-05
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 4067917628321917086L;

    /**
     * 错误码, 参考ResultCodeEnum
     */
    private String code;
    private String message;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 根据枚举类创建异常, 以枚举类的描述信息作为异常的message
     */
    public BusinessException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 根据枚举类创建异常, 并补充具体的错误描述信息, 以枚举描述+detail作为异常的message
     */
    public BusinessException(IResultCode resultCode, String detail) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        String message = resultCode.getMessage();
        if (detail != null && !detail.isEmpty()) {
            message = message + ":" + detail;
        }
        this.message = message;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 业务异常不需要跟踪堆栈信息，减少VM在抛异常时java进程的子线程的停顿
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("code=" + code)
                .add("message='" + message + "'")
                .toString();
    }
}
