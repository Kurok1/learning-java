## 设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到 Github。

引入`sharding-jdbc-spring-boot-starter` [pom.xml](./08sharding-crud/pom.xml)
```xml
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
    <version>${sharding.starter.version}</version>
</dependency>
```
### 相关配置
#### 分库分表信息
```yml
mysql:  # 数据库配置
    databases: 2
    tables-each-database: 4
    slave0-order-0:
        host: 47.107.166.121
        port: 3316
        database: order_0
        username: "root"
        password: "123456"
    slave0-order-1:
        host: 47.107.166.121
        port: 3316
        database: order_1
        username: "root"
        password: "123456"
    slave1-order-0:
        host: 47.107.166.121
        port: 3326
        database: order_0
        username: "root"
        password: "123456"
    slave1-order-1:
        host: 47.107.166.121
        port: 3326
        database: order_1
        username: "root"
        password: "123456"

spring:
    shardingsphere:
        datasource:
            names: db0-slave0,db0-slave1,db1-slave0,db1-slave1
            # 配置分库信息
            db0-slave0: # 配置第一个从库
                type: com.zaxxer.hikari.HikariDataSource
                driverClassName: com.mysql.cj.jdbc.Driver
                jdbcUrl: jdbc:mysql://${mysql.slave0-order-0.host}:${mysql.slave0-order-0.port}/${mysql.slave0-order-0.database}?serverTimezone=GMT%2B8
                username: ${mysql.slave0-order-0.username}
                password: ${mysql.slave0-order-0.password}
            db0-slave1: # 配置第一个从库
                type: com.zaxxer.hikari.HikariDataSource
                driverClassName: com.mysql.cj.jdbc.Driver
                jdbcUrl: jdbc:mysql://${mysql.slave0-order-1.host}:${mysql.slave0-order-1.port}/${mysql.slave0-order-1.database}?serverTimezone=GMT%2B8
                username: ${mysql.slave0-order-1.username}
                password: ${mysql.slave0-order-1.password}
            db1-slave0: # 配置第三个从库
                type: com.zaxxer.hikari.HikariDataSource
                driverClassName: com.mysql.cj.jdbc.Driver
                jdbcUrl: jdbc:mysql://${mysql.slave1-order-0.host}:${mysql.slave1-order-0.port}/${mysql.slave1-order-0.database}?serverTimezone=GMT%2B8
                username: ${mysql.slave1-order-0.username}
                password: ${mysql.slave1-order-0.password}
            db1-slave1: # 配置第四一个从库
                type: com.zaxxer.hikari.HikariDataSource
                driverClassName: com.mysql.cj.jdbc.Driver
                jdbcUrl: jdbc:mysql://${mysql.slave1-order-1.host}:${mysql.slave1-order-1.port}/${mysql.slave1-order-1.database}?serverTimezone=GMT%2B8
                username: ${mysql.slave1-order-1.username}
                password: ${mysql.slave1-order-1.password}
```
一共2个数据库实例，每个数据库实例拥有两个数据库，每个数据库拥有4张表

#### 分表策略
```yaml
spring:
    shardingsphere:
        sharding:
            tables:
                order_header:
                    table-strategy:
                        inline:
                            sharding-column: "id"
                            algorithm-expression: "order_header_$->{id % 4}"
                    database-strategy:
                        inline:
                            sharding-column: "id"
                            algorithm-expression: "db$->{id % 2}-slave$->{id % 2}"
                    actual-data-nodes: db$->{0..1}-slave$->{0..1}.order_header_$->{0..3}
                    key-generator:
                        type: redis_auto_increment
                        column: "id"
            binding-tables: "order_header"
```
绑定`order_header`作为逻辑表，同时自定义id生成器 [RedisIdGenerator](./08sharding-crud/src/main/java/indi/kurok1/sharding/autoconfigure/RedisIdGenerator.java)
```java
public class RedisIdGenerator implements ShardingKeyGenerator {
    private static StringRedisTemplate redisTemplate = null;
    public static void setRedisTemplate(StringRedisTemplate redisTemplate);

    @Override
    public Comparable<?> generateKey() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.increment("id");
    }
}
```
采用全局自增的方式获取id. 同时注意，当前版本(`4.1.1`)不支持`Spring Bean`的方式注入，只能通过`SPI`方式注入

### 业务处理
[`OrderService`](./08sharding-crud/src/main/java/indi/kurok1/sharding/service/OrderService.java)


## 基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一），提交到 Github
选用ShardingSphere 的 Atomikos。

依赖引入 [pom.xml](./08sharding-xa/pom.xml)
```xml
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>shardingsphere-jdbc-core-spring-boot-starter</artifactId>
    <version>${shardingsphere.core.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>shardingsphere-transaction-xa-core</artifactId>
    <version>${shardingsphere.core.version}</version>
</dependency>
```

### DEMO实现
[OrderService](./08sharding-xa/src/main/java/indi/kurok1/sharding/xa/OrderService.java)
```java
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
```