package com.viewshine.exportexcel.utils;

import com.viewshine.exportexcel.entity.OperationEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.viewshine.exportexcel.constants.DataSourceConstants.*;
import static com.viewshine.exportexcel.entity.OperationEnum.LEFT_PARENTHESIS;
import static com.viewshine.exportexcel.entity.OperationEnum.RIGHT_PARENTHESIS;

/**
 * 一些常用的工具类
 * @author changWei[changwei@viewshine.cn]
 */
public final class CommonUtils {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    private static final String YYYYMMDDHHMMSS = "YYYYMMddHHmmss";

    private static final Map<Character, OperationEnum> operationMap;

    static {
        operationMap= Arrays.stream(OperationEnum.values())
                .collect(Collectors.toMap(OperationEnum::getOperation, Function.identity()));
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
        result.append(formatPathOnSystem(directory));
        //删除文件名的前导路径分隔符
        if (separatorChar == result.charAt(0)) {
            result.deleteCharAt(0);
        }
        if (separatorChar != result.charAt(result.length() -1)) {
            result.append(separatorChar);
        }
        result.append(prefix).append('_');
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
    public static String formatPathOnSystem(String filePath) {
        char separatorChar = File.separatorChar;
        if (separatorChar == '\\') {                                //window系统下，将所有的/转化为\
            return filePath.replace('/', separatorChar);
        } else if (separatorChar == '/'){                           //Linux系统下，将所有的\转化为/
            return filePath.replace('\\', separatorChar);
        } else {
            logger.error("不能确定当前系统。File.separatorChar不是[\\]也不是[/]");
            throw new RuntimeException("不能确定当前系统。File.separatorChar不是[\\]也不是[/]");
        }
    }

    /**
     * 用于计算公式的最终计算值
     * @param formula 表示执行的公式
     * @param values 公式中对应的值
     * @return 返回计算值
     */
    public static BigDecimal computeFormula(@NonNull String formula, @NonNull Map<String, Object> values) {
        Stack<OperationEnum> operation = new Stack<>();
        Stack<BigDecimal> numberStack = new Stack<>();
        int startIndex = 0;
        int currentIndex = 0;
        int formulaLength = formula.length();
        boolean addNumber = true;
        for (; currentIndex < formulaLength; currentIndex ++) {
            if (!operationMap.containsKey(formula.charAt(currentIndex))) {
                continue;
            }
            OperationEnum currentOperation = operationMap.get(formula.charAt(currentIndex));
            if (currentOperation != LEFT_PARENTHESIS && currentOperation != RIGHT_PARENTHESIS) {
                if (addNumber) {
                    String operationNum = StringUtils.deleteWhitespace(formula.substring(startIndex, currentIndex));
                    numberStack.add(new BigDecimal(values.getOrDefault(operationNum, operationNum).toString()));
                }
                if (!operation.isEmpty() && currentOperation.isCompute(operation.peek())) {
                    numberStack.push(operation.pop().compute(numberStack.pop(), numberStack.pop()));
                }
                addNumber = true;
            } else if (currentOperation == RIGHT_PARENTHESIS) {
                if (addNumber) {
                    String operationNum = StringUtils.deleteWhitespace(formula.substring(startIndex, currentIndex));
                    numberStack.add(new BigDecimal(values.getOrDefault(operationNum, operationNum).toString()));
                }
                addNumber =false;
                while (operation.peek() != LEFT_PARENTHESIS) {
                    numberStack.push(operation.pop().compute(numberStack.pop(), numberStack.pop()));
                }
                operation.pop();
                startIndex = currentIndex + 1;
                continue;
            }
            operation.push(currentOperation);
            startIndex = currentIndex + 1;
        }
        if (startIndex < formulaLength) {
            String operationNum = StringUtils.deleteWhitespace(formula.substring(startIndex, currentIndex));
            numberStack.add(new BigDecimal(values.getOrDefault(operationNum, operationNum).toString()));
        }
        while (!operation.isEmpty()) {
            numberStack.push(operation.pop().compute(numberStack.pop(), numberStack.pop()));
        }
        return numberStack.pop();
    }

    /**
     * 用于返回一个UUID唯一标识
     * @return UUIDString
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 用于获取最终返回的URL地址
     *      1.如果exportUrlPrefix不为空，以这个地址为返回地址的前缀，否则根据request的请求，返回远程服务器的地址
     *      2.进行字符串的拼接，返回客户端最终可以下载的URL地址
     * @param request 当前请求
     * @param excelId 导出的文件名称的唯一表示
     * @param exportUrlPrefix 表示导出路径的前缀
     * @return 导出的URL地址
     */
    public static String getExportUrl(HttpServletRequest request, String excelId, String exportUrlPrefix) {
        StringBuilder result = new StringBuilder(120);
        if (StringUtils.isNotBlank(exportUrlPrefix)) {
            result.append(exportUrlPrefix);
        } else {
            result.append(request.getScheme()).append("://").
                    append(request.getServerName()).append(":").append(request.getServerPort())
                    .append(request.getContextPath());
        }
        return result.append(DOWNLOAD_FILE_INTERCEPTOR).append("?")
                .append(EXCELPARAM).append('=').append(excelId).toString();
    }

    /**
     * 根据客户端的请求获取客户端的真实IP地址地址。
     *     1.如果请求对象为空，返回127.0.0.1
     *     2.否则获取真实IP地址
     * @param request 请求对象
     * @return 请求的真实IP地址
     */
    public static String getClientHost(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            logger.warn("请求对象为NULL，返回本地地址：[{}]", LOCAL_HOST);
            return LOCAL_HOST;
        }
        String ipAddress;
        try {
            ipAddress = request.getHeader("X-Forwarded-For");
            if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (LOCAL_HOST.equals(ipAddress)) {
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage() + "获取客户端地址失败，返回本地IP地址：" + LOCAL_HOST, e);
                        return LOCAL_HOST;
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
        } catch (Exception e) {
            logger.error("获取客户端HOST错误，返回默认的HOST：[{}]", LOCAL_HOST);
            return LOCAL_HOST;
        }
        if (StringUtils.isBlank(ipAddress)) {
            logger.warn("没有获取到请求的真实IP地址，返回默认的本地地址：[{}]", LOCAL_HOST);
            return LOCAL_HOST;
        } else {
            String host = StringUtils.split(ipAddress, ",")[0];
            logger.info("客户端真实的IP地址为：[{}]", host);
            return host;
        }
    }

}
