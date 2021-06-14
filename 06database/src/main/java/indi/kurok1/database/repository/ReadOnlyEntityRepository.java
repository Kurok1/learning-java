package indi.kurok1.database.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 只读抽象类
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
public abstract class ReadOnlyEntityRepository<E, I> extends AbstractEntityRepository<E, I> {

    public E getById(I id) {
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ")
                .append(getTableName())
                .append(" WHERE ").append(idColumn).append(" = ?");
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            return getResultSetExtractor().extractOne(resultSet, super.fieldMetaData, getEntityClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public E getByCondition(Map<String, Object> conditions) {
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ")
                .append(getTableName())
                .append(" WHERE 1=1 ");
        final List<Object> args = new ArrayList<>();
        conditions.forEach(
                (key, value) -> {
                    sql.append("AND ").append(key).append(" = ? ");
                    args.add(value);
                }
        );
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < conditions.size(); i++)
                statement.setObject(i + 1, args.get(i));
            ResultSet resultSet = statement.executeQuery();
            return getResultSetExtractor().extractOne(resultSet, super.fieldMetaData, getEntityClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<E> findAll() {
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ")
                .append(getTableName());
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            ResultSet resultSet = statement.executeQuery();
            return getResultSetExtractor().extractAll(resultSet, super.fieldMetaData, getEntityClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<E> findByConditions(Map<String, Object> conditions) {
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ")
                .append(getTableName())
                .append(" WHERE 1=1 ");
        final List<Object> args = new ArrayList<>();
        conditions.forEach(
                (key, value) -> {
                    sql.append("AND ").append(key).append(" = ? ");
                    args.add(value);
                }
        );
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < conditions.size(); i++)
                statement.setObject(i + 1, args.get(i));
            ResultSet resultSet = statement.executeQuery();
            return getResultSetExtractor().extractAll(resultSet, super.fieldMetaData, getEntityClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
