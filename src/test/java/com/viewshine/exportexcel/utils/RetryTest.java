package com.viewshine.exportexcel.utils;

import com.github.rholder.retry.*;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 使用Guava-retry进行失败重试机制
 * @author changWei[changwei@viewshine.cn]
 */
public class RetryTest {

    /**
     * guava中的指数失败重试机制
     * 每一次以2的重复次数的幂，即：POW(2,重试次数)*multiplier作为休息的时间，最长等待1分钟，
     *  当POW(2,重试次数)*multiplier大于1分钟，则最长等待1分钟
     */
    @Test
    public void exponentTest() {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(aBoolean -> Objects.equals(aBoolean, false)) //返回结果为false，进行重试
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println(attempt.getAttemptNumber());
                    }
                })
                .withWaitStrategy(WaitStrategies.exponentialWait(100, 1, TimeUnit.MINUTES))
                .withStopStrategy(StopStrategies.stopAfterDelay(2, TimeUnit.MINUTES))
                .build();
        try {
            retryer.call(() -> {
                System.out.println(Thread.currentThread().getName());
                System.out.println(LocalDateTime.now());
                return false;
            });
        } catch (ExecutionException | RetryException e) {
            System.out.println(LocalDateTime.now());
            e.printStackTrace();
            System.out.println("最终通知客户端也失败了，");
        }
    }

    /**
     * fibonacci的重试机制：1,1,2,3,5,8....*multiplier的机制进行等待，最长等待30秒
     */
    @Test
    public void fibonacciWaitTest() {
        Retryer<Integer> build = RetryerBuilder.<Integer>newBuilder()
                .retryIfResult(i -> i > 1)
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("result:" + attempt.getResult());
                        System.out.println("number:" + attempt.getAttemptNumber());
                    }
                })
                .withWaitStrategy(WaitStrategies.fibonacciWait(100, 30, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterDelay(2, TimeUnit.MINUTES))
                .build();
        try {
            build.call(() -> {
                System.out.println(LocalDateTime.now());
                return ThreadLocalRandom.current().nextInt(3,6);
            });
        } catch (ExecutionException | RetryException e) {
            e.printStackTrace();
        }
    }

}
