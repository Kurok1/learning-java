## 按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率

### 设计1
单一事务+for循环批量插入
```java
try (
            Connection connection = getConnection()
        ){
        //写头
        final String header_sql="insert into order_header(`code`, `shipTo`, `shipToAddress`, `shipToPhone`, `created`, `createdBy`, `totalQty`, `totalLines`, `totalPrice`) values(?,?,?,?,?,?,?,?,?)";
        connection.setAutoCommit(false);
        for(int i=0;i<insertRows; i++){
        PreparedStatement statement=connection.prepareStatement(header_sql);
        statement.setString(1,getNextOrderCode());
        statement.setString(2,shipTo);
        statement.setString(3,shipToAddress);
        statement.setString(4,shipToPhone);
        statement.setObject(5,created);
        statement.setString(6,createdBy);
        statement.setInt(7,totalQty);
        statement.setInt(8,totalLines);
        statement.setBigDecimal(9,totalPrice);
        statement.executeUpdate();
        statement.close();
        }
        connection.commit();
}
```

### 设计2
使用`addBatch`
```java
    final String header_sql="insert into order_header(`code`, `shipTo`, `shipToAddress`, `shipToPhone`, `created`, `createdBy`, `totalQty`, `totalLines`, `totalPrice`) ";

        connection.setAutoCommit(false);
        long executeTime=System.currentTimeMillis();
        for(int i=0;i<insertRows; i++){
            StringBuffer sql=new StringBuffer();
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
        
        statement.executeBatch();
        connection.commit();
```

### 设计3
使用`addBatch`+`batchInsert`,配合步长
```java
        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {
            //写头
            final String header_sql = "insert into order_header(`code`, `shipTo`, `shipToAddress`, `shipToPhone`, `created`, `createdBy`, `totalQty`, `totalLines`, `totalPrice`) ";

            connection.setAutoCommit(false);
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
            statement.executeBatch();
            connection.commit();
        } catch (Exception t) {
            t.printStackTrace();
            return System.currentTimeMillis();
        }
```
测试结果

|设计方案|执行时长|
|-------|--------|
|设计1  |153540ms|
|设计2  |148048ms|
|设计3(500步长)  |19362ms |


## 读写分离 - 动态切换数据源版本 1.0
1. 数据源配置， [DatabaseProperties](./src/main/java/indi/kurok1/datasource/autconfigure/DatabaseProperties.java)
动态数据源配置文件，一写多读配置

2. 实现`AbstractRoutingDataSource`, [DataSourceContainer](./src/main/java/indi/kurok1/datasource/autconfigure/DataSourceContainer.java)

3. 重写`protected Object determineCurrentLookupKey()`,读取HTTP HEADER，判断当前请求是否是只读请求
```java
HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
if(request==null||request.getHeader(READ_ONLY_HEADER)==null)
        return MASTER_KEY;
return choose(readOnlyKeys);
```
4. 配置`JdbcTemplate`
```java
@Bean
@Primary
public JdbcTemplate jdbcTemplate(DataSourceContainer dataSourceContainer) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    jdbcTemplate.setDataSource(dataSourceContainer);
    return jdbcTemplate;
}
```

## 读写分离 - 数据库框架版本 2.0
引入`sharding-jdbc-spring-boot-starter`
```xml
<dependency>
    <groupId>io.shardingsphere</groupId>
    <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
    <version>${sharding.version}</version>
</dependency>
```
编写配置文件
```yml
sharding:
    jdbc:
        dataSource:
            names: db-master,db-slave0,db-slave1
            # 配置主库
            db-master: #org.apache.tomcat.jdbc.pool.DataSource
                type: com.zaxxer.hikari.HikariDataSource
                driverClassName: com.mysql.cj.jdbc.Driver
                jdbcUrl: jdbc:mysql://localhost:3306/test_jdbc?serverTimezone=GMT%2B8
                username: root
                password: '123qwertyA'
            db-slave0: # 配置第一个从库
                type: com.zaxxer.hikari.HikariDataSource
                driverClassName: com.mysql.cj.jdbc.Driver
                jdbcUrl: jdbc:mysql://localhost:3316/test_jdbc?serverTimezone=GMT%2B8
                username: root
                password: '123qwertyA'
            db-slave1: # 配置第二个从库
                type: com.zaxxer.hikari.HikariDataSource
                driverClassName: com.mysql.cj.jdbc.Driver
                jdbcUrl: jdbc:mysql://localhost:3326/test_jdbc?serverTimezone=GMT%2B8
                username: root
                password: '123qwertyA'
        config:
            masterslave: # 配置读写分离
                load-balance-algorithm-type: round_robin # 配置从库选择策略，这里选择用轮询//random 随机 //round_robin 轮询
                name: master-slave
                master-data-source-name: db-master
                slave-data-source-names: db-slave0,db-slave1
```