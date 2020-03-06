package com.viewshine.exportexcel.properties;

import lombok.Data;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * MongoClient的线程池的属性
 * @author changWei[changwei@viewshine.cn]
 */
@Data
public class MongoPoolProperties {

    /**
     * MongoClient线程池允许的最大连接数量，默认为100
     */
    private int maxSize = 100;

    /**
     * 表示连接池中允许的最小连接储量，就算无请求，也会在MongoClient中保存一个连接对象。默认为1,
     */
    private int minSize = 1;

    /**
     * 对于一个MongoClient操作，当MongoClient都在忙碌中，允许的最大等待队列大小，从而MongoClient可用。默认为500
     */
    private int maxWaitQueueSize = 500;

    /**
     * 一个操作请求最长等待一个MongoClient的时间，0表示不等待，负数表示无限等待。默认为2分钟，
     */
    private long maxWaitTimeMS = 1000 * 60 * 2;

    /**\
     * 一个MongoClient连接的最大存活时间，0表示没有限制。
     */
    private long maxConnectionLifeTimeMS;

    /**
     * 一个MongoClient的最大空闲时间，0表示没有限制
     */
    private long maxConnectionIdleTimeMS;

    /**
     * 在MongoClient的连接池上在执行第一次维护作业之前的等待时间
     */
    private long maintenanceInitialDelayMS;

    /**
     * 在MongoClient的连接池上执行维护作业的间隔时间，默认为1分钟
     */
    private long maintenanceFrequencyMS = MILLISECONDS.convert(1, MINUTES);

}
