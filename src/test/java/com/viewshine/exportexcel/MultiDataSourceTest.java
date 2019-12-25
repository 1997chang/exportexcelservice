package com.viewshine.exportexcel;

import com.viewshine.exportexcel.entity.ExcelColumnDTO;
import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.service.ExportExcelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
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

    @Autowired
    private MockHttpServletRequest mockHttpServletRequest;

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
        List<ExcelColumnDTO> excelColumnDTOList = new ArrayList<>(Arrays.asList(excelColumnDTO, excelColumnDTO1,
                excelColumnDTO2, excelColumnDTO3));
        requestExcelDTO.setExcelColumnDTOList(excelColumnDTOList);
    }

    @Test
    public void writeCountryLanguageExcel() {
        RequestExcelDTO requestExcelDTO = new RequestExcelDTO();
        requestExcelDTO.setSql("select * from countrylanguage");
        requestExcelDTO.setDatasource("one");
        requestExcelDTO.setSaveDay(4);
        requestExcelDTO.setFilePrefix("test");
        requestExcelDTO.setExportDirectory("temptest");
        createExcelColumn(requestExcelDTO);
        exportExcelService.exportExcelToDisk(requestExcelDTO, mockHttpServletRequest);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createUserExcelColumn(RequestExcelDTO requestExcelDTO) {
        ExcelColumnDTO excelColumnDTO = new ExcelColumnDTO();
        excelColumnDTO.setColumnName("id");
        excelColumnDTO.setExcelHeadName(Arrays.asList("威星", "编号"));

        ExcelColumnDTO excelColumnDTO1 = new ExcelColumnDTO();
        excelColumnDTO1.setColumnName("username");
        excelColumnDTO1.setExcelHeadName(Arrays.asList("威星","姓名"));

        ExcelColumnDTO excelColumnDTO2 = new ExcelColumnDTO();
        excelColumnDTO2.setColumnName("password");
        excelColumnDTO2.setExcelHeadName(Arrays.asList("威星","密码"));

        ExcelColumnDTO excelColumnDTO3 = new ExcelColumnDTO();
        excelColumnDTO3.setColumnName("password_salt");
        excelColumnDTO3.setExcelHeadName(Arrays.asList("威星","数学"));


        ExcelColumnDTO excelColumnDTO4 = new ExcelColumnDTO();
        excelColumnDTO4.setColumnName("price");
        excelColumnDTO4.setExcelHeadName(Arrays.asList("威星","语文"));

        ExcelColumnDTO excelColumnDTO5 = new ExcelColumnDTO();
        excelColumnDTO5.setColumnName("sum");
        excelColumnDTO5.setFormula("password_salt + price");
        excelColumnDTO5.setExcelHeadName(Arrays.asList("威星","最终得分"));

        ExcelColumnDTO excelColumnDTO6 = new ExcelColumnDTO();
        excelColumnDTO6.setColumnName("create_time");
        excelColumnDTO6.setFormat("yyyy-MM-dd HH:mm:ss");
        excelColumnDTO6.setExcelHeadName(Arrays.asList("威星","创建时间"));

        List<ExcelColumnDTO> excelColumnDTOList = new ArrayList<>(Arrays.asList(excelColumnDTO, excelColumnDTO1,
                excelColumnDTO2, excelColumnDTO3, excelColumnDTO4, excelColumnDTO5, excelColumnDTO6));
        requestExcelDTO.setExcelColumnDTOList(excelColumnDTOList);
    }

    @Test
    public void writeUser() {
        RequestExcelDTO requestExcelDTO = new RequestExcelDTO();
        requestExcelDTO.setSql("select * from users");
        requestExcelDTO.setDatasource("one");
        requestExcelDTO.setSaveDay(4);
        requestExcelDTO.setFilePrefix("user");
        requestExcelDTO.setExportDirectory("users");
        createUserExcelColumn(requestExcelDTO);
        exportExcelService.exportExcelToDisk(requestExcelDTO, mockHttpServletRequest);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
