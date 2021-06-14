## 基于电商交易场景（用户、商品、订单），设计一套简单的表结构，提交 DDL 的 SQL 文件到 Github（后面 2 周的作业依然要是用到这个表结构）。
[sql文件位置](./src/main/resources/db/migration/)

表结构设计
```sql
-- 创建item货品表
CREATE TABLE `item`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT,
    `code`  varchar(50) NOT NULL COMMENT '货品编码',
    `name`  varchar(50) NULL COMMENT '货品名称',
    `price` decimal(10, 2) NULL COMMENT '单价',
    PRIMARY KEY (`id`),
    INDEX `code_index`(`code`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建用户表

CREATE TABLE `user`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT,
    `code`  varchar(50) NOT NULL COMMENT '用户编码',
    `name`  varchar(50) NULL COMMENT '用户名称',
    `phone`  varchar(50) NULL COMMENT '手机号码',
    `email`  varchar(50) NULL COMMENT '邮箱',
    PRIMARY KEY (`id`),
    INDEX `code_index`(`code`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- 创建订单头
CREATE TABLE `order_header`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT,
    `code`  varchar(50) NOT NULL COMMENT '订单编码',
    `shipTo`  varchar(50) NOT NULL COMMENT '收货人',
    `shipToAddress`  varchar(50) NOT NULL COMMENT '收货地址',
    `shipToPhone`  varchar(50) NOT NULL COMMENT '收货电话',
    `created`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
    `createdBy` varchar(50) NOT NULL DEFAULT "api" COMMENT '创建人，默认接口创建',
    `totalQty` int(11) NOT NULL default 0,
    `totalLines` int(11) NOT NULL default 0 COMMENT '明细行数量',
    `totalPrice` decimal(10, 2) NOT NULL default 0 COMMENT '总金额',
    PRIMARY KEY (`id`),
    INDEX `code_index`(`code`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 创建订单明细
CREATE TABLE `order_detail`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT,
    `orderId`  int(11) NOT NULL COMMENT '订单头id',
    `orderCode` varchar(50) NOT NULL COMMENT '订单编码',
    `created`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
    `createdBy` varchar(50) NOT NULL DEFAULT "api" COMMENT '创建人，默认接口创建',
    `itemCode` varchar(50) NOT NULL COMMENT '货品编码',
    `itemName` varchar(50) NOT NULL COMMENT '货品名称',
    `requestQty` int(11) NOT NULL COMMENT '请求数量',
    `shipQty` int(11) NOT NULL DEFAULT 0 COMMENT '实际发货数量',
    `totalPrice` decimal(10, 2) NOT NULL default 0 COMMENT '总金额',
    PRIMARY KEY (`id`),
    INDEX `orderId_index`(`orderId`) USING BTREE,
    INDEX `orderCode_index`(`orderCode`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## 对 MySQL 配置不同的数据库连接池（DBCP、C3P0、Druid、Hikari），测试增删改查 100 万次，对比性能，生成报告。

### 测试流程
创建订单
1. 检查数据库同是否存在相同单号的订单
    
    1.1 存在，删除原订单
2. 创建订单明细（orderId=0）
3. 创建订单头
4. 查找orderId=0，单号=orderCode的订单，订单明细写入orderId

[业务实现位置](./src/main/java/indi/kurok1/database/service/OrderService.java)

压测环境说明
1. JVM参数 `-Xmx8g -Xms8g`,其余参数均为JDK8默认
2. 线程数 40个，每个线程循环发送25000个订单
3. 测试报文
```json
{
    "header": {
        "code": "${orderCode}",
        "shipTo": "unkown",
        "shipToAddress": "unkown",
        "shipToPhone": "111111",
        "createdBy": "api"
    },
    "details": [
        {
            "itemCode": "1111",
            "itemName": "2222",
            "createdBy":"api",
            "requestQty": "1",
            "totalPrice": "11.5"
        },
        {
            "itemCode": "1111",
            "itemName": "2222",
            "createdBy":"api",
            "requestQty": "2",
            "totalPrice": "23"
        },
        {
            "itemCode": "1111",
            "itemName": "2222",
            "createdBy":"api",
            "requestQty": "3",
            "totalPrice": "34.5"
        }
    ]
}
```
4. 数据库连接池最大连接数为10

#### 测试结论
|数据库连接池| 响应时间平均值(ms)| 响应时间中位数(ms)| 吞吐量 |
|-----| ----------- | ---------------- | ---- |
|Hikari| 180 | 171 | 218.6/sec|
|DBCP2| 178 | 168| 220.9/sec|
|Druid| 177 | 170| 227.3/sec|
