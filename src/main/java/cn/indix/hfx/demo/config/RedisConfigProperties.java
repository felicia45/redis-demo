package cn.indix.hfx.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hfx
 * @version 1.0.0
 * @Description:
 * @Date: 2023/08/11 10:29
 */
@Data
@ConfigurationProperties(prefix = "redis")
public class RedisConfigProperties {

    private String host;

    private Integer port;

}
