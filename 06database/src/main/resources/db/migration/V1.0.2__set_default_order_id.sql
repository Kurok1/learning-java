ALTER TABLE `order_detail`
    MODIFY COLUMN `orderId` int(0) NOT NULL DEFAULT 0 COMMENT '订单头id' AFTER `id`;