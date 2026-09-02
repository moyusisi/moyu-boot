package com.moyu.boot.common.web.advice;


import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.sign.exception.SaSignException;
import cn.hutool.json.JSONUtil;
import com.moyu.boot.common.authZ.util.ExceptionWrapperUtils;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>@ControllerAdvice和@RestControllerAdvice都可以指向控制器的一个子集</p>
 * <pre>
 *   如:
 *   指向所有带 @RestController 注解的控制器: @ControllerAdvice(annotations = RestController.class)
 *   指向所有指定包中的控制器: @ControllerAdvice("org.example.controllers")
 * </pre>
 * <p>@ControllerAdvice和@RestControllerAdvice的区别在于:</p>
 * <pre>
 *   ControllerAdvice     中@ExceptionHandler注解的函数返回值不会自动加@ResponseBody语义
 *   RestControllerAdvice 中@ExceptionHandler注解的函数返回值会自动加@ResponseBody语义(就像RestController一样)
 * </pre>
 *
 * @author shisong
 * @since 2022-09-08
 */
@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    /**
     * 参数缺失异常(MissingServletRequestParameterException extends ServletRequestBindingException)
     * <p>
     * 请求参数绑定到JavaBean或模型属性时出现的异常，如必传参数缺失(parameter、header、cookie、path等)
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public Result<?> exceptionHandler(ServletRequestBindingException e) {
        log.error("参数缺失异常:{}", e.getMessage(), e);
        Result<?> result = new Result<>(ResultCodeEnum.INVALID_PARAMETER_ERROR, e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 参数绑定异常(MethodArgumentNotValidException extends BindException)
     * <p>
     * 使用 @RequestBody @Valid 或者 @Validated 进行参数验证时绑定失败会触发
     */
    @ExceptionHandler(BindException.class)
    public Result<?> exceptionHandler(BindException e) {
        log.error("绑定异常:{}", e.getMessage());
        // 单个字段错误会返回MethodArgumentNotValidException，多个字段错误会把所有错误信息拼在一起返回BindException
        String message = e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        Result<?> result = new Result<>(ResultCodeEnum.INVALID_PARAMETER_ERROR, message);
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 违反约束条件异常(ConstraintViolationException extends ValidationException)
     * <p>
     * 使用 @Valid 或者 @Validated 进行参数验证时，违反如 @Size、@Min、@Max等约束条件会触发
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> exceptionHandler(ConstraintViolationException e) {
        log.error("违反约束条件异常:{}", e.getMessage());
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        Result<?> result = new Result<>(ResultCodeEnum.INVALID_PARAMETER_ERROR, message);
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 参数类型不匹配的异常 MethodArgumentTypeMismatchException
     * <p>
     * 当请求参数类型不匹配时的异常，如Controller中@RequestParam指定的字段与传参不一致
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> exceptionHandler(MethodArgumentTypeMismatchException e) {
        log.error("参数类型错误异常:{}", e.getMessage(), e);
        Result<?> result = new Result<>(ResultCodeEnum.INVALID_PARAMETER_ERROR, e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 参数转换异常 HttpMessageConversionException
     * <p>
     * 如json格式参数进行参数类型转换时，参数转换失败则抛出异常。
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public Result<?> exceptionHandler(HttpMessageConversionException e) {
        log.error("参数转换异常:{}", e.getMessage(), e);
        Result<?> result = new Result<>(ResultCodeEnum.INVALID_PARAMETER_ERROR, e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    // ===================== 4xx 客户端异常 =====================

    /**
     * 404 接口不存在
     * 访问不存在的接口（404），默认不会抛出 NoHandlerFoundException，直接转发 /error，走 BasicErrorController
     * 需两个配置，才能捕获 404 异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handle404(NoHandlerFoundException e) {
        log.warn("访问接口不存在异常:{}", e.getMessage(), e);
        Result<?> result = new Result<>(ResultCodeEnum.INTERFACE_NOT_EXIST);
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 405 请求方法不支持 GET/POST 不匹配
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handle405(HttpRequestMethodNotSupportedException e) {
        log.warn("405 请求方法错误:{}", e.getMethod(), e);
        Result<?> result = new Result<>(ResultCodeEnum.USER_ERROR, e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 406 无法返回客户端接受的媒体类型
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Result<?> handle406(HttpMediaTypeNotAcceptableException e) {
        log.warn("406 无法返回客户端 Accept 指定格式:{}", e.getMessage(), e);
        Result<?> result = new Result<>(ResultCodeEnum.USER_ERROR, e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 415 媒体类型不支持（你之前表单传@RequestBody报错）
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result<?> handle415(HttpMediaTypeNotSupportedException e) {
        log.warn("415 不支持的请求类型:{}", e.getContentType(), e);
        Result<?> result = new Result<>(ResultCodeEnum.USER_ERROR, e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * Servlet异常(此异常范围较大) 兜底捕获400/416等未单独捕获的MVC异常
     */
    @ExceptionHandler(ServletException.class)
    public Result<?> exceptionHandler(ServletException e) {
        log.error("Servlet异常:{}", e.getMessage(), e);
        Result<?> result = new Result<>(ResultCodeEnum.SYSTEM_ERROR, e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }
    // ===================== 容器IO异常 =====================

    /**
     * 客户端主动断开连接，避免大量ERROR日志刷屏
     */
    @ExceptionHandler(ClientAbortException.class)
    public Result<?> handleClientAbort() {
        log.debug("客户端主动断开TCP连接，无需返回响应");
        return null;
    }

    /**
     * 非法参数异常
     * <p>
     * 一些断言工具中会抛出的异常,如 Assert.notEmpty
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> exceptionHandler(IllegalArgumentException e) {
        log.error("参数绑定异常:{}", e.getMessage(), e);
        Result<?> result = new Result<>(ResultCodeEnum.INVALID_PARAMETER_ERROR, e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 业务异常
     * <p>
     * 业务逻辑发生异常时主动抛出
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> exceptionHandler(BusinessException e) {
        log.error(e.getMessage());
        Result<?> result = new Result<>(e.getCode(), e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 认证鉴权异常
     * sa鉴权的相关异常(SaTokenException的子类)(注意要使用AOP模式，不要使用拦截器模式,否则无法打印入参)
     */
    @ExceptionHandler({NotLoginException.class, NotRoleException.class, NotPermissionException.class})
    public Result<?> noPermissionException(HttpServletRequest request, SaTokenException e) {
        Result<?> result = new Result<>(ResultCodeEnum.ACCESS_UNAUTHORIZED);
        if (e instanceof NotLoginException) {
            // 处理未登录异常，区分未认证的具体场景
            result = ExceptionWrapperUtils.handleNotLogin((NotLoginException) e);
        }
        log.info("未授权访问：{}", request.getRequestURI());
        return result;
    }

    /**
     * 验签异常
     */
    @ExceptionHandler({SaSignException.class})
    public Result<?> signException(HttpServletRequest request, SaSignException e) {
        Result<?> result = new Result<>(ResultCodeEnum.INVALID_PARAMETER_ERROR, e.getMessage());
        log.info("验签异常：{}", request.getRequestURI());
        return result;
    }

    /**
     * 其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> exceptionHandler(Exception e) {
        // Filter层的异常不会经过ExceptionHandler处理
        log.error("系统异常", e);
        Result<?> result = Result.failed(e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }
}
