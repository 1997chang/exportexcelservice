package com.viewshine.exportexcel.service;

import com.viewshine.exportexcel.entity.RequestExcelDTO;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public interface ExportExcelService {
    /**
     * 在指定的dataBase数据源下执行执行的SQL语句，并保存到本地磁盘文件fileName中
     * @param requestExcelDTO 表示执行的SQL语句，选择的数据源，以及各个导出列的属性内容
     * @param fileName 表示导出的文件名称
     */
    void exportExcelToDisk(RequestExcelDTO requestExcelDTO, String fileName);
}
