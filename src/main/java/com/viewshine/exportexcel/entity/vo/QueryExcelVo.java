package com.viewshine.exportexcel.entity.vo;

import lombok.Data;

/**
 * 用于查询Excel是否下载成功。
 * @author ChangWei[changwei@viewshine.cn]
 */
@Data
public class QueryExcelVo {

    /**
     * 表示是否下载完成
     */
    private Boolean finish;
}
