package com.viewshine.exportexcel.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表示导出Excel表格的列的相关属性
 * @author ChangWei[changwei@viewshine.cn]
 */
@Data
public class ExcelColumnDTO {

    /**
     * 表示SQL语句中对应的列名，或者Mongo数据库中的列名
     */
    private String columnName;

    /**
     * 表示Java中对应类型的全限定名称
     */
    private String classType;

    /**
     * 表示输出到Excel表格中的头数据
     */
    private List<String> excelHeadName;

    /**
     * 值的映射关系
     */
    private Map<String, String> mapping;

    /**
     * 表示这一列的值使用公式计算出来
     */
    private String formula;

    /**
     * 表示格式化的形式
     */
    private String format;

    /**
     * 表示列的宽度。
     * 注意：如果autoAdjustColumnWidth为True时，这个值无效
     */
    private Integer columnWidth;

    /**
     * 表示某一列是否自动调整列的宽度。
     */
    private Boolean autoAdjustColumnWidth;


}
