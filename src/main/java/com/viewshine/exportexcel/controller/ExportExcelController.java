package com.viewshine.exportexcel.controller;

import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.entity.ResultVO;
import com.viewshine.exportexcel.service.ExportExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
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
     * 用于
     * @return
     */
    @PostMapping("/exportExcelToDisk")
    public ResultVO<Void> exportExcelToDisk(@Valid @RequestBody RequestExcelDTO requestExcelDTO,
                                            BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResultVO.errorResult(bindingResult.getFieldError().getDefaultMessage());
        }
        logger.info("准备写入到本地磁盘文件中，传递的参数内容为：[{}]", JSON.toJSONString(requestExcelDTO));
        exportExcelService.exportExcelToDisk(requestExcelDTO);
        return ResultVO.successResult();
    }

}
