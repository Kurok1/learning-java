package indi.kurok1.redis.common.connect;

import indi.kurok1.redis.common.config.ClusterConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis连接提供方
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 */
public class ConnectionProvider {

    private final ClusterConfig config;
    private JedisPool jedisPool;
    private final String registerValue = "1";

    public ConnectionProvider() {
        config = ClusterConfig.instance();
        if (config == null) {
            throw new IllegalArgumentException();
        }
        init();
    }

    public ConnectionProvider(String configFile) {
        config = ClusterConfig.instance(configFile);
        if (config == null) {
            throw new IllegalArgumentException();
        }
        init();
    }

    protected void init() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, config.getHost(), config.getPort(), 30*1000, config.getPassword(), config.getDatabase());

        Jedis jedis = this.jedisPool.getResource();
        if (!jedis.exists(config.getClusterName())) {
            jedis.set(config.getClusterName(), registerValue);
        } else throw new IllegalStateException();

    }

    public Jedis getConnection() {
        return this.jedisPool.getResource();
    }

    private static final ConnectionProvider instance = new ConnectionProvider();

    public static ConnectionProvider getInstance() {
        return instance;
    }

    public void destory() {
        Jedis jedis = this.jedisPool.getResource();
        jedis.del(config.getClusterName());
    }

}
