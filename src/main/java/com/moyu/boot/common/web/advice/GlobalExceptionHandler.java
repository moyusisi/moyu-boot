package com.moyu.boot.common.web.advice;


import cn.hutool.json.JSONUtil;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
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
     * 绑定异常(MethodArgumentNotValidException extends BindException)
     * <p>
     * 使用 @Valid 或者 @Validated 进行参数验证时绑定失败会触发
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
     * 参数绑定异常(MissingServletRequestParameterException extends ServletRequestBindingException)
     * <p>
     * 请求参数绑定到JavaBean或模型属性时出现的异常，如必传参数缺失(parameter、header、cookie、path等)
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public Result<?> exceptionHandler(ServletRequestBindingException e) {
        log.error("参数绑定异常:{}", e.getMessage(), e);
        Result<?> result = new Result<>(ResultCodeEnum.INVALID_PARAMETER_ERROR, e.getMessage());
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

    /**
     * 接口不存在
     * <p>
     * 当客户端请求一个不存在的路径时，会抛出 NoHandlerFoundException 异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> exceptionHandler(NoHandlerFoundException e) {
        log.error("访问接口不存在异常:{}", e.getMessage(), e);
        Result<?> result = new Result<>(ResultCodeEnum.INTERFACE_NOT_EXIST);
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * Servlet异常(此异常范围较大)
     * <p>
     * 当 Servlet 请求处理时发生的异常。
     */
    @ExceptionHandler(ServletException.class)
    public Result<?> exceptionHandler(ServletException e) {
        log.error("Servlet异常:{}", e.getMessage(), e);
        Result<?> result = new Result<>(ResultCodeEnum.SYSTEM_ERROR, e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
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
     * 其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> exceptionHandler(Exception e) throws Exception {
        // 将认证鉴权异常异常继续抛出，不在此处理
        if (e instanceof AccessDeniedException || e instanceof AuthenticationException) {
            throw e;
        }
        log.error("系统异常", e);
        Result<?> result = Result.failed(e.getMessage());
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(result));
        return result;
    }
}
