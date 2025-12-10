-- 1. 代码生成配置表
DROP TABLE IF EXISTS `gen_config`;
CREATE TABLE `gen_config`
(
    `id`               BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `table_name`       VARCHAR(100) NOT NULL COMMENT '表名',
    `table_comment`    VARCHAR(100) COMMENT '表描述',
    `package_name`     VARCHAR(255) NOT NULL COMMENT '包名',
    `module_name`      VARCHAR(100) NOT NULL COMMENT '模块名',
    `entity_name`      VARCHAR(100) NOT NULL COMMENT '实体类名',
    `entity_desc`      VARCHAR(100) DEFAULT NULL COMMENT '实体类描述',
    `author`           VARCHAR(50)  DEFAULT NULL COMMENT '作者',
    `parent_menu_code` VARCHAR(64)  DEFAULT NULL COMMENT '父菜单编码',
    `detail_open_type` INT          DEFAULT '0' COMMENT '详情页打开方式，字典:0页内打开,1独立页面打开',
    `source_type`      VARCHAR(20)  DEFAULT NULL COMMENT '来源类型',

    `create_time`      DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_time`      DATETIME     DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_table_name` (`table_name`)
) ENGINE = InnoDB COMMENT ='代码生成配置表'
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- 2. 代码生成字段配置表
DROP TABLE IF EXISTS `gen_field_config`;
CREATE TABLE `gen_field_config`
(
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `table_id`      BIGINT(20)   NOT NULL COMMENT '关联的实体配置ID',
    `column_name`   VARCHAR(100) NOT NULL COMMENT '列名称',
    `column_type`   VARCHAR(50)  NOT NULL COMMENT '列类型',
    `field_name`    VARCHAR(100) NOT NULL COMMENT '字段名称',
    `field_type`    VARCHAR(100) NOT NULL COMMENT '字段类型',
    `field_remark`  VARCHAR(255) COMMENT '字段描述',
    `field_sort`    INT COMMENT '字段排序',
    `max_length`    INT COMMENT '最大长度',
    `required`      TINYINT(5) DEFAULT '0' COMMENT '是否必填',
    `ellipsis`      TINYINT(5) DEFAULT '0' COMMENT '是否省略显示并提示',
    `show_in_list`  TINYINT(5) DEFAULT '0' COMMENT '是否在列表显示',
    `show_in_form`  TINYINT(5) DEFAULT '0' COMMENT '是否在表单显示',
    `show_in_query` TINYINT(5) DEFAULT '0' COMMENT '是否在查询条件显示',
    `query_type`    VARCHAR(20) COMMENT '查询方式',
    `form_type`     VARCHAR(20) COMMENT '表单类型',
    `dict_type`     VARCHAR(50) COMMENT '字典类型',
    `create_time`   DATETIME COMMENT '创建时间',
    `update_time`   DATETIME COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_table_id` (`table_id`)
) ENGINE = InnoDB COMMENT ='代码生成字段配置表'
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

