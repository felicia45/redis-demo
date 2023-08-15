package cn.indix.hfx.demo.util;

import com.alibaba.fastjson.JSON;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.SetParams;

/**
 * @author hfx
 * @version 1.0.0
 * @Description: Redis 工具类
 * @Date: 2023/08/11 10:39
 */
public class RedisUtil {

    private static final String OK = "OK";

    private static JedisPool jedisPool;

    private static JedisPooled jedisPooled = new JedisPooled("127.0.0.1", 6379);

    public static boolean del(String key) {
        long result = jedisPooled.del(key);
        return result == 0L;
    }

    public static boolean exists(String key) {
        return jedisPooled.exists(key);
    }

    public static long expireTime(String key) {
        return jedisPooled.expireTime(key);
    }


    //################################## String #####################################

    public static String set(String key, String value) {
        return jedisPooled.set(key, value);
    }

    public static String get(String key) {
        return jedisPooled.get(key);
    }

    public static String setex(String key, long seconds, String value) {
        return jedisPooled.setex(key, seconds, value);
    }

    /**
     * set if key not exist
     *
     * @param key
     * @param value
     * @return 1设置成功 0设置失败
     */
    public static long setnx(String key, String value) {
        return jedisPooled.setnx(key, value);
    }

    public static boolean set(String key, String value, SetParams setParams) {
        String result = jedisPooled.set(key, value, setParams);
        return OK.equals(result);
    }

    public static String set(String key, Object value) {
        String jsonString = JSON.toJSONString(value);
        return jedisPooled.set(key, jsonString);
    }

    public static <T> T get(String key, Class<T> t) {
        String jsonString = jedisPooled.get(key);
        return JSON.parseObject(jsonString, t);
    }

    public static String setex(String key, long seconds, Object value) {
        String jsonString = JSON.toJSONString(value);
        return jedisPooled.setex(key, seconds, jsonString);
    }

    public static long setnx(String key, Object value) {
        String jsonString = JSON.toJSONString(value);
        return jedisPooled.setnx(key, jsonString);
    }

    public static boolean set(String key, Object value, SetParams setParams) {
        String jsonString = JSON.toJSONString(value);
        String result = jedisPooled.set(key, jsonString, setParams);
        return OK.equals(result);
    }


    public static void main(String[] args) {

    }


    public static void setJedisPool(JedisPool jedisPool) {
        RedisUtil.jedisPool = jedisPool;
    }

    public static void setJedisPooled(JedisPooled jedisPooled) {
        RedisUtil.jedisPooled = jedisPooled;
    }


}
