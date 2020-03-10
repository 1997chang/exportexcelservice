package com.viewshine.exportexcel.utils;

import com.viewshine.exportexcel.entity.enums.DataSourceType;

import static com.viewshine.exportexcel.constants.DataSourceConstants.DEFAULT_DATASOURCE_NAME;

/**
 * 这个表示数据库名称选择器，每个线程过这个进行选择具体的数据库
 *      1.保存选择的数据库的类型
 *      2.保存选择的数据库的名称
 * @author ChangWei[changwei@viewshine.cn]
 */
public class DataSourceHolder {

    private final static ThreadLocal<String> dataSourceName = ThreadLocal.withInitial(() -> DEFAULT_DATASOURCE_NAME);

    private final static ThreadLocal<DataSourceType> dataSourceType = new ThreadLocal<>();

    /**
     * 获取当前线程的的数据源的名称
     * @return
     */
    public static String getActiveDataSourceName() {
        return dataSourceName.get();
    }

    /**
     * 设置数据源的名称
     * @param activeDataSource
     */
    public static void setActiveDataSourceName(String activeDataSource) {
        dataSourceName.set(activeDataSource);
    }

    /**
     * 清空数据源的名称
     */
    public static void clearDataSourceName() {
        dataSourceName.remove();
    }

    /**
     * 设置激活的数据源的类型
     * @param type 数据源的类型
     */
    public static void setActiveDataSourceType(DataSourceType type) {
        dataSourceType.set(type);
    }

    /**
     * 获取当前线程选择的数据源的类型
     */
    public static DataSourceType getActiveDataSourceType() {
        return dataSourceType.get();
    }

    /**
     * 用于清空当前线程的数据源的类型
     */
    public static void clearDataSourceType() {
        dataSourceType.remove();
    }

}
