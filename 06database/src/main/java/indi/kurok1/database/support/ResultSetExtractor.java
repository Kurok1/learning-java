package indi.kurok1.database.support;

import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 结果集合提取
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@Component
public class ResultSetExtractor{

    private <E> E read(ResultSet resultSet, List<ColumnMetaData> metaDataSet, E target) throws SQLException, InvocationTargetException, IllegalAccessException {
        for (ColumnMetaData metaData : metaDataSet) {
            Method setter = metaData.getSetter();
            Converter converter = metaData.getConverter();
            if (converter != null) {
                //需要转换结果
                String result = resultSet.getString(metaData.getColumnName());
                if (converter.isSupport(metaData.getPropertyType())) {
                    Object convertedResult = converter.convert(result, metaData.getPropertyType());
                    setter.invoke(target, convertedResult);
                } else {
                    setter.invoke(target, result);
                }
            } else {
                Object result = resultSet.getObject(metaData.getColumnName());
                setter.invoke(target, result);
            }
        }
        return target;
    }

    public <E> E extractOne(ResultSet resultSet, List<ColumnMetaData> metaDataSet, Class<E> entityClass) {
        try {
            E target = entityClass.newInstance();
            if (!resultSet.next())
                return null;

            return read(resultSet, metaDataSet, target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <E> List<E> extractAll(ResultSet resultSet, List<ColumnMetaData> metaDataSet, Class<E> entityClass) {
        List<E> list = new LinkedList<>();
        try {
            while (resultSet.next()) {
                list.add(read(resultSet, metaDataSet, entityClass.newInstance()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
