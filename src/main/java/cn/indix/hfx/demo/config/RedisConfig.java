package cn.indix.hfx.demo.config;

import cn.indix.hfx.demo.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;

/**
 * @author hfx
 * @version 1.0.0
 * @Description:
 * @Date: 2023/08/11 10:13
 */
@Configuration
@EnableConfigurationProperties(RedisConfigProperties.class)
public class RedisConfig {

    @Autowired
    private RedisConfigProperties redisConfigProperties;

    @Bean
    public JedisPool jedisPool() {
        JedisPool jedisPool = new JedisPool(redisConfigProperties.getHost(), redisConfigProperties.getPort());
        RedisUtil.setJedisPool(jedisPool);
        return jedisPool;
    }

    /**
     * 从jedis 4.x开始支持
     *
     * @return
     */
    @Bean
    public JedisPooled jedisPooled() {
        JedisPooled jedisPooled = new JedisPooled(redisConfigProperties.getHost(), redisConfigProperties.getPort());
        RedisUtil.setJedisPooled(jedisPooled);
        return jedisPooled;
    }

}
