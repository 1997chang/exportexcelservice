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
    FORMULA_ERROR(10_0002, "计算表达式错误"),

    SELECT_DATA_MYSQL(40_0001, "查询MYSQL数据异常"),
    SELECT_DATA_MONGO(40_0002, "查询MONGO数据异常"),
    NO_COLLECTION_MONGO(40_0003, "没有提供MONGO数据的表"),
    DATASOURCE_NAME_ERROR(40_0004, "数据库名称空或者Mongo路由不包含这个数据库名称"),
    DATABASE_TYPE_ERROR(40_0005, "查询的数据库类型不正确，没有为数据库类型提供查询数据方法"),

    DELETE_FILE_ERROR(50_0001,"删除文件失败");


    private final int code;

    private final String message;
}
