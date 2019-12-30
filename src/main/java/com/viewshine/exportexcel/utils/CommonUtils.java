package com.viewshine.exportexcel.utils;

import com.viewshine.exportexcel.entity.OperationEnum;
import com.viewshine.exportexcel.exceptions.CommonRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.viewshine.exportexcel.constants.DataSourceConstants.DOWNLOAD_CALLBACK_PATH;
import static com.viewshine.exportexcel.constants.DataSourceConstants.DOWNLOAD_FILE_URL;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public final class CommonUtils {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    private static final String YYYYMMDDHHMMSS = "YYYYMMddHHmmss";

    private static final Map<Character, OperationEnum> operationMap;

    static {
        operationMap = new HashMap<>((int)Math.ceil(OperationEnum.values().length * 4.0 /3));
        Arrays.stream(OperationEnum.values()).forEach(operationEnum ->
                operationMap.put(operationEnum.getOperation(), operationEnum));
    }

    private CommonUtils() {}

    /**
     * 根据一个前缀生成Excel文件的路径文件名，表示一个相对路径的文件名称
     * 格式：${directory}/${prefix}_时间6位随机数.xlsx
     * @param directory 文件所在的目录
     * @param prefix 文件名前缀
     * @return 文件名称
     */
    public static String generateExcelFileName(String directory, String prefix) {
        StringBuilder result = new StringBuilder(128);
        char separatorChar = File.separatorChar;
        result.append(formatFileOnSystem(directory));
        result.append(separatorChar);
        result.append(prefix + '_');
        String currentDateString = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS).format(LocalDateTime.now());
        result.append(currentDateString);
        result.append(ThreadLocalRandom.current().nextInt(100000, 1000000));
        result.append(".xlsx");
        return result.toString();
    }

    /**
     * 根据当前系统，将路径地址转化为当前系统正确地址
     * @return 对应系统的路径地址
     */
    public static String formatFileOnSystem(String filePath) {
        char separatorChar = File.separatorChar;
        if (separatorChar == '\\') {
            return filePath.replace('/', separatorChar);
        } else if (separatorChar == '/'){
            return filePath.replace('\\', separatorChar);
        } else {
            logger.error("不能确定当前系统");
            //TODO 抛出不能确定当前系统
            throw new CommonRuntimeException();
        }
    }

    /**
     * 用于计算公式的最终计算值
     * @param formula 表示执行的公式
     * @param values 公式中对应的值
     * @return 返回计算值
     */
    public static BigDecimal computeFormula(@NonNull String formula, @NonNull Map<String, String> values) {
        Stack<OperationEnum> operation = new Stack<>();
        Stack<BigDecimal> numberStack = new Stack<>();
        int startIndex = 0;
        int currentIndex = 0;
        int formulaLength = formula.length();
        while (currentIndex < formulaLength) {
            //TODO 没有完成括号的计算
            if (operationMap.containsKey(formula.charAt(currentIndex))) {
                OperationEnum currentOperation = operationMap.get(formula.charAt(currentIndex));
                numberStack.push(new BigDecimal(values.get(StringUtils.deleteWhitespace(formula.substring(startIndex,
                        currentIndex)))));
                if (!operation.isEmpty() && currentOperation.isCompute(operation.peek())) {
                    numberStack.push(operation.pop().compute(numberStack.pop(), numberStack.pop()));
                }
                operation.push(currentOperation);
                startIndex = currentIndex + 1;
            }
            currentIndex++;
        }
        numberStack.push(new BigDecimal(values.get(StringUtils.deleteWhitespace(formula.substring(startIndex,
                formulaLength)))));
        while (!operation.isEmpty()) {
            numberStack.push(operation.pop().compute(numberStack.pop(), numberStack.pop()));
        }
        return numberStack.pop();
    }

    /**
     * 用于返回一个唯一标识
     * @return UUIDString
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 用于获取最终返回的URL地址
     * @param request 当前请求
     * @param exportExcelFileName 导出的文件名称
     * @return 导出的URL地址
     */
    public static String getExportUrl(HttpServletRequest request, String exportExcelFileName) {
        return new StringBuilder(120).append(request.getScheme()).append("://").
                append(request.getServerName()).append(":").append(request.getServerPort())
                .append(request.getContextPath()).append(DOWNLOAD_FILE_URL).append("/").append(exportExcelFileName).toString();
    }

    /**
     * 用于获取回调的通知地址
     * @param callback
     * @return
     */
    public static String getCallBackUrl(String callback) {
        if (!(callback.startsWith("http://") || callback.startsWith("https://"))) {
            logger.error("callBack地址错误，没有以http://或者https://开头");
            return null;
        }
        return new StringBuilder(120).append(callback).append(DOWNLOAD_CALLBACK_PATH).toString();
    }

}
