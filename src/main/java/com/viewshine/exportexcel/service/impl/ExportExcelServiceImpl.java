package com.viewshine.exportexcel.service.impl;

import cn.viewshine.cloudthree.excel.ExcelFactory;
import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.entity.ExcelColumnDTO;
import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.properties.DataSourceNameHolder;
import com.viewshine.exportexcel.service.ExportExcelService;
import com.viewshine.exportexcel.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Service
public class ExportExcelServiceImpl implements ExportExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExportExcelServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskExecutor exportExcelTaskExecutor;

    @Override
    public void exportExcelToDisk(RequestExcelDTO requestExcelDTO) {
        exportExcelTaskExecutor.execute(() -> {
            try {

                logger.info("准备导出Excel");
                List<List<String>> excelContentData = getExcelContentData(requestExcelDTO);
                logger.info("查询出来的数据内容为：[{}]", JSON.toJSONString(excelContentData));
                List<List<String>> excelHeadName = getExcelHeadName(requestExcelDTO.getExcelColumnDTOList());
                logger.info("Excel表格的头数据内容：[{}]", JSON.toJSONString(excelContentData));
                String fileName = CommonUtils.generateExcelFileName(requestExcelDTO.getExportDirectory(),
                        requestExcelDTO.getFilePrefix());
                logger.info("最终的文件路径地址；[{}]", fileName);
                ExcelFactory.writeExcel(Collections.singletonMap("sheet1", excelContentData),
                        Collections.singletonMap("sheet1", excelHeadName), fileName);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     * 用于
     * @return
     */
    private List<List<String>> getExcelContentData(RequestExcelDTO requestExcelDTO) {
        try{
            logger.info("准备获取数据内容，执行的SQL语句为：[{}]", requestExcelDTO.getSql());
            DataSourceNameHolder.setActiveDataSource(requestExcelDTO.getDatasource());
            List<List<String>> result = jdbcTemplate.query(requestExcelDTO.getSql(), (rs, rowCount) -> {
                List<String> itemData = new ArrayList<>();
                requestExcelDTO.getExcelColumnDTOList().forEach(columnDTO -> {
                    //计算公式
                    try {
                        if (StringUtils.isNotBlank(columnDTO.getFormula())) {

                        } else {
                            itemData.add(rs.getString(columnDTO.getColumnName()));
                        }
                    } catch (Exception e) {
                        logger.error("获取SQL数据内容错误");
                    }
                });
                return itemData;
            }, requestExcelDTO.getSqlParams());
            logger.info("查询出来的数据内容为：{}", JSON.toJSONString(result));
            return result;
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private static List<List<String>> getExcelHeadName(List<ExcelColumnDTO> excelColumnDTOList) {
        if (excelColumnDTOList == null) {
            return (List<List<String>>) Collections.EMPTY_LIST;
        }
        return excelColumnDTOList.stream().map(ExcelColumnDTO::getExcelHeadName).collect(Collectors.toList());
    }

}
