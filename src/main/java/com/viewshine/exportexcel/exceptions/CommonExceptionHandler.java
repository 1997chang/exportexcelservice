package com.viewshine.exportexcel.exceptions;

import com.viewshine.exportexcel.entity.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 这个表示异常处理类
 * @author ChangWei[changwei@viewshine.cn]
 */
@RestControllerAdvice
public class CommonExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommonExceptionHandler.class);

    /**
     * JSON格式的数据在不满足验证条件的时候，抛出：{@link MethodArgumentNotValidException MethodArgumentNotValidException}异常
     * 处理这个异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<Void> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        MethodParameter parameter = exception.getParameter();
        logger.error("在执行{}.{}的时候出现绑定JSON数据参数异常，字段名称为：{}，提示消息：{}",
                parameter.getDeclaringClass().getName(), parameter.getMethod().getName(), fieldError.getField(),
                fieldError.getDefaultMessage());
        return ResultVO.errorResult(Objects.requireNonNull(fieldError).getDefaultMessage());
    }

    /**
     * 表示处理所有剩余异常，没有被捕获的异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResultVO<Void> exceptionHandle(Exception e) {
        logger.error("系统错误：" + e.getMessage(), e);
        return ResultVO.errorResult("系统繁忙，请稍后重试！！");
    }

}