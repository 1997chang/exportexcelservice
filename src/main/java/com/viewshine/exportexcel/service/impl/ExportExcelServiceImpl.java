package com.viewshine.exportexcel.service.impl;

import cn.viewshine.cloudthree.excel.ExcelFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.viewshine.exportexcel.config.AddDeleteFileConfig;
import com.viewshine.exportexcel.datasource.MongoDataSourceRouting;
import com.viewshine.exportexcel.entity.DeleteFile;
import com.viewshine.exportexcel.entity.ExcelColumnDTO;
import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.entity.enums.DataSourceType;
import com.viewshine.exportexcel.entity.thrift.Message;
import com.viewshine.exportexcel.entity.vo.ExportExcelVo;
import com.viewshine.exportexcel.entity.vo.QueryExcelVo;
import com.viewshine.exportexcel.entity.vo.ResultVO;
import com.viewshine.exportexcel.exceptions.BusinessException;
import com.viewshine.exportexcel.service.ExportExcelService;
import com.viewshine.exportexcel.utils.CommonUtils;
import com.viewshine.exportexcel.utils.DataSourceHolder;
import com.viewshine.exportexcel.utils.ExcelCallbackSocket;
import com.viewshine.exportexcel.utils.RedisUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.viewshine.exportexcel.constants.DataSourceConstants.DELETE_FILE_REDIS_PREFIX;
import static com.viewshine.exportexcel.constants.DataSourceConstants.EXPORT_EXCEL_REDIS_PREFIX;
import static com.viewshine.exportexcel.entity.enums.ExcelDownloadStatus.DOWNLOADING;
import static com.viewshine.exportexcel.entity.enums.ExcelDownloadStatus.FINISHED;
import static com.viewshine.exportexcel.exceptions.enums.BusinessErrorCode.*;
import static com.viewshine.exportexcel.utils.CommonUtils.getClientHost;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Service
public class ExportExcelServiceImpl implements ExportExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExportExcelServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoDataSourceRouting mongoDataSourceRouting;

    @Autowired
    private TaskExecutor exportExcelTaskExecutor;

    @Value("${export.excel.docFilePath}")
    private String docFilePath;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private TaskScheduler deleteExcelScheduler;

    @Autowired
    private AddDeleteFileConfig addDeleteFileConfig;

    @Override
    public ResultVO<ExportExcelVo> exportExcelToDisk(RequestExcelDTO requestExcelDTO, HttpServletRequest request) {
        String relativeFilePath = CommonUtils.generateExcelFileName(requestExcelDTO.getExportDirectory(),
                requestExcelDTO.getFilePrefix());
        DataSourceType activeDataSourceType = DataSourceHolder.getActiveDataSourceType();
        String finalSelectDataSourceName = DataSourceHolder.getActiveDataSourceName();

        ExportExcelVo exportExcelVo = new ExportExcelVo();
        exportExcelVo.setExcelId(CommonUtils.generateUUID());
        exportExcelVo.setStatus(DOWNLOADING);
        exportExcelVo.setUri(relativeFilePath.replace('\\', '/'));
        exportExcelVo.setUrl(CommonUtils.getExportUrl(request, exportExcelVo.getExcelId(),
                requestExcelDTO.getExportUrlPrefix()));
        String clientHost = getClientHost(request);
        //保存到Redis中，设置为下载状态
        redisUtils.set(EXPORT_EXCEL_REDIS_PREFIX + exportExcelVo.getExcelId(), exportExcelVo);
        exportExcelTaskExecutor.execute(() -> {
            try {
                logger.info("最终选择的数据库类型：[{}]", activeDataSourceType.getCName());
                logger.info("最终选择的数据库为：{}", finalSelectDataSourceName);
                DataSourceHolder.setActiveDataSourceName(finalSelectDataSourceName);
                DataSourceHolder.setActiveDataSourceType(activeDataSourceType);
                logger.info("准备将数据导出到Excel中");
                List<List<String>> excelContentData = getExcelContentData(requestExcelDTO);
                logger.info("查询出来的数据内容为：[{}]", JSON.toJSONString(excelContentData));
                List<List<String>> excelHeadName = getExcelHeadName(requestExcelDTO.getExcelColumnDTOList());
                logger.info("Excel表格的头数据内容：[{}]", JSON.toJSONString(excelHeadName));
                String filePath = getFileSavePath(relativeFilePath);
                ExcelFactory.writeExcel(Collections.singletonMap("sheet1", excelContentData),
                        Collections.singletonMap("sheet1", excelHeadName), filePath);
                logger.info("ExcelID：[{}]的文件导出成功，最终的文件路径地址；[{}]", exportExcelVo.getExcelId(), filePath);
                downloadDoThing(filePath, requestExcelDTO.getSaveDay(), clientHost,
                        requestExcelDTO.getThriftPort(), exportExcelVo);
            } catch (Exception e){
                logger.error("导出Excel出现错误。" +e.getMessage(), e);
                exportErrorHandle(exportExcelVo);
            }
        });
        return ResultVO.successResult(exportExcelVo);
    }

    @Override
    public ResultVO<ExportExcelVo> queryByExcelId(String uuid) {
        ExportExcelVo exportExcelVo = redisUtils.get(EXPORT_EXCEL_REDIS_PREFIX + uuid, ExportExcelVo.class);
        logger.info("从redis中获取key:[{}]的数据：{}", EXPORT_EXCEL_REDIS_PREFIX + uuid,
                JSONObject.toJSONString(exportExcelVo));
        return ResultVO.successResult(exportExcelVo);
    }

    @Override
    public ResultVO<QueryExcelVo> queryStatusByExcelId(String uuid) {
        ExportExcelVo exportExcelVo = redisUtils.get(EXPORT_EXCEL_REDIS_PREFIX + uuid, ExportExcelVo.class);
        logger.info("从redis中获取key:[{}]的数据：{}", EXPORT_EXCEL_REDIS_PREFIX + uuid,
                JSONObject.toJSONString(exportExcelVo));
        QueryExcelVo queryExcelVo = new QueryExcelVo();
        queryExcelVo.setFinish(false);
        if (Objects.nonNull(exportExcelVo) && Objects.equals(FINISHED, exportExcelVo.getStatus())) {
            queryExcelVo.setFinish(true);
        }
        return ResultVO.successResult(queryExcelVo);
    }

    /**
     * 当文件下载完成之后执行的业务逻辑
     * 1.将文件信息更新保存到Redis中，并设置过期时间
     * 2.将删除文件到延迟任务中，从而删除文件，并将删除文件
     * 3.通知请求方，提示下载完成
     */
    private void downloadDoThing(String filePath, int saveDayCount, String host, Integer thriftPort,
                                 ExportExcelVo exportExcelVo) {
        ExportExcelVo clone = null;
        try {
            clone = exportExcelVo.clone();
        } catch (CloneNotSupportedException e) {
            logger.error("克隆对象失败，{}", JSON.toJSONString(exportExcelVo));
            e.printStackTrace();
            throw new BusinessException(CLONE_OBJECT_ERROR);
        }
        clone.setStatus(FINISHED);

        redisUtils.set(EXPORT_EXCEL_REDIS_PREFIX + exportExcelVo.getExcelId(), clone , saveDayCount, TimeUnit.DAYS);

        deleteFileOnSchedule(filePath, saveDayCount, exportExcelVo.getExcelId());

        Message message = new Message();
        message.setExcelId(clone.getExcelId());
        message.setUrl(clone.getUrl());
        boolean success = ExcelCallbackSocket.executeExcelBack(host, thriftPort, message);
        if (!success) {
            logger.error("执行回调失败");
        }
    }

    /**
     * 获取Excel最终保存的路径地址
     * @param exportFileName 相对路径地址。
     * @return 保存的绝对路径地址
     */
    private String getFileSavePath(String exportFileName) {
        StringBuilder result = new StringBuilder(128);
        result.append(CommonUtils.formatPathOnSystem(docFilePath));
        char separatorChar = File.separatorChar;
        if (! Objects.equals(separatorChar, result.charAt(result.length() - 1))) {
            result.append(separatorChar);
        }
        return result.append(exportFileName).toString();
    }

    /**
     * 根据提供的Query查询内容，以及数据库的名称进行数据的查询
     * @param requestExcelDTO 请求导出的Excel对象
     * @return 导出的Excel数据内容
     */
    private List<List<String>> getExcelContentDataInMongo(final RequestExcelDTO requestExcelDTO) {
        String collectionName = requestExcelDTO.getCollectionName();
        if (StringUtils.isBlank(collectionName)) {
            logger.error("没有为Mongo数据库提供查询的收集器名称");
            throw new BusinessException(NO_COLLECTION_MONGO);
        }
        String dataSourceName = DataSourceHolder.getActiveDataSourceName();
        if (StringUtils.isBlank(dataSourceName) || !mongoDataSourceRouting.getDatasources().containsKey(dataSourceName)) {
            logger.error("提供的数据库名[{}]为空或者Mongo路由不包含这个数据库名称", dataSourceName);
            throw new BusinessException(DATASOURCE_NAME_ERROR);
        }
        logger.info("准备在MONGO的[{}]数据库中的[{}]集合中上执行查询语句", dataSourceName, collectionName);
        MongoOperations mongoOperations = mongoDataSourceRouting.getDatasources().get(dataSourceName);
        Query query = JSONObject.parseObject(requestExcelDTO.getSql(), Query.class);
        List<Map<String, Object>> dataContent = mongoOperations.find(query, JSONObject.class, collectionName)
                .stream().map(json -> (Map<String, Object>) json).collect(toList());
        return getFinishExcelData(requestExcelDTO, dataContent);
    }

    /**
     * 根据执行的SQL语句，SQL的参数值，以及查询导出的列进行数据的查询。
     * @param requestExcelDTO 请求导出的Excel对象
     * @return 返回查询的结果
     */
    private List<List<String>> getExcelContentDataInMysql(final RequestExcelDTO requestExcelDTO) {
        logger.info("在MYSQL数据库上执行导出任务，查询的SQL语句为：[{}]，参数值列表为：[{}]",
                requestExcelDTO.getSql(), JSONObject.toJSONString(requestExcelDTO.getSqlParams()));
        List<Map<String, Object>> dataContent =
                jdbcTemplate.queryForList(requestExcelDTO.getSql(), requestExcelDTO.getSqlParams());
        return getFinishExcelData(requestExcelDTO, dataContent);
    }

    /**
     * 用于获取最终导出的数据内容
     *      1.普通的数据内容直接放入其中
     *      2.如果映射关系mapping不为null的话，进行关系的映射
     *      3.如果公式不为空的话，计算公式的内容
     *      4.如果格式化不为空的话，进行格式化数据
     * @param requestExcelDTO 请求的数据内容
     * @param data 从数据库中查询的数据内容
     * @return 最终导出的数据内容
     */
    private List<List<String>> getFinishExcelData(final RequestExcelDTO requestExcelDTO,
                                                  final List<Map<String, Object>> data) {
        return data.stream().map(entry -> requestExcelDTO.getExcelColumnDTOList().stream()
                    .filter(columnDTO -> StringUtils.isNotBlank(columnDTO.getColumnName()))
                    .map(columnDTO -> {
                        if (MapUtils.isNotEmpty(columnDTO.getMapping())) {
                            String columnValue = Objects.toString(entry.getOrDefault(columnDTO.getColumnName(), ""), "");
                            return columnDTO.getMapping().getOrDefault(columnValue, "");
                        } else if (StringUtils.isNotBlank(columnDTO.getFormula())) {
                            return CommonUtils.computeFormula(columnDTO.getFormula(), entry).toPlainString();
                        } else if (StringUtils.isNotBlank(columnDTO.getFormat())) {
                            return Optional.ofNullable(TypeUtils.castToTimestamp(
                                    entry.getOrDefault(columnDTO.getColumnName(), null)))
                                    .map(Timestamp::toLocalDateTime)
                                    .map(time -> time.format(DateTimeFormatter.ofPattern(columnDTO.getFormat())))
                                    .orElse("");
                        } else {
                            return Objects.toString(entry.getOrDefault(columnDTO.getColumnName(), ""), "");
                        }
                    }).collect(toList())
        ).collect(Collectors.toList());
    }

    /**
     * 用于获取在任何指定的数据库中的导出数据内容。
     * 注意：最终选择的列，以requestExcelDTO属性中的excelColumnDTOList中指定的列的名称为准
     * @param requestExcelDTO 导出Excel的SQL语句，导出的列名等
     * @return 导出的数据内容
     */
    @NonNull
    private List<List<String>> getExcelContentData(final RequestExcelDTO requestExcelDTO) {
        logger.info("准备获取数据内容，执行的SQL语句为：[{}]", requestExcelDTO.getSql());
        logger.info("最终导出的列名有：{}", requestExcelDTO.getExcelColumnDTOList().stream().
                map(ExcelColumnDTO::getColumnName).filter(StringUtils::isNotBlank).collect(joining(",")));
        DataSourceType activeDataSourceType = DataSourceHolder.getActiveDataSourceType();
        switch (activeDataSourceType) {
            case MYSQL:
                return getExcelContentDataInMysql(requestExcelDTO);
            case MONGODB:
                return getExcelContentDataInMongo(requestExcelDTO);
            default:
                throw new BusinessException(DATABASE_TYPE_ERROR);
        }
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

    /**
     * 导出Excel异常处理
     * @param exportExcelVo 导出Excel返回的对象
     */
    private void exportErrorHandle(ExportExcelVo exportExcelVo) {
        logger.error("导出Excel出现错误，请检查。导出的Excel为：{}", JSON.toJSONString(exportExcelVo));
    }

    /**
     * 定时删除文件
     *  1.向Redis中添加一条删除记录，用于避免删除系统重启而没有删除文件，造成定时任务无法执行
     *  2.向定时执行器中添加一个任务
     * @param filePath 文件所在地址
     * @param saveDayCount 保存的天数
     * @param excelUUID Excel文件的唯一标识
     */
    private void deleteFileOnSchedule(String filePath, int saveDayCount, String excelUUID) {
        LocalDateTime deleteTime = LocalDateTime.now().plusDays(saveDayCount);
        logger.info("准备在[{}]时间删除[{}]文件",
                deleteTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), filePath);
        DeleteFile deleteFile = new DeleteFile(excelUUID, filePath, deleteTime);
        redisUtils.set(DELETE_FILE_REDIS_PREFIX + excelUUID, deleteFile);
        addDeleteFileConfig.deleteFile(deleteFile);
    }

}
