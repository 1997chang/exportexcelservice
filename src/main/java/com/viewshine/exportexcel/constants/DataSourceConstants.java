package com.viewshine.exportexcel.constants;

/**
 * @author ChangWei[changwei@viewshine.cn]
 */
public class DataSourceConstants {

    /**
     * 表示没有要选择的数据源的名称时，提供一个默认的数据源的名称
     */
    public static final String DEFAULT_DATASOURCE_NAME = "master";

    /**
     * 表示WEB请求中，设置要选择的数据源的参数名称
     */
    public static final String HTTP_DATASOURCE_NAME = "datasource";

    /**
     * 表示下载文件的路径地址,静态资源对应的目录
     */
    public static final String DOWNLOAD_FILE_URL = "/download";

    /**
     * 表示静态资源对应地址
     */
    public static final String DOWNLOAD_FILE_URL_HANDLE = DOWNLOAD_FILE_URL + "/**";

    /**
     * 表示今天资源下载的拦截器
     */
    public static final String DOWNLOAD_FILE_INTERCEPTOR = "/downloadExcel";

    /**
     * 表示文件下载之后，保存到Redis的前缀
     */
    public static final String EXPORT_EXCEL_REDIS_PREFIX = "export.excel.download.";

    /**
     * 表示回调通知的地址
     */
    public static final String DOWNLOAD_CALLBACK_PATH = "/export/excel/start/callback";

    /**
     * 表示Excel唯一表示的参数名称
     */
    public static final String EXCELPARAM = "excelId";

    /**
     * 表示Thrift的连接超时时间
     */
    public static final int THRIFT_TIMEOUT = 3000;

    /**
     * 表示Thrift连接的客户端HOST
     */
    public static final String LOCAL_HOST = "127.0.0.1";

    public static final String UNKNOWN = "unknown";
}
