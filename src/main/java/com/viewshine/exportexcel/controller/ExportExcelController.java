package com.viewshine.exportexcel.controller;

import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.entity.ResultVO;
import com.viewshine.exportexcel.service.ExportExcelService;
import com.viewshine.exportexcel.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

import static com.viewshine.exportexcel.constants.DataSourceConstants.DOWNLOAD_FILE_URL;

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
    public ResultVO<String> exportExcelToDisk(@Valid @RequestBody RequestExcelDTO requestExcelDTO,
                                            BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResultVO.errorResult(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        logger.info("准备写入到本地磁盘文件中，传递的参数内容为：[{}]", JSON.toJSONString(requestExcelDTO));
        String exportFileName = CommonUtils.generateExcelFileName(requestExcelDTO.getExportDirectory(),
                requestExcelDTO.getFilePrefix());
        exportExcelService.exportExcelToDisk(requestExcelDTO, exportFileName);
        return ResultVO.successResult(getExportUrl(request, exportFileName));
    }

    /**
     * 用于获取最终返回的URL地址
     * @param request 当前请求
     * @param exportExcelFileName 导出的文件名称
     * @return 导出的URL地址
     */
    private String getExportUrl(HttpServletRequest request, String exportExcelFileName) {
        return new StringBuilder(120).append(request.getScheme()).append("://").
            append(request.getServerName()).append(":").append(request.getServerPort())
            .append(request.getContextPath()).append(DOWNLOAD_FILE_URL).append(exportExcelFileName).toString();
    }

}
