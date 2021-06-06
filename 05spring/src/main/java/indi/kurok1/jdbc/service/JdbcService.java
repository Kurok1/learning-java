package indi.kurok1.jdbc.service;

import com.zaxxer.hikari.HikariConfig;
import indi.kurok1.jdbc.ConnectionProvider;
import indi.kurok1.jdbc.Student;
import indi.kurok1.jdbc.annoation.MyCache;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 增删改查操作（非连接池）
 * CREATE TABLE `test_jdbc`.`Untitled`  (
 *   `id` int(11) NOT NULL,
 *   `name` varchar(50) NULL,
 *   `age` int(2) NOT NULL DEFAULT 0,
 *   PRIMARY KEY (`id`)
 * );
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@Service
public class JdbcService {

    protected final String tableName = "student";
    protected final String url = "jdbc:mysql://localhost:3306/test_jdbc?serverTimezone=GMT%2B8";
    protected final String username = "root";
    protected final String password = "123qwertyA";

    protected final ConnectionProvider provider;

    public JdbcService() {
        this(false);
    }

    public JdbcService(boolean usePool) {
        if (!usePool)
            this.provider = new ConnectionProvider();
        else this.provider = new ConnectionProvider(buildHikariConfig());
    }

    private HikariConfig buildHikariConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(this.url);
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return config;
    }

    public Student insert(Student student) {
        return insert(student, false);
    }

    protected Connection getConnection() throws SQLException {
        return provider.getConnection(url, username, password);
    }

    /**
     * 插入数据
     *
     * @param student 插入数据
     * @return 成功返回实体，否则返回null
     */
    public Student insert(Student student, boolean beginTransaction) {
        if (student == null)
            throw new IllegalArgumentException("student need not null");


        Connection connection = null;
        Savepoint savepoint = null;
        try {
            connection = getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        final String sql = "insert into student(`name`,`age`) values(?,?)";
        if (beginTransaction) {
            try {
                connection.setAutoCommit(false);
                savepoint = connection.setSavepoint();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }
        }
        try {
            final PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, student.getName());
            statement.setInt(2, student.getAge());
            int updated = statement.executeUpdate();
            if (updated > 0) {//插入数据成功
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next())
                    student.setId(rs.getLong(1));
                if (savepoint != null)
                    connection.commit();
                return student;
            }
            if (savepoint != null)
                connection.rollback(savepoint);
            return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (savepoint != null) {
                try {
                    connection.rollback(savepoint);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    public int delete(long id) { return delete(id, false); }

    /**
     * 根据id删除记录
     * @param id id
     * @return 返回影响行数，永远判断是否成功
     */
    public int delete(long id, boolean beginTransaction) {
        Connection connection = null;
        Savepoint savepoint = null;
        try {
            connection = getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
        final String sql = "delete from student where id = ?";
        if (beginTransaction) {
            try {
                connection.setAutoCommit(false);
                savepoint = connection.setSavepoint();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return 0;
            }
        }
        try {
            final PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            int updated = statement.executeUpdate();
            if (updated > 0) {//插入数据成功
                if (savepoint != null)
                    connection.commit();
                return updated;
            }
            if (savepoint != null)
                connection.rollback(savepoint);
            return 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (savepoint != null) {
                try {
                    connection.rollback(savepoint);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }
    }

    public int update(Student student) {
        return update(student, false);
    }

    /**
     * 更新操作
     * @param student 根据id更新
     * @param beginTransaction 是否开启事务
     * @return 返回影响行数
     */
    public int update(Student student, boolean beginTransaction) {
        if (student == null || student.getId() < 0)
            throw new IllegalArgumentException("student is illegal");

        Connection connection = null;
        Savepoint savepoint = null;
        try {
            connection = getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
        final String sql = "update student set `name` = ?,`age` = ? where id = ?";
        if (beginTransaction) {
            try {
                connection.setAutoCommit(false);
                savepoint = connection.setSavepoint();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return 0;
            }
        }
        try {
            final PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, student.getName());
            statement.setInt(2, student.getAge());
            statement.setLong(3, student.getId());
            int updated = statement.executeUpdate();
            if (updated > 0) {//插入数据成功
                if (savepoint != null)
                    connection.commit();
                return updated;
            }
            if (savepoint != null)
                connection.rollback(savepoint);
            return 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (savepoint != null) {
                try {
                    connection.rollback(savepoint);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }
    }

    /**
     * 查找所有数据
     * @return 返回数据集合,non-null
     */
    @MyCache
    public List<Student> findAll() {
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Collections.emptyList();
        }
        final String sql = "select id,name,age from student";

        try {
            final PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<Student> students = new LinkedList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                Integer age = resultSet.getInt("age");
                Long id = resultSet.getLong("id");
                Student student = Student.of(name, age);
                student.setId(id);
                students.add(student);
            }
            return students;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 批量更新,默认开启事务
     * @param students 需要更新的数据
     * @return 影响行数集合
     */
    public int[] batchUpdate(Collection<Student> students) {
        if (students == null || students.size() == 0)
            throw new IllegalArgumentException("students is illegal");

        Connection connection = null;
        Savepoint savepoint = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            final String sql = "update student set `name` = ?,`age` = ? where id = ?";
            final PreparedStatement statement = connection.prepareStatement(sql);
            for (Student student : students) {
                statement.setString(1, student.getName());
                statement.setInt(2, student.getAge());
                statement.setLong(3, student.getId());
                statement.addBatch();
            }


            int[] result = statement.executeBatch();
            connection.commit();
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (savepoint != null) {
                try {
                    connection.rollback(savepoint);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return new int[students.size()];
        }
    }

}
