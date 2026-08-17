-- 三方接口表(可选)
drop table if exists third_party_api;
create table third_party_api
(
    `id`             BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `code`           VARCHAR(64)   DEFAULT NULL COMMENT '三方接口唯一标识',
    `name`           VARCHAR(64)   DEFAULT NULL COMMENT '三方接口名称',
    `url`            VARCHAR(1024) DEFAULT NULL COMMENT '三方接口URL',
    `request_method` VARCHAR(16)   DEFAULT NULL COMMENT '请求方式 GET/POST等',

    `debug_status`   TINYINT       DEFAULT 0 COMMENT '调试状态（0未调通 1已调通）',
    `request_header` TEXT          DEFAULT NULL COMMENT '请求头参数',
    `request_body`   TEXT          DEFAULT NULL COMMENT '请求体参数',
    `response_body`  TEXT          DEFAULT NULL COMMENT '响应结果',
    `status_code`    VARCHAR(64)   DEFAULT NULL COMMENT 'HTTP状态码',
    `request_time`   DATETIME(3)   DEFAULT NULL COMMENT '请求时间',
    `response_time`  DATETIME(3)   DEFAULT NULL COMMENT '响应时间',

    `ext_json`       TEXT          DEFAULT NULL COMMENT '扩展信息',
    `remark`         TEXT          DEFAULT NULL COMMENT '备注',
    `deleted`        TINYINT       DEFAULT 0 COMMENT '删除标志（0未删除  1已删除）',
    `create_time`    DATETIME      DEFAULT NULL COMMENT '创建时间',
    `create_by`      VARCHAR(32)   DEFAULT NULL COMMENT '创建人',
    `update_time`    DATETIME      DEFAULT NULL COMMENT '修改时间',
    `update_by`      VARCHAR(32)   DEFAULT NULL COMMENT '修改人',
    primary key (`id`),
    UNIQUE INDEX `uniq_code` (`code`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  AUTO_INCREMENT = 100 COMMENT = '三方接口表';

-- 三方应用表(可选)
drop table if exists third_party_app;
create table third_party_app
(
    `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `app_key`     VARCHAR(64) DEFAULT NULL COMMENT '应用标识',
    `app_name`    VARCHAR(64) DEFAULT NULL COMMENT '应用名称',
    `app_secret`  VARCHAR(64) DEFAULT NULL COMMENT '应用密钥',
    `digest_algo` VARCHAR(16) DEFAULT NULL COMMENT '签名算法',

    `ext_json`    TEXT        DEFAULT NULL COMMENT '扩展信息',
    `remark`      TEXT        DEFAULT NULL COMMENT '备注',
    `deleted`     TINYINT     DEFAULT 0 COMMENT '删除标志（0未删除  1已删除）',
    `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
    `create_by`   VARCHAR(32) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME    DEFAULT NULL COMMENT '修改时间',
    `update_by`   VARCHAR(32) DEFAULT NULL COMMENT '修改人',
    primary key (`id`),
    UNIQUE INDEX `uniq_app_key` (`app_key`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  AUTO_INCREMENT = 100 COMMENT = '三方应用表';
