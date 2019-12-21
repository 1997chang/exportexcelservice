package com.viewshine.exportexcel;

import com.viewshine.exportexcel.entity.ExcelColumnDTO;
import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.service.ExportExcelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author ChangWei[changwei@viewshine.cn]
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MultiDataSourceTest {

    @Autowired
    private ExportExcelService exportExcelService;

    public void createExcelColumn(RequestExcelDTO requestExcelDTO) {
        ExcelColumnDTO excelColumnDTO = new ExcelColumnDTO();
        excelColumnDTO.setColumnName("CountryCode");
        excelColumnDTO.setExcelHeadName(Collections.singletonList("编码"));

        ExcelColumnDTO excelColumnDTO1 = new ExcelColumnDTO();
        excelColumnDTO1.setColumnName("Language");
        excelColumnDTO1.setExcelHeadName(Collections.singletonList("语言"));

        ExcelColumnDTO excelColumnDTO2 = new ExcelColumnDTO();
        excelColumnDTO2.setColumnName("IsOfficial");
        excelColumnDTO2.setExcelHeadName(Collections.singletonList("是否官方"));

        ExcelColumnDTO excelColumnDTO3 = new ExcelColumnDTO();
        excelColumnDTO3.setColumnName("Percentage");
        excelColumnDTO3.setExcelHeadName(Collections.singletonList("频率"));
        List<ExcelColumnDTO> excelColumnDTOList = new ArrayList<>(Arrays.asList(excelColumnDTO, excelColumnDTO1, excelColumnDTO2, excelColumnDTO3));
        requestExcelDTO.setExcelColumnDTOList(excelColumnDTOList);
    }

    @Test
    public void writeExcel() {
        RequestExcelDTO requestExcelDTO = new RequestExcelDTO();
        requestExcelDTO.setSql("select * from countrylanguage");
        requestExcelDTO.setDatasource("one");
        requestExcelDTO.setSaveDay(4);
        requestExcelDTO.setFilePrefix("test");
        requestExcelDTO.setExportDirectory("temptest");
        createExcelColumn(requestExcelDTO);
        exportExcelService.exportExcelToDisk(requestExcelDTO);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
