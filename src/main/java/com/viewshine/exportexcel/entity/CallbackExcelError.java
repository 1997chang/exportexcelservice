package com.viewshine.exportexcel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 通知客户端最终也失败
 * @author changWei[changwei@viewshine.cn]
 */
@Data
@AllArgsConstructor
public class CallbackExcelError {

    /**
     * 表示客户端的主机号
     */
    private String host;

    /**
     * 表示客户端的端口
     */
    private int port;

    /**
     * 表示那个Excel文件通知失败
     */
    private String excelId;

    /**
     * 表示Excel的URL地址
     */
    private String url;

}
