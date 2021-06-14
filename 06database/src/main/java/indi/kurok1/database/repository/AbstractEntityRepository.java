package indi.kurok1.database.repository;

import indi.kurok1.database.annoation.Column;
import indi.kurok1.database.annoation.ConvertParam;
import indi.kurok1.database.annoation.Id;
import indi.kurok1.database.annoation.Table;
import indi.kurok1.database.support.ColumnMetaData;
import indi.kurok1.database.support.Converter;
import indi.kurok1.database.support.ResultSetExtractor;
import indi.kurok1.database.support.convert.StringConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象实体服务,几乎不提供任何操作
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
public abstract class AbstractEntityRepository<E, I> implements BeanFactoryAware, InitializingBean {


    private BeanFactory beanFactory;
    /**
     * 手动注入数据源
     */
    private DataSource dataSource;
    private ResultSetExtractor resultSetExtractor;

    private static final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    protected abstract Class<E> getEntityClass();
    protected abstract Class<I> getIdClass();

    protected String idColumn = "id";
    private String tableName = "";
    protected List<ColumnMetaData> fieldMetaData = null;
    protected ColumnMetaData idMetaData = null;

    //生成一些默认sql
    protected String insertSql = null;
    protected String deleteSql = null;
    protected String updateSql = null;

    /**
     * 根据已知，获取表名称
     * @return 表名称
     */
    protected String getTableName() {
        if (StringUtils.hasLength(tableName))
            return tableName;
        Class<E> entityClass = getEntityClass();
        if (entityClass.isAnnotationPresent(Table.class)) {
            tableName = entityClass.getDeclaredAnnotation(Table.class).name();
        } else tableName = entityClass.getSimpleName();
        return tableName;
    }

    @Override
    public void setBeanFactory(org.springframework.beans.factory.BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.dataSource = beanFactory.getBean(DataSource.class);
        this.resultSetExtractor = beanFactory.getBean(ResultSetExtractor.class);
        //读取id名称
        readColumn();
        //生成base的sql
        generateInsertSql();
        generateUpdateSql();
        generateDeleteSql();
    }

    protected final BeanFactory getBeanFactory() {
        return beanFactory;
    }

    protected final DataSource getDataSource() {
        return dataSource;
    }

    public final ResultSetExtractor getResultSetExtractor() { return resultSetExtractor; }

    /**
     * 获取当前线程的connection,同一线程重复利用
     * @return connection
     */
    protected final Connection getConnection() throws RuntimeException {
        try {
            if (connectionThreadLocal.get() == null) {
                //从数据源取
                connectionThreadLocal.set(dataSource.getConnection());
            }
            return connectionThreadLocal.get();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    /**
     * 读取列属性
     */
    protected void readColumn() {
        Class<E> entityClass = getEntityClass();
        Field[] fields = entityClass.getDeclaredFields();
        if (fields.length == 0)
            throw new RuntimeException("no field found");

        this.fieldMetaData = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                //判断下类型
                if (field.getType() != getIdClass())
                    throw new RuntimeException("the id type is error");
                idColumn = field.getName();
                ColumnMetaData metaData = buildFieldMetaData(field, entityClass);
                fieldMetaData.add(metaData);
                idMetaData = metaData;
            } else {
                fieldMetaData.add(buildFieldMetaData(field, entityClass));
            }
        }

        //检查id是否存在
        try {
            entityClass.getDeclaredField(idColumn);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("id field not found");
        }
    }

    private ColumnMetaData buildFieldMetaData(Field field, Class<E> clazz) {
        String propertyName = field.getName();
        ColumnMetaData metaData = null;
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getDeclaredAnnotation(Column.class);
            Class<? extends Converter> converter = column.converter();
            ConvertParam[] convertParams = column.convertParams();
            metaData = new ColumnMetaData(convertParams, converter);
            metaData.setProperty(propertyName);
            if (StringUtils.hasLength(column.name()))
                metaData.setColumnName(column.name());
            else metaData.setColumnName(propertyName);
            metaData.setPropertyType(field.getType());
        } else {
            metaData = new ColumnMetaData(null, StringConverter.class);
            metaData.setProperty(propertyName);
            metaData.setColumnName(propertyName);
            metaData.setPropertyType(field.getType());
        }
        //setter,getter...
        try {
            if (propertyName.length() == 1) {
                String setMethod = String.format("set%s", propertyName.toUpperCase());
                String getMethod = String.format("get%s", propertyName.toUpperCase());
                metaData.setSetter(clazz.getDeclaredMethod(setMethod, field.getType()));
                metaData.setGetter(clazz.getDeclaredMethod(getMethod));
            } else {
                String setMethod = String.format("set%c%s", propertyName.toUpperCase().charAt(0), propertyName.substring(1));
                String getMethod = String.format("get%c%s", propertyName.toUpperCase().charAt(0), propertyName.substring(1));
                metaData.setSetter(clazz.getDeclaredMethod(setMethod, field.getType()));
                metaData.setGetter(clazz.getDeclaredMethod(getMethod));
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }


        return metaData;
    }

    protected void generateInsertSql() {
        final StringBuffer values = new StringBuffer();
        values.append("(");
        final StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ").append(getTableName()).append("(");
        for (ColumnMetaData metaData : fieldMetaData) {
            if (idColumn.equals(metaData.getColumnName()))
                continue;
            sql.append(String.format("`%s`,", metaData.getColumnName()));
            values.append("?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        values.deleteCharAt(values.length() - 1);
        sql.append(") values ");
        values.append(")");
        sql.append(values);

        this.insertSql = sql.toString();
    }

    protected void generateUpdateSql() {
        final StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ").append(getTableName()).append(" SET ");
        for (ColumnMetaData metaData : fieldMetaData) {
            if (idColumn.equals(metaData.getColumnName()))
                continue;
            sql.append(String.format("`%s` = ?,", metaData.getColumnName()));
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE ").append(idColumn).append(" = ? ");

        this.updateSql = sql.toString();
    }

    protected void generateDeleteSql() {
        this.deleteSql = String.format("DELETE FROM %s WHERE %s = ?", getTableName(), idColumn);
    }

    public static void release() {
        try {
            connectionThreadLocal.get().close();
            connectionThreadLocal.set(null);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
