package com.viewshine.exportexcel.service.impl;

import com.viewshine.exportexcel.service.ExportExcelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Service
public class ExportExcelServiceImpl implements ExportExcelService {

    @Override
    public void exportExcel(String sql, String dataBase, List<List<String>> columnNameLists) {

    }
}
