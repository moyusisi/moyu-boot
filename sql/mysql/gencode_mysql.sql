-- 1. 代码生成实体配置表
DROP TABLE IF EXISTS `gen_config`;
CREATE TABLE `gen_config`
(
    `id`               BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `table_name`       VARCHAR(100) NOT NULL COMMENT '表名',
    `module_name`      VARCHAR(100) COMMENT '模块名',
    `package_name`     VARCHAR(255) NOT NULL COMMENT '包名',
    `business_name`    VARCHAR(100) NOT NULL COMMENT '业务名',
    `entity_name`      VARCHAR(100) NOT NULL COMMENT '实体类名',
    `author`           VARCHAR(50)  NOT NULL COMMENT '作者',
    `parent_menu_code` VARCHAR(64)  NULL DEFAULT '0' COMMENT '父菜单编码',

    `delete_flag`      TINYINT(5)   NULL DEFAULT 0 COMMENT '删除标志（0未删除  1已删除）',
    `create_time`      DATETIME     NULL DEFAULT NULL COMMENT '创建时间',
    `create_by`        VARCHAR(20)  NULL DEFAULT NULL COMMENT '创建人',
    `update_time`      DATETIME     NULL DEFAULT NULL COMMENT '修改时间',
    `update_by`        VARCHAR(20)  NULL DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_table_name` (`table_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='代码生成实体配置表'
  COLLATE = utf8mb4_general_ci;
