package com.viewshine.exportexcel;

import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.entity.ExcelColumnDTO;
import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.service.ExportExcelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ChangWei[changwei@viewshine.cn]
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MultiDataSourceTest {

    @Autowired
    private ExportExcelService exportExcelService;

    @Autowired
    private MockHttpServletRequest mockHttpServletRequest;

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    public void multiDataSourceTest() {
        RequestExcelDTO requestExcelDTO = new RequestExcelDTO();
        requestExcelDTO.setSql("select * from user");
        requestExcelDTO.setDatasource("one");
        requestExcelDTO.setSaveDay(4);
        requestExcelDTO.setFilePrefix("user");
        requestExcelDTO.setPageSize(200);
        requestExcelDTO.setExportDirectory("users");
        createUserExcelColumn(requestExcelDTO);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/export/excelToDisk")
                .param("type", "MYSQL")
                .param("datasource", "one")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(requestExcelDTO));

        RequestExcelDTO requestExcelDTO1 = new RequestExcelDTO();
        requestExcelDTO1.setSql("select * from ss_operator");
        requestExcelDTO1.setDatasource("two");
        requestExcelDTO1.setSaveDay(4);
        requestExcelDTO1.setFilePrefix("user1");
        requestExcelDTO1.setPageSize(2000);
        requestExcelDTO1.setExportDirectory("users");
        createUserExcelColumn1(requestExcelDTO1);
        RequestBuilder requestBuilder1 = MockMvcRequestBuilders.post("/export/excelToDisk")
                .param("type", "MYSQL")
                .param("datasource", "two")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(requestExcelDTO1));
        try {
            mockMvc.perform(requestBuilder1).andReturn().getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mockMvc.perform(requestBuilder).andReturn().getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void singleDataSrouceTest() {
        RequestExcelDTO requestExcelDTO = new RequestExcelDTO();
        requestExcelDTO.setSql("select * from ss_operator");
        requestExcelDTO.setDatasource("two");
        requestExcelDTO.setSaveDay(4);
        requestExcelDTO.setFilePrefix("user1");
        requestExcelDTO.setPageSize(2000);
        requestExcelDTO.setExportDirectory("users");
        createUserExcelColumn1(requestExcelDTO);
//        mockHttpServletRequest.setRequestURI("/export/excelToDisk");
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/export/excelToDisk")
                .param("type", "MYSQL")
                .param("datasource", "two")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(requestExcelDTO));
        try {
            mockMvc.perform(requestBuilder).andReturn().getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建ygp中ss_operator的表借口样式
     * @param requestExcelDTO
     */
    private void createUserExcelColumn1(RequestExcelDTO requestExcelDTO) {
        ExcelColumnDTO excelColumnDTO = new ExcelColumnDTO();
        excelColumnDTO.setColumnName("oper_id");
        excelColumnDTO.setExcelHeadName(Arrays.asList("威星", "编号"));

        ExcelColumnDTO excelColumnDTO1 = new ExcelColumnDTO();
        excelColumnDTO1.setColumnName("work_code");
        excelColumnDTO1.setExcelHeadName(Arrays.asList("威星","登录名"));

        ExcelColumnDTO excelColumnDTO2 = new ExcelColumnDTO();
        excelColumnDTO2.setColumnName("oper_name");
        excelColumnDTO2.setExcelHeadName(Arrays.asList("威星","用户名"));

        List<ExcelColumnDTO> excelColumnDTOList = new ArrayList<>(Arrays.asList(excelColumnDTO, excelColumnDTO1,
                excelColumnDTO2));
        requestExcelDTO.setExcelColumnDTOList(excelColumnDTOList);
    }

}
