package com.viewshine.exportexcel.entity;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
     * 表示SQL语句中的参数
     */
    private Object[] sqlParams;

    /**
     * 表示选择的数据源名称
     */
    @NotBlank(message = "没有指定选择的数据库名称")
    private String datasource;

    /**
     * 表示各个Excel导出列的属性，一个对应一个ExcelColumnDTO。
     */
    @NotNull(message = "Excel表格属性不能为空")
    @NotEmpty(message = "Excel表格属性不能为空")
    private List<ExcelColumnDTO> excelColumnDTOList;

    /**
     * 表示保存的天数
     */
    @Min(message = "保存的天数不能少于1天", value = 1)
    private Integer saveDay;

    /**
     * 表示导出到那个目录下
     */
    @NotBlank(message = "导出的目录不能为空")
    private String exportDirectory;

    /**
     * 表示文件的前缀
     */
    private String filePrefix;

    /**
     * 表示回调地址端口
     */
    private Integer thriftPort;

}
