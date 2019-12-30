package com.viewshine.exportexcel.controller;

import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.entity.vo.ExportExcelVo;
import com.viewshine.exportexcel.entity.vo.ResultVO;
import com.viewshine.exportexcel.service.ExportExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@RestController
public class ExportExcelController {

    private final Logger logger = LoggerFactory.getLogger(ExportExcelController.class);

    @Autowired
    private ExportExcelService exportExcelService;

    /**
     * 用于导出Excel到本地文件中
     * @return 返回导出的Excel路径以及唯一表示
     */
    @PostMapping("/export/excelToDisk")
    public ResultVO<ExportExcelVo> exportExcelToDisk(@Valid @RequestBody RequestExcelDTO requestExcelDTO,
                                                      HttpServletRequest request) {
        //TODO 完成MD5取值，从而防止重复提交
        logger.info("准备写入到本地磁盘文件中，传递的参数内容为：[{}]", JSON.toJSONString(requestExcelDTO));
        return exportExcelService.exportExcelToDisk(requestExcelDTO, request);
    }

    /**
     * 用于查询某一个Excel文件是否下载完成
     * @param ExcelId Excel文件对应的唯一表示
     * @return 下载状态
     */
    @GetMapping("/queryByExcelId/{ExcelId}")
    public ResultVO<ExportExcelVo> queryByUUID(@PathVariable("ExcelId") String ExcelId) {
        logger.info("准备根据UUID查询下载状态，excelId：{}", ExcelId);
        return exportExcelService.queryByExcelId(ExcelId);
    }

}
