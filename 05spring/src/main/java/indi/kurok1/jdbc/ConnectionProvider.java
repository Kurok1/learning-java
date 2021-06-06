package indi.kurok1.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接提供
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
public class ConnectionProvider extends HikariDataSource {

    private final HikariConfig config;

    public ConnectionProvider() {
        config = null;//不使用连接池
    }

    public ConnectionProvider(HikariConfig configuration) {
        super(configuration);//使用连接池
        this.config = configuration;

    }

    public Connection getConnection(String url, String username, String password) throws SQLException {
        if (this.config != null)
            throw new UnsupportedOperationException();

        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.config == null)
            throw new UnsupportedOperationException();
        else return super.getConnection();
    }
}
