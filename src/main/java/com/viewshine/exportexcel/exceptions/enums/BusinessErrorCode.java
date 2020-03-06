package com.viewshine.exportexcel.exceptions.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务系统的错误编码，以及返回的错误信息
 * @author changWei[changwei@viewshine.cn]
 */
@AllArgsConstructor
@Getter
public enum BusinessErrorCode {

    PARAM_ILLEGAL(10_0001, "参数值无效"),

    DELETE_FILE_ERROR(50_0001,"删除文件失败");

    private final int code;

    private final String message;
}
