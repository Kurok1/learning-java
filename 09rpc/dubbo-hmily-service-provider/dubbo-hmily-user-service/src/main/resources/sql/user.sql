CREATE TABLE `user`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT,
    `code`  varchar(50) NOT NULL COMMENT '用户编码',
    `name`  varchar(50) NULL COMMENT '用户名称',
    PRIMARY KEY (`id`),
    INDEX `code_index`(`code`) USING BTREE
)ENGINE=InnoDB;