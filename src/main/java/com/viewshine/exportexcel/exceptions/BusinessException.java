package com.viewshine.exportexcel.exceptions;

import com.viewshine.exportexcel.exceptions.enums.BusinessErrorCode;

/**
 * 业务逻辑错误的异常，只能接受一个错误编码参数，从而在统一异常处理中，直接将错误编码以及错误消息传递给前端
 * @author changWei[changwei@viewshine.cn]
 */
public class BusinessException extends RuntimeException {

    private BusinessErrorCode errorCode;

    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessErrorCode getErrorCode() {
        return errorCode;
    }
}
