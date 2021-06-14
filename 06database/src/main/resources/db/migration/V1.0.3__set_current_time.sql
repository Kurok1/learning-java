ALTER TABLE `order_detail`
    MODIFY COLUMN `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP AFTER `orderCode`;

ALTER TABLE `order_header`
    MODIFY COLUMN `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP AFTER `shipToPhone`;