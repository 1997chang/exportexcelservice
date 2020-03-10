package com.viewshine.exportexcel.exceptions;

import com.viewshine.exportexcel.entity.vo.ResultVO;
import com.viewshine.exportexcel.exceptions.enums.BusinessErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 这个表示异常处理类，只有@RestController或者Controller中抛出的异常才会进入这个方法中。其他的都不会进入这个异常处理。
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
        return ResultVO.errorResult(fieldError.getDefaultMessage());
    }

    /**
     * 用于处理在导出Excel中出现的异常问题
     */
    @ExceptionHandler(BusinessException.class)
    public ResultVO<Void> businessExceptionHandle(BusinessException businessException) {
        BusinessErrorCode errorCode = businessException.getErrorCode();
        logger.error("执行业务逻辑出现错误：错误编码是：[{}]，错误消息是：[{}]",
                errorCode.getCode(), errorCode.getMessage());
        return ResultVO.errorResult(errorCode.getCode(), errorCode.getMessage());
    }

}
