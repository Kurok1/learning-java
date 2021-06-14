package indi.kurok1.database.repository;

import indi.kurok1.database.support.ColumnMetaData;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 抽象实现，对实体提供CRUD操作
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
public abstract class CrudEntityRepository<E, I> extends ReadOnlyEntityRepository<E, I> {

    public E insert(E target) {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(super.insertSql, Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            for (ColumnMetaData metaData : fieldMetaData) {
                if (idColumn.equals(metaData.getColumnName()))
                    continue;
                Method getter = metaData.getGetter();
                Object field = getter.invoke(target);
                statement.setObject(index, field);
                index++;
            }
            int update = statement.executeUpdate();
            if (update > 0) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) { //插入成功才返回
                    Object id = idMetaData.getConverter().convert(rs.getObject(1).toString(), idMetaData.getPropertyType());
                    idMetaData.getSetter().invoke(target, id);
                    return target;
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public E update(E target) {

        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(super.updateSql)) {
            int index = 1;
            for (ColumnMetaData metaData : fieldMetaData) {
                if (idColumn.equals(metaData.getColumnName()))
                    continue;
                Method getter = metaData.getGetter();
                Object field = getter.invoke(target);
                statement.setObject(index, field);
                index++;
            }
            statement.setObject(index, idMetaData.getGetter().invoke(target));
            int update = statement.executeUpdate();
            if (update > 0) {
                //更新成功才返回
                return target;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(I id) {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(super.deleteSql)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
