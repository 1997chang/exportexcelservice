package com.viewshine.exportexcel.entity.vo;

import com.viewshine.exportexcel.entity.enums.ExcelDownloadStatus;
import lombok.Data;

/**
 * 表示返回导出Excel的实体
 * @author ChangWei[changwei@viewshine.cn]
 */
@Data
public class ExportExcelVo implements Cloneable {

    /**
     * 表示一个唯一标识，用于查询一个文件的下载状态或者文件基本信息
     */
    private String excelId;

    /**
     * 表示下载状态
     */
    private ExcelDownloadStatus status;

    /**
     * 表示下载Excel的文件路径地址，使用这个请求进行下载文件
     */
    private String url;

    /**
     * 表示Excel资源定位
     */
    private String uri;

    @Override
    public ExportExcelVo clone() throws CloneNotSupportedException {
        return (ExportExcelVo) super.clone();
    }
}
