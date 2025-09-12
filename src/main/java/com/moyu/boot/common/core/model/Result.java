package com.moyu.boot.common.core.model;

import com.moyu.boot.common.core.enums.ResultCodeEnum;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * 基本响应对象, 包含错误代码和错误描述信息, 返回的具体数据可包含在 data 字段中, data采用范型, 可根据需要定义类型
 * <p>使用举例:</p>
 * <pre>
 * 仅创建一个空响应对象:
 *     Result<T> response = new Result<T>();
 * 创建一个成功的响应对象:
 *     Result<T> response = Result.success();
 * 自定义响应码和响应描述信息:
 *     Result<T> response = new Result<T>(code, message);
 * 自定义响应码和响应描述信息:
 *     Result<T> response = new Result<T>(code, message);
 * 通过数据创建对象:
 *     Result<T> response = new Result<T>(code, message, data);
 * </pre>
 *
 * @author song.shi
 * @since 2018-07-31
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 创建响应码为 SUCCESS_CODE 的对象
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCodeEnum.SUCCESS);
    }

    /**
     * 返回成功的响应
     */
    public static <T> Result<T> success(T data) {
        Result<T> response = success();
        response.setData(data);
        return response;
    }

    public Result() {
    }


    public Result(T data) {
        this.data = data;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(IResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Result.class.getSimpleName() + "[", "]")
                .add("code=" + code)
                .add("message='" + message + "'")
                .add("data=" + data)
                .toString();
    }
}
