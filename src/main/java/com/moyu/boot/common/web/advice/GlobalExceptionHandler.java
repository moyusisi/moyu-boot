package com.moyu.boot.common.web.advice;


import cn.hutool.json.JSONUtil;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
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
     * 异常捕捉处理
     * 若为ControllerAdvice     处理异常后返回本函数的返回值
     * 若为RestControllerAdvice 处理异常后返回本函数的返回值的restful结果(就像加了@ResponseBody一样)
     */
    @ExceptionHandler(value = Exception.class)
    public Result<?> exceptionHandler(Exception e) {
        Result<?> response = new Result<>();
        if (e instanceof BindException) {
            // Spring验证异常(MethodArgumentNotValidException extends BindException)
            // 单个字段错误会返回MethodArgumentNotValidException，多个字段错误会把所有错误信息拼在一起返回BindException
            BindingResult bindingResult = ((BindException) e).getBindingResult();
            String message = "参数错误:" + bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(";"));
            log.error(message);
            response.setCode(ResultCodeEnum.INVALID_PARAMETER.getCode());
            response.setMessage(message);
        } else if (e instanceof ConstraintViolationException) {
            // Spring校验违反约束异常(ConstraintViolationException extends ValidationException)，如 @Size、@Min、@Max
            String message = "参数错误:" + e.getMessage();
            log.error(message);
            response.setCode(ResultCodeEnum.INVALID_PARAMETER.getCode());
            response.setMessage(e.getMessage());
        } else if (e instanceof IllegalArgumentException) {
            // 业务校验中的参数异常 IllegalArgumentException，如 hutool中的Assert.isTrue
            String message = "参数错误:" + e.getMessage();
            log.error(message);
            response.setCode(ResultCodeEnum.INVALID_PARAMETER.getCode());
            response.setMessage(message);
        } else if (e instanceof ServletRequestBindingException) {
            // ServletRequestBindingException是请求参数绑定到JavaBean或模型属性时出现的异常，如必传参数缺失
            log.error(e.getMessage(), e);
            response.setCode(ResultCodeEnum.INVALID_PARAMETER.getCode());
            response.setMessage(ResultCodeEnum.INVALID_PARAMETER.getMessage());
        } else if (e instanceof HttpMessageConversionException) {
            // json格式参数进行参数类型转换时，参数转换失败则HttpMessageConversionException
            log.error(e.getMessage(), e);
            response.setCode(ResultCodeEnum.INVALID_PARAMETER.getCode());
            response.setMessage(ResultCodeEnum.INVALID_PARAMETER.getMessage() + ":参数转换异常");
        } else if (e instanceof BusinessException) {
            log.error(e.getMessage());
            response.setCode(((BusinessException) e).getCode());
            response.setMessage(e.getMessage());
        } else {
            log.error("系统异常", e);
            response.setCode(ResultCodeEnum.SYSTEM_ERROR.getCode());
            response.setMessage(ResultCodeEnum.SYSTEM_ERROR.getMessage());
        }
        log.info("异常捕捉处理后返回结果为:{}", JSONUtil.toJsonStr(response));
        return response;
    }
}
