package indi.kurok1.insert;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.20
 */
public class JdbcService {

    private String url = "jdbc:mysql://localhost:3306/test_jdbc?serverTimezone=GMT%2B8";
    private String username = "root";
    private String password = "123qwertyA";

    private static final String ORDER_PREFIX = "ORDER-";

    private volatile int index = 1;

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return DriverManager.getConnection(url, username, password);
    }

    //private int insertRows = 100 * 10000;//100万行订单数据
    private int insertRows = 1000000;//100万行订单数据

    private String getNextOrderCode() {
        return String.format("%s%d", ORDER_PREFIX, index++);
    }

    //订单数据
    private String shipTo = "shipTo";
    private String shipToAddress = "shipToAddress";
    private String shipToPhone = "123456";
    private LocalDateTime created = LocalDateTime.now();
    private String createdBy = "api";
    private Integer totalQty = 1;
    private Integer totalLines = 1;
    private BigDecimal totalPrice = BigDecimal.ONE;

    public long doInsert() {
        try (
            Connection connection = getConnection()
        ) {
            //写头
            final String header_sql = "insert into order_header(`code`, `shipTo`, `shipToAddress`, `shipToPhone`, `created`, `createdBy`, `totalQty`, `totalLines`, `totalPrice`) values(?,?,?,?,?,?,?,?,?)";
            connection.setAutoCommit(false);
            long begin = System.currentTimeMillis();
            for (int i = 0 ; i < insertRows; i++) {
                PreparedStatement statement = connection.prepareStatement(header_sql);
                statement.setString(1, getNextOrderCode());
                statement.setString(2, shipTo);
                statement.setString(3, shipToAddress);
                statement.setString(4, shipToPhone);
                statement.setObject(5, created);
                statement.setString(6, createdBy);
                statement.setInt(7, totalQty);
                statement.setInt(8, totalLines);
                statement.setBigDecimal(9, totalPrice);
                statement.executeUpdate();
                statement.close();
            }
            connection.commit();
            return begin;

        } catch (Exception t) {
            t.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    public long doAddBatch() {
        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {
            //写头
            final String header_sql = "insert into order_header(`code`, `shipTo`, `shipToAddress`, `shipToPhone`, `created`, `createdBy`, `totalQty`, `totalLines`, `totalPrice`) ";

            connection.setAutoCommit(false);
            long executeTime = System.currentTimeMillis();
            for (int i = 0 ; i < insertRows; i++) {
                StringBuffer sql = new StringBuffer();
                sql.append(header_sql);
                sql.append(" values ('")
                        .append(getNextOrderCode()).append("','")
                        .append(shipTo).append("','")
                        .append(shipToAddress).append("','")
                        .append(shipToPhone).append("',")
                        .append("'2020-06-18 18:00:00'").append(",'")
                        .append(createdBy).append("',")
                        .append(totalQty.toString()).append(",")
                        .append(totalLines.toString()).append(",")
                        .append(totalPrice.toString()).append(");");
                statement.addBatch(sql.toString());
            }
            System.out.printf("prepare data use %d ms", System.currentTimeMillis() - executeTime);
            executeTime = System.currentTimeMillis();
            statement.executeBatch();
            connection.commit();
            return executeTime;
        } catch (Exception t) {
            t.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    public long doAddBatchByStep(int step) {
        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {
            //写头
            final String header_sql = "insert into order_header(`code`, `shipTo`, `shipToAddress`, `shipToPhone`, `created`, `createdBy`, `totalQty`, `totalLines`, `totalPrice`) ";

            connection.setAutoCommit(false);
            long executeTime = System.currentTimeMillis();
            for (int i = 0 ; i < insertRows; i = i + step) {
                StringBuffer sql = new StringBuffer();
                sql.append(header_sql);
                sql.append(" values ");
                for (int j = 0 ; j < step ; j++) {
                    sql.append(" ('")
                            .append(getNextOrderCode()).append("','")
                            .append(shipTo).append("','")
                            .append(shipToAddress).append("','")
                            .append(shipToPhone).append("',")
                            .append("'2020-06-18 18:00:00'").append(",'")
                            .append(createdBy).append("',")
                            .append(totalQty.toString()).append(",")
                            .append(totalLines.toString()).append(",")
                            .append(totalPrice.toString()).append("),");

                }
                sql.deleteCharAt(sql.length() - 1);
                statement.addBatch(sql.toString());
            }
            System.out.printf("prepare data use %d ms\n", System.currentTimeMillis() - executeTime);
            executeTime = System.currentTimeMillis();
            statement.executeBatch();
            connection.commit();
            return executeTime;
        } catch (Exception t) {
            t.printStackTrace();
            return System.currentTimeMillis();
        }
    }

}
