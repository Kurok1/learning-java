package indi.kurok1.sharding.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
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

    private Object[] insertData = {"order11","shipTo","shipToAddress","123456",LocalDateTime.now(),"api",1,1,BigDecimal.ONE};

    public int insert() {

        final String header_sql = "insert into order_header(`code`, `shipTo`, `shipToAddress`, `shipToPhone`, `created`, `createdBy`, `totalQty`, `totalLines`, `totalPrice`) values(?,?,?,?,?,?,?,?,?)";
        return this.jdbcTemplate.update(header_sql, insertData);
    }

    public List<Map<String, Object>> findById(long id) {
        return this.jdbcTemplate.queryForList("select * from order_header where id = ?", id);
    }

    public int update(Map<String, Object> updateValues, long id) {
        if (updateValues.isEmpty())
            return 0;
        StringBuffer sql = new StringBuffer();
        List<Object> objects = new LinkedList<>();
        sql.append("update order_header set ");
        updateValues.forEach(
                (key, value)->{
                    sql.append(key).append(" = ?,");
                    objects.add(value);
                }
        );
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE id = ?");
        objects.add(id);
        return this.jdbcTemplate.update(sql.toString(), objects.toArray());
    }

    public void delete(long id) {
        this.jdbcTemplate.update("delete from order_header where id = ?", id);
    }
}
