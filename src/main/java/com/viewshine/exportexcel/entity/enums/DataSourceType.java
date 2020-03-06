package com.viewshine.exportexcel.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据库的类型的枚举，传递的type参数值必须是这些中的一个
 * @author changWei[changwei@viewshine.cn]
 */
@AllArgsConstructor
@Getter
public enum DataSourceType {

    MONGODB("MONGODB数据库"),
    MYSQL("MYSQL数据库");

    private final String cName;

}

