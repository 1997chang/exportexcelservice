package com.viewshine.exportexcel.entity.enums;

/**
 * 表示Excel文件的下载状态
 * @author changWei[changwei@viewshine.cn]
 */
public enum ExcelDownloadStatus {

    DOWNLOADING("下载中"),
    FINISHED("下载完成"),
    DELETE("删除"),
    CANCEL("下载取消");

    private final String status;

    ExcelDownloadStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
