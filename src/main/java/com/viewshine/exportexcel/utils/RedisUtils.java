package com.viewshine.exportexcel.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis的工具类：某一个Key是否存在，保存KeyValue值，获取某一个Key的值，删除某一个Key的值
 * @author ChangWei[changwei@viewshine.cn]
 */
@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, valueToString(value));
    }

    public void set(String key, Object value, long seconds){
        stringRedisTemplate.opsForValue().set(key, valueToString(value), seconds, TimeUnit.SECONDS);
    }

    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, valueToString(value), timeout, timeUnit);
    }

    public <T> T get(String key, Class<T> zclass) {
        String value = stringRedisTemplate.opsForValue().get(key);
        //FastJSON中已经进行了value的空处理，如果为null，将直接返回null
        return JSON.parseObject(value, zclass);
    }

    public <T> List<T> getList(String key, Class<T> zclass) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return JSON.parseArray(value, zclass);
    }

    public Boolean exits(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 将一个对象转化为字符串
     * @param value 转化的对象
     * @return 对象字符串
     */
    private String valueToString(Object value) {
        if (value instanceof String) {
            return value.toString();
        }
        return JSON.toJSONString(value);
    }

}
