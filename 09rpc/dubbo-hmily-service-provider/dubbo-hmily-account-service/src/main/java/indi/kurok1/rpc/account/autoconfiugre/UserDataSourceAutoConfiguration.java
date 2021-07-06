package indi.kurok1.rpc.account.autoconfiugre;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源定义
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.06
 */
@Configuration
public class UserDataSourceAutoConfiguration {


    @Bean
    public DataSource dataSource() {
        DataSourceContainer container = new DataSourceContainer();
        Map<Object, Object> map = new HashMap<>();
        DataSource defaultDataSource = dataSource1();
        map.put("db1", defaultDataSource);
        map.put("db2", dataSource2());
        container.setDefaultTargetDataSource(defaultDataSource);
        container.setTargetDataSources(map);
        container.addKey("db1");
        container.addKey("db2");

        return container;
    }

    private DataSource dataSource1() {
        final HikariConfig config = new HikariConfig();
        config.setPoolName("db1");
        config.setUsername("root");
        config.setPassword("123456");
        config.setJdbcUrl("jdbc:mysql://47.107.166.121:3316/order_0?serverTimezone=GMT%2B8");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        return new HikariDataSource(config);
    }

    private DataSource dataSource2() {
        final HikariConfig config = new HikariConfig();
        config.setPoolName("db2");
        config.setUsername("root");
        config.setPassword("123456");
        config.setJdbcUrl("jdbc:mysql://47.107.166.121:3316/order_1?serverTimezone=GMT%2B8");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        return new HikariDataSource(config);
    }
}
