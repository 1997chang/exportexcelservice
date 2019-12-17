package com.viewshine.exportexcel.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * HTTP请求导出Excel的参数
 * @author ChangWei[changwei@viewshine.cn]
 */
@Data
public class RequestExcelDTO {

    /**
     * 表示执行的SQL语句
     */
    @NotBlank(message = "执行的SQL语句不能为NULL")
    private String sql;

    /**
     * 表示选择的数据源名称
     */
    private String datasource;

    /**
     * 表示各个Excel导出列的属性，一个对应一个ExcelColumnDTO。
     */
    @NotEmpty(message = "Excel表格属性不能为空")
    private List<ExcelColumnDTO> excelColumnDTOList;

}
