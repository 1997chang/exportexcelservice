package com.viewshine.exportexcel.service.impl;

import cn.viewshine.cloudthree.excel.ExcelFactory;
import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.entity.ExcelColumnDTO;
import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.entity.vo.ExportExcelVo;
import com.viewshine.exportexcel.entity.vo.QueryExcelVo;
import com.viewshine.exportexcel.entity.vo.ResultVO;
import com.viewshine.exportexcel.exceptions.CommonRuntimeException;
import com.viewshine.exportexcel.properties.DataSourceNameHolder;
import com.viewshine.exportexcel.service.ExportExcelService;
import com.viewshine.exportexcel.utils.CommonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.viewshine.exportexcel.entity.enums.ExcelDownloadStatus.DOWNLOADING;
import static java.util.stream.Collectors.joining;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Service
public class ExportExcelServiceImpl implements ExportExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExportExcelServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskExecutor exportExcelTaskExecutor;

    @Value("${export.excel.docFilePath}")
    private String docFilePath;

    @Override
    public ResultVO<ExportExcelVo> exportExcelToDisk(RequestExcelDTO requestExcelDTO, HttpServletRequest request) {
        String relativeFilePath = CommonUtils.generateExcelFileName(requestExcelDTO.getExportDirectory(),
                requestExcelDTO.getFilePrefix());

        if (StringUtils.isNotBlank(requestExcelDTO.getDatasource())) {
            DataSourceNameHolder.setActiveDataSource(requestExcelDTO.getDatasource());
        }
        String finalSelectDataSourceName = DataSourceNameHolder.getActiveDataSource();
        exportExcelTaskExecutor.execute(() -> {
            try {
                logger.info("最终选择的数据库为：{}", finalSelectDataSourceName);
                DataSourceNameHolder.setActiveDataSource(finalSelectDataSourceName);
                logger.info("准备将数据导出到Excel中");
                List<List<String>> excelContentData = getExcelContentData(requestExcelDTO);
                logger.info("查询出来的数据内容为：[{}]", JSON.toJSONString(excelContentData));
                List<List<String>> excelHeadName = getExcelHeadName(requestExcelDTO.getExcelColumnDTOList());
                logger.info("Excel表格的头数据内容：[{}]", JSON.toJSONString(excelHeadName));
                String filePath = getFileSavePath(relativeFilePath);
                ExcelFactory.writeExcel(Collections.singletonMap("sheet1", excelContentData),
                        Collections.singletonMap("sheet1", excelHeadName), filePath);
                logger.info("最终的文件路径地址；[{}]", filePath);
                //TODO 下载完成，通知下载方

            } catch (Exception e){
                logger.error("导出Excel出现错误。" +e.getMessage(), e);
                throw new CommonRuntimeException();
            }
        });
        ExportExcelVo exportExcelVo = new ExportExcelVo();
        exportExcelVo.setExcelId(CommonUtils.generateUUID());
        exportExcelVo.setStatus(DOWNLOADING);
        exportExcelVo.setUrl(CommonUtils.getExportUrl(request, relativeFilePath));
        //TODO 保存到Redis中
        //TODO 使用延迟时间，加入到延期任务，从而完成超时执行任务
        return ResultVO.successResult(exportExcelVo);
    }

    @Override
    public ResultVO<QueryExcelVo> queryByExcelId(String uuid) {
        //TODO 从Redis中返回下载完成情况
        return null;
    }

    /**
     * 获取Excel最终保存的路径地址
     * @param exportFileName 相对路径地址。
     * @return 保存的绝对路径地址
     */
    private String getFileSavePath(String exportFileName) {
        StringBuilder result = new StringBuilder(128);
        result.append(CommonUtils.formatFileOnSystem(docFilePath));
        char separatorChar = File.separatorChar;
        if (! Objects.equals(separatorChar, result.charAt(result.length() - 1))) {
            result.append(separatorChar);
        }
        return result.append(exportFileName).toString();
    }

    /**
     * 用于获取SQL语句中执行的数据内容数据。
     * 注意：最终选择的列，以requestExcelDTO属性中的excelColumnDTOList中指定的列的名称为准
     * @param requestExcelDTO 导出Excel的SQL语句，导出的列名等
     * @return SQL执行的数据内容
     */
    @NonNull
    private List<List<String>> getExcelContentData(final RequestExcelDTO requestExcelDTO) {
        logger.info("准备获取数据内容，执行的SQL语句为：[{}]", requestExcelDTO.getSql());
        logger.info("最终导出的列名有：{}", requestExcelDTO.getExcelColumnDTOList().stream().
                map(ExcelColumnDTO::getColumnName).filter(StringUtils::isNotBlank).collect(joining(",")));
        long columnCount = requestExcelDTO.getExcelColumnDTOList().stream().map(ExcelColumnDTO::getColumnName).
                filter(StringUtils::isNotBlank).count();
        final Map<String, String> dataMap = new HashMap<>((int) Math.ceil(columnCount * 4 / 3));

        List<List<String>> result = jdbcTemplate.query(requestExcelDTO.getSql(), (rs, rowCount) -> {
            List<String> itemData = new ArrayList<>();
            requestExcelDTO.getExcelColumnDTOList().stream().filter(columnDTO ->
                    StringUtils.isNotBlank(columnDTO.getColumnName())).forEach(columnDTO -> {
                String dataValue;
                try {
                    if (StringUtils.isNotBlank(columnDTO.getFormula())) {
                        dataValue = CommonUtils.computeFormula(columnDTO.getFormula(), dataMap).toPlainString();
                    } else if (StringUtils.isNotBlank(columnDTO.getFormat())) {
                        LocalDateTime localDateTime = rs.getTimestamp(columnDTO.getColumnName(),
                                Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).toLocalDateTime();
                        dataValue = localDateTime.format(DateTimeFormatter.ofPattern(columnDTO.getFormat()));
                    } else {
                        dataValue = rs.getString(columnDTO.getColumnName());
                    }
                    itemData.add(dataValue);
                } catch (Exception e) {
                    logger.error("获取SQL数据内容错误" + e.getMessage(), e);
                    //TODO 获取数据库中的数据内容错误，抛出相应错误
                    throw new CommonRuntimeException();
                }
                dataMap.put(columnDTO.getColumnName(), dataValue);
            });
            dataMap.clear();
            return itemData;
        }, requestExcelDTO.getSqlParams());
        return result;
    }

    /**
     * 获取导出Excel表格的表头数据内容
     * 获取所有ColumnName不为空的，如果HeadName为空，则使用ColumnName作为表头
     * @param excelColumnDTOList 导出各个列的数据内容
     * @return 返回表头数据内容
     */
    private static List<List<String>> getExcelHeadName(List<ExcelColumnDTO> excelColumnDTOList) {
        if (excelColumnDTOList == null) {
            return (List<List<String>>) Collections.EMPTY_LIST;
        }
        return excelColumnDTOList.stream().filter(excelColumnDTO ->
                StringUtils.isNotBlank(excelColumnDTO.getColumnName())).map(excelColumnDTO -> {
                    if(CollectionUtils.isEmpty(excelColumnDTO.getExcelHeadName())) {
                        return Collections.singletonList(excelColumnDTO.getColumnName());
                    } else {
                        return excelColumnDTO.getExcelHeadName();
                    }
                }).collect(Collectors.toList());
    }

}
