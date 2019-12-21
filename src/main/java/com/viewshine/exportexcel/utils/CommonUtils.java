package com.viewshine.exportexcel.utils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public final class CommonUtils {

    public static final String YYYYMMDDHHMMSS = "YYYYMMddHHmmss";

    private CommonUtils() {}

    /**
     * 根据一个前缀生成Excel文件的路径文件名
     * @param directory 文件所在的目录
     * @param prefix 文件名前缀
     * @return
     */
    public static String generateExcelFileName(String directory, String prefix) {
        StringBuilder result = new StringBuilder(128);
        char separatorChar = File.separatorChar;
        if (separatorChar == '\\') {
            result.append(directory.replace('/', separatorChar));
        } else if (separatorChar == '/'){
            result.append(directory.replace('\\', separatorChar));
        }
        result.append(separatorChar);
        result.append(prefix + '_');
        String currentDateString = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS).format(LocalDateTime.now());
        result.append(currentDateString);
        result.append(".xlsx");
        return result.toString();
    }

}
