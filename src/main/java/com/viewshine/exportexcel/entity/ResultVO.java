package com.viewshine.exportexcel.entity;

import lombok.Data;

/**
 * @author ChangWei[changwei@viewshine.cn]
 */
@Data
public class ResultVO<T> {

    private Integer code;

    private String message;

    private T data;

    private ResultVO() {}

    private ResultVO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResultVO errorResult(String message) {
        ResultVO resultVO = new ResultVO();
        resultVO.code = 500;
        resultVO.message = message;
        return resultVO;
    }

    public static ResultVO<Void> successResult() {
        return new ResultVO<Void>(200, "success");
    }

    public static <T> ResultVO<T> successResult(T t) {
        ResultVO<T> success = new ResultVO<>(200, "success");
        success.setData(t);
        return success;
    }
}
