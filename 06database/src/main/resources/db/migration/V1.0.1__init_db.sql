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