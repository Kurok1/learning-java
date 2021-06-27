package indi.kurok1.sharding.xa;

import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.27
 */
@Service
public class OrderService {

    private final JdbcTemplate jdbcTemplate;

    public OrderService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void process() {
        //设置事务级别为XA
        TransactionTypeHolder.set(TransactionType.XA);
        this.jdbcTemplate.execute(this::batchInsertAndPrint);
        List<Map<String, Object>> data = jdbcTemplate.queryForList("SELECT * FROM order_header");
        if (data.isEmpty()) {
            System.out.println("order is empty");
            return;
        }
        for (Map<String, Object> each : data) {
            System.out.println(each.toString());
        }
    }

    private Object[] insertData = {"order11","shipTo","shipToAddress","123456", LocalDateTime.now(),"api",1,1, BigDecimal.ONE};

    /**
     * 批量插入数据到多个数据库，然后全部查询出来
     * @return
     */
    public Boolean batchInsertAndPrint(Connection connection) {

        final String header_sql = "insert into order_header(`code`, `shipTo`, `shipToAddress`, `shipToPhone`, `created`, `createdBy`, `totalQty`, `totalLines`, `totalPrice`) values(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(header_sql)) {
            connection.setAutoCommit(false);
            for (int i = 0; i < 5 ; i++) {
                preparedStatement.setObject(1, insertData[0]);
                preparedStatement.setObject(2, insertData[1]);
                preparedStatement.setObject(3, insertData[2]);
                preparedStatement.setObject(4, insertData[3]);
                preparedStatement.setObject(5, insertData[4]);
                preparedStatement.setObject(6, insertData[5]);
                preparedStatement.setObject(7, insertData[6]);
                preparedStatement.setObject(8, insertData[7]);
                preparedStatement.setObject(9, insertData[7]);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return false;
        }

    }
}
