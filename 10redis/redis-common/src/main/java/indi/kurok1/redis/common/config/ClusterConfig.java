package indi.kurok1.redis.common.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * 客户端配置
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.18
 */
public class ClusterConfig extends Properties {

    private static String CONFIG_FILE = "cluster.properties";

    private ClusterConfig() {
        try {
            reload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final ClusterConfig config = new ClusterConfig();

    private void reload() throws IOException {
        String file = Thread.currentThread().getContextClassLoader().getResource(CONFIG_FILE).getFile();
        FileReader reader = new FileReader(file);
        load(reader);
    }


    public static ClusterConfig instance(String configLocation) {
        ClusterConfig.CONFIG_FILE = configLocation;
        try {
            config.reload();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return config;
    }

    public static ClusterConfig instance() {
        return config;
    }

    public String getHost() {
        return getProperty(ClusterConfig.ClusterConfigKey.REDIS_SERVER);
    }

    public String getClusterName() {
        return getProperty(ClusterConfig.ClusterConfigKey.CLUSTER_NAME);
    }

    public int getDatabase() {
        return Integer.decode(getProperty(ClusterConfig.ClusterConfigKey.REDIS_DATABASE));
    }

    public int getPort() {
        return Integer.decode(config.getProperty(ClusterConfig.ClusterConfigKey.REDIS_PORT));
    }

    public String getPassword() {
        return config.getProperty(ClusterConfig.ClusterConfigKey.REDIS_PASSWORD);
    }

    public static class ClusterConfigKey {
        public static final String CLUSTER_NAME = "cluster.name";
        public static final String REDIS_SERVER = "redis.server";
        public static final String REDIS_PORT = "redis.port";
        public static final String REDIS_DATABASE = "redis.database";
        public static final String REDIS_PASSWORD = "redis.password";

    }
}
