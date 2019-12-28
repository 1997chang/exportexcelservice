package com.viewshine.exportexcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SchedulerTaskTest {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootTest.class);

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private TaskScheduler deleteExcelScheduler;

    @Test
    public void test() throws InterruptedException {
        Date date = new Date();
        logger.info(date.toString());
        ScheduledFuture<?> schedule = taskScheduler.schedule(() -> {
            logger.info("5秒之后执行");
        }, new Date(date.getTime() + 5000));
        deleteExcelScheduler.schedule(() -> {
            logger.info("3秒之后执行");
            throw new NullPointerException();

        }, new Date(date.getTime() + 3000));
        Thread.sleep(6000);
    }

}
