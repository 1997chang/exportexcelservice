package com.viewshine.exportexcel.entity.vo;

import com.viewshine.exportexcel.entity.enums.ExcelDownloadStatus;
import lombok.Data;

/**
 * 表示返回导出Excel的实体
 * @author ChangWei[changwei@viewshine.cn]
 */
@Data
public class ExportExcelVo {

    /**
     * 表示一个唯一标识
     */
    private String excelId;

    /**
     * 表示下载状态
     */
    private ExcelDownloadStatus status;

    /**
     * 表示下载Excel的文件路径地址
     */
    private String url;
}
