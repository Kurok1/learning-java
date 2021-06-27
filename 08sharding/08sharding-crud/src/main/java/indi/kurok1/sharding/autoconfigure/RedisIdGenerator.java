package indi.kurok1.sharding.autoconfigure;

import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Properties;

/**
 * 全局管理的id自增生成器
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.27
 */
public class RedisIdGenerator implements ShardingKeyGenerator {

    private Properties properties;

    private static StringRedisTemplate redisTemplate = null;

    public static void setRedisTemplate(StringRedisTemplate redisTemplate) {
        RedisIdGenerator.redisTemplate = redisTemplate;
    }

    /**
     * Generate key.
     *
     * @return generated key
     */
    @Override
    public Comparable<?> generateKey() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.increment("id");
    }

    /**
     * Get algorithm type.
     *
     * @return type
     */
    @Override
    public String getType() {
        return "redis_auto_increment";
    }

    /**
     * Get properties.
     *
     * @return properties of algorithm
     */
    @Override
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Set properties.
     *
     * @param properties properties of algorithm
     */
    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
