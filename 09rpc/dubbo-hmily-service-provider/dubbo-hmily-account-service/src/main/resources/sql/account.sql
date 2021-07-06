CREATE TABLE `account`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT,
    `userId`    int(11) NOT NULL,
    `onHandQty`  decimal (15,6) NOT NULL COMMENT '账户金额',
    `lockedQty`  decimal (15,6) NOT NULL COMMENT '暂时锁定金额',
    `inTransitQty`  decimal (15,6) NOT NULL COMMENT '在途金额',
    PRIMARY KEY (`id`),
    INDEX `user_index`(`userId`) USING BTREE
)ENGINE=InnoDB;