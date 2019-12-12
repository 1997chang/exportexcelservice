package com.viewshine.exportexcel.service;

import java.util.List;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public interface ExportExcelService {
    /**
     * 在指定的dataBase数据源下执行执行的SQL语句，输出的列名为column
     * @param sql 执行的SQL语句
     * @param dataBase 指定那个数据源
     * @param columnNameLists 导出的列名
     */
    void exportExcel(String sql, String dataBase, List<List<String>> columnNameLists);
}
