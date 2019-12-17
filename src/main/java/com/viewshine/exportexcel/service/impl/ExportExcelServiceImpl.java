package com.viewshine.exportexcel.service.impl;

import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.properties.DataSourceNameHolder;
import com.viewshine.exportexcel.service.ExportExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
    public void exportExcelToDisk(RequestExcelDTO requestExcelDTO) {
        String activeDataSource = DataSourceNameHolder.getActiveDataSource();
        exportExcelTaskExecutor.execute(() -> {
            DataSourceNameHolder.setActiveDataSource(activeDataSource);
            //TODO 1执行SQL语句
            //TODO 将执行结果导出到Excel表格中
        });
    }
}
