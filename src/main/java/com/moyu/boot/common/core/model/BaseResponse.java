package com.moyu.boot.common.core.model;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * 基本响应对象, 包含错误代码和错误描述信息, 返回的具体数据可包含在 data 字段中, data采用范型, 可根据需要定义类型
 * <p>使用举例:</p>
 * <pre>
 * 仅创建一个空响应对象:
 *     BaseResponse<T> response = new BaseResponse<T>();
 * 通过数据创建对象, 此时响应码和响应信息都是空的:
 *     BaseResponse<T> response = new BaseResponse<T>(data);
 * 自定义响应码和响应描述信息:
 *     BaseResponse<T> response = new BaseResponse<T>(code, message);
 * </pre>
 *
 * @author song.shi
 * @since 2018-07-31
 */
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int SUCCESS_CODE = 0;

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
    public static <T> BaseResponse<T> getSuccessResponse() {
        return new BaseResponse<T>(SUCCESS_CODE, "成功");
    }

    /**
     * 返回成功的响应
     */
    public static <T> BaseResponse<T> getSuccessResponse(T data) {
        BaseResponse<T> response = getSuccessResponse();
        response.setData(data);
        return response;
    }

    public BaseResponse() {
    }


    public BaseResponse(T data) {
        this.data = data;
    }

    public BaseResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 判断请求是否成功的方法
     */
    public boolean isSuccess() {
        return SUCCESS_CODE == code;
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
        return new StringJoiner(", ", BaseResponse.class.getSimpleName() + "[", "]")
                .add("code=" + code)
                .add("message='" + message + "'")
                .add("data=" + data)
                .toString();
    }
}
