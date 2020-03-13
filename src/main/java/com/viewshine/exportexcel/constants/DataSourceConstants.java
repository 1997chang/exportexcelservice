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
     * 表示web请求中，设置要连接的数据源的类型的参数名称，（MONGODB，MYSQL等等）
     */
    public static final String HTTP_DATASOURCE_TYPE = "type";

    /**
     * 表示静态资源的拦截的请求地址，如果是这个请求地址，就会进入静态目录下下载对应的目录
     */
    public static final String DOWNLOAD_FILE_URL = "/download";

    /**
     * 下载文件的拦截器地址，如果这个地址，就会进入DownloadInterceptor拦截器，
     *          进行地址进行重定向到/download，然后到静态目录下文件
     */
    public static final String DOWNLOAD_FILE_INTERCEPTOR = "/downloadExcel";

    /**
     * 表示文件下载之后，保存到Redis的前缀
     */
    public static final String EXPORT_EXCEL_REDIS_PREFIX = "export.excel.download.";

    /**
     * 保存要删除的File对象
     */
    public static final String DELETE_FILE_REDIS_PREFIX = "delete.file.";

    /**
     * 通知客户端下载完成的Redis前缀
     */
    public static final String CALLBACK_CLIENT_ERROR = "callback.client.error.";

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

    /**
     * 未知的请求地址
     */
    public static final String UNKNOWN = "unknown";
}
