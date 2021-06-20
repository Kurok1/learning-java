package indi.kurok1.datasource.autconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 数据库配置,一写多读
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.20
 */
@ConfigurationProperties(prefix = "database.config")
public class DatabaseProperties {

    private boolean enabled = false;

    private String driverClassName;

    private List<ReadOnlyProperties> readOnly;

    private WriteableProperties writeable;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public List<ReadOnlyProperties> getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(List<ReadOnlyProperties> readOnly) {
        this.readOnly = readOnly;
    }

    public WriteableProperties getWriteable() {
        return writeable;
    }

    public void setWriteable(WriteableProperties writeable) {
        this.writeable = writeable;
    }

    /**
     * 只读的数据库
     */
    static class ReadOnlyProperties {
        private String jdbcUrl;
        private String username;
        private String password;
        private int maxPoolSize = 10;

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }
    }

    static class WriteableProperties {
        private String jdbcUrl;
        private String username;
        private String password;
        private int maxPoolSize = 10;

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }
    }

}
