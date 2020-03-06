package com.viewshine.exportexcel.exceptions;

/**
 * 这个表示数据库的错误信息
 * @author ChangWei[changwei@viewshine.cn]
 */
public class MultiDataSourceException extends RuntimeException {

    public MultiDataSourceException(String message) {
        super(message);
    }

    public MultiDataSourceException(Throwable throwable) {
        super(throwable);
    }

    public MultiDataSourceException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
