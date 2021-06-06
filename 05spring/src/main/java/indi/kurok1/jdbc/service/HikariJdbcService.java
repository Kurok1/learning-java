package indi.kurok1.jdbc.service;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 使用连接池重现JDBC
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 * @see JdbcService
 */
@Service
public class HikariJdbcService extends JdbcService {

    public HikariJdbcService() {
        super(true);
    }


    @Override
    protected Connection getConnection() throws SQLException {
        return super.provider.getConnection();
    }
}
