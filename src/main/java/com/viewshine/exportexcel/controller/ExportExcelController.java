package com.viewshine.exportexcel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.viewshine.exportexcel.service.ExportExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@RestController
public class ExportExcelController {

    private final Logger logger = LoggerFactory.getLogger(ExportExcelController.class);

    @Autowired
    private ExportExcelService exportExcelService;

    @GetMapping("/exportExcel")
    public JSONObject exportExcel(String sql, String dataBase, List<List<String>> columnNameLists) {
        logger.info("要执行的SQL语句是：{}，连接的数据库是：{}，Excel各个表头是：{}", sql, dataBase,
                JSON.toJSONString(columnNameLists));
        exportExcelService.exportExcel(sql, dataBase, columnNameLists);
        JSONObject result = new JSONObject();
        result.put("resultCode", "200");
        result.put("resultMessage", "success");
        return result;
    }

}
