package com.viewshine.exportexcel.service.impl;

import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.properties.DataSourceNameHolder;
import com.viewshine.exportexcel.service.ExportExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Service
public class ExportExcelServiceImpl implements ExportExcelService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskExecutor exportExcelTaskExecutor;

    @Override
    public void exportExcel(String sql, String dataBase, List<List<String>> columnNameLists) {
        String activeDataSource = DataSourceNameHolder.getActiveDataSource();
        exportExcelTaskExecutor.execute(() -> {
            DataSourceNameHolder.setActiveDataSource(activeDataSource);
            System.out.println(JSON.toJSONString(jdbcTemplate.queryForList(sql)));
        });
    }
}
