package com.viewshine.exportexcel.config;

import com.viewshine.exportexcel.entity.DeleteFile;
import com.viewshine.exportexcel.exceptions.BusinessException;
import com.viewshine.exportexcel.utils.RedisUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.util.Objects;

import static com.viewshine.exportexcel.constants.DataSourceConstants.DELETE_FILE_REDIS_PREFIX;
import static com.viewshine.exportexcel.constants.DataSourceConstants.EXPORT_EXCEL_REDIS_PREFIX;
import static com.viewshine.exportexcel.exceptions.enums.BusinessErrorCode.DELETE_FILE_ERROR;

/**
 * 用于在重启的时候，向定时任务中添加删除文件，用于避免文件的堆积
 * @author changWei[changwei@viewshine.cn]
 */
@Component
public class AddDeleteFileConfig implements ApplicationRunner {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AddDeleteFileConfig.class);

    @Autowired
    private TaskScheduler deleteExcelScheduler;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        redisUtils.getKeys(DELETE_FILE_REDIS_PREFIX + "*").stream()
                .map(key -> redisUtils.get(key, DeleteFile.class))
                .filter(Objects::nonNull)
                .forEach(this::deleteFile);
    }

    /**
     * 用于定时删除文件方法
     * @param deleteFile 删除文件的内容
     */
    public void deleteFile(DeleteFile deleteFile) {
        deleteExcelScheduler.schedule(() -> {
            try {
                Files.deleteIfExists(Paths.get(deleteFile.getPath()));
                redisUtils.delete(DELETE_FILE_REDIS_PREFIX + deleteFile.getUUID());
                redisUtils.delete(EXPORT_EXCEL_REDIS_PREFIX + deleteFile.getUUID());
                logger.info("删除文件成功。文件路径为：[{}]", deleteFile.getPath());
            } catch (IOException e) {
                logger.error("删除文件失败，请检查，文件名为：[{}]", deleteFile.getPath());
                logger.error(e.getMessage(), e);
                throw new BusinessException(DELETE_FILE_ERROR);
            }
        }, deleteFile.getExpire().toInstant(ZoneOffset.ofHours(8)));
    }
}
