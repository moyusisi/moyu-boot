# 站内信、PUSH、短信 ==> 任务待办、通知公告
-- 1. 站内消息表
DROP TABLE IF EXISTS `dev_message`;
CREATE TABLE `dev_message`
(
    `id`           BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `code`         VARCHAR(64)  DEFAULT NULL COMMENT '唯一编码',
    `message_type` TINYINT      DEFAULT 0 COMMENT '消息类型（0正常 1停用）',
    `title`        VARCHAR(255) DEFAULT NULL COMMENT '标题',
    `content`      TEXT         DEFAULT NULL COMMENT '内容说明',
    `ext_json`     TEXT         DEFAULT NULL COMMENT '扩展信息',
    `send_by`      VARCHAR(32)  DEFAULT NULL COMMENT '发送人',
    `send_time`    DATETIME     DEFAULT NULL COMMENT '发送时间',
    `expire_time`  DATETIME     DEFAULT NULL COMMENT '过期时间',
    `deleted`      TINYINT      DEFAULT 0 COMMENT '删除标志（0未删除  1已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_code` (`code`),
    KEY `idx_send_time` (`send_time`)
) ENGINE = InnoDB COMMENT ='站内消息表'
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- 2. 用户消息接收表
DROP TABLE IF EXISTS `dev_user_message`;
CREATE TABLE `dev_user_message`
(
    `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `from_id`     VARCHAR(64) DEFAULT NULL COMMENT '来源对象id',
    `user_id`     VARCHAR(64) DEFAULT NULL COMMENT '用户唯一id',
    `has_read`    TINYINT     DEFAULT 0 COMMENT '是否已读（0未读 1已读）',
    `read_time`   DATETIME    DEFAULT NULL COMMENT '已读时间',
    `deleted`     TINYINT     DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
    `create_time` DATETIME    DEFAULT NULL COMMENT '接收时间',
    PRIMARY KEY (`id`),
    KEY `idx_from_id` (`from_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB COMMENT ='用户消息接收表'
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

