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
     * 表示下载文件的路径地址
     */
    public static final String DOWNLOAD_FILE_URL = "/download";

    /**
     * 表示下载文件的拦截器地址，这个最好不要修改
     */
    public static final String DOWNLOAD_FILE_URL_HANDLE = DOWNLOAD_FILE_URL + "/**";

    /**
     * 表示文件下载之后，保存到Redis的前缀
     */
    public static final String EXPORT_EXCEL_REDIS_PREFIX = "export.excel.download.";

    /**
     * 表示回调通知的地址
     */
    public static final String DOWNLOAD_CALLBACK_PATH = "/export/excel/start/callback";

}
