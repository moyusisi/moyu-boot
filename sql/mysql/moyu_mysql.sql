-- 1. 组织机构表
DROP TABLE IF EXISTS `sys_org`;
CREATE TABLE `sys_org`
(
    `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `parent_code` VARCHAR(64)   DEFAULT '0' COMMENT '父编码',
    `name`        VARCHAR(64)   DEFAULT NULL COMMENT '名称',
    `code`        VARCHAR(64)   DEFAULT NULL COMMENT '编码',
    `org_type`    TINYINT       DEFAULT NULL COMMENT '组织机构类型(字典 1公司组织 2部门机构 3虚拟节点)',
    `org_level`   TINYINT       DEFAULT NULL COMMENT '组织层级(字典 1一级公司 2二级公司 3三级公司)',
    `org_path`    VARCHAR(1024) DEFAULT NULL COMMENT '组织机构层级路径,逗号分隔,父节点在后',

    `sort_num`    INT           DEFAULT NULL COMMENT '排序顺序',
    `status`      TINYINT       DEFAULT 0 COMMENT '状态（0正常 1停用）',
    `ext_json`    TEXT          DEFAULT NULL COMMENT '扩展信息',
    `remark`      TEXT          DEFAULT NULL comment '备注',
    `deleted`     TINYINT       DEFAULT 0 COMMENT '删除标志（0未删除  1已删除）',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `create_by`   VARCHAR(20)   DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME      DEFAULT NULL COMMENT '修改时间',
    `update_by`   VARCHAR(20)   DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  AUTO_INCREMENT = 2000 COMMENT = '组织机构表';

-- 2. 用户信息表
drop table if exists sys_user;
create table sys_user
(
    `id`              BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `account`         VARCHAR(64)   DEFAULT NULL COMMENT '账号',
    `password`        VARCHAR(64)   DEFAULT NULL COMMENT '密码',
    `nick_name`       VARCHAR(64)   DEFAULT NULL COMMENT '昵称',
    `avatar`          VARCHAR(255)  DEFAULT NULL COMMENT '头像',
    `name`            VARCHAR(64)   DEFAULT NULL COMMENT '姓名',
    `gender`          TINYINT(4)    DEFAULT NULL COMMENT '性别(字典 0未知 1男 2女)',
    `birthday`        DATETIME      DEFAULT NULL COMMENT '生日',
    `email`           VARCHAR(64)   DEFAULT NULL COMMENT '邮箱',
    `phone`           VARCHAR(20)   DEFAULT NULL COMMENT '手机',
    `id_no`           VARCHAR(20)   DEFAULT NULL COMMENT '身份证号',
    `address`         VARCHAR(200)  DEFAULT NULL COMMENT '联系地址',

    `staff_code`      VARCHAR(64)   DEFAULT NULL COMMENT '员工编码',
    `entry_date`      DATETIME      DEFAULT NULL COMMENT '员工入职日期',
    `org_code`        VARCHAR(64)   DEFAULT NULL COMMENT '直属组织编码',
    `org_name`        VARCHAR(64)   DEFAULT NULL COMMENT '直属组织名称',
    `org_path`        VARCHAR(1024) DEFAULT NULL COMMENT '组织机构层级路径,逗号分隔,父节点在后',

    `login_ip`        VARCHAR(20)   DEFAULT NULL COMMENT '登陆IP',
    `login_time`      DATETIME      DEFAULT NULL COMMENT '登陆时间',
    `last_login_ip`   VARCHAR(20)   DEFAULT NULL COMMENT '上次登陆IP',
    `last_login_time` DATETIME      DEFAULT NULL COMMENT '上次登陆时间',
    `pwd_update_time` DATETIME      DEFAULT NULL COMMENT '上次密码更新时间',

    `status`          TINYINT       DEFAULT 0 COMMENT '状态（0正常 1停用）',
    `remark`          TEXT          DEFAULT NULL comment '备注',
    `deleted`         TINYINT       DEFAULT 0 COMMENT '删除标志（0未删除  1已删除）',
    `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
    `create_by`       VARCHAR(32)   DEFAULT NULL COMMENT '创建人',
    `update_time`     DATETIME      DEFAULT NULL COMMENT '修改时间',
    `update_by`       VARCHAR(32)   DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uniq_account` (`account`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  AUTO_INCREMENT = 2000 COMMENT = '用户信息表';

-- 3. 角色信息表
drop table if exists sys_role;
create table sys_role
(
    `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `name`        VARCHAR(64) DEFAULT NULL COMMENT '角色名称',
    `code`        VARCHAR(64) DEFAULT NULL COMMENT '角色编码',

    `sort_num`    INT         DEFAULT NULL COMMENT '排序顺序',
    `status`      TINYINT     DEFAULT 0 COMMENT '使用状态（0正常 1停用）',
    `ext_json`    TEXT        DEFAULT NULL COMMENT '扩展信息',
    `remark`      TEXT        DEFAULT NULL comment '备注',
    `deleted`     TINYINT     DEFAULT 0 COMMENT '删除标志（0未删除  1已删除）',
    `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
    `create_by`   VARCHAR(32) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME    DEFAULT NULL COMMENT '修改时间',
    `update_by`   VARCHAR(32) DEFAULT NULL COMMENT '修改人',
    primary key (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  AUTO_INCREMENT = 100 COMMENT = '角色信息表';

-- 4. 分组信息表
drop table if exists sys_group;
create table sys_group
(
    `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `name`        VARCHAR(64)   DEFAULT NULL COMMENT '名称',
    `code`        VARCHAR(64)   DEFAULT NULL COMMENT '编码',
    `org_code`    VARCHAR(64)   DEFAULT NULL COMMENT '直属组织编码',
    `org_name`    VARCHAR(64)   DEFAULT NULL COMMENT '直属组织名称',
    `data_scope`  TINYINT       DEFAULT NULL COMMENT '数据范围(字典 0无限制 1本人数据 2本机构 3本机构及以下 4自定义)',
    `scope_set`   VARCHAR(1024) DEFAULT NULL COMMENT '自定义scope集合,逗号分隔',
    `org_path`    VARCHAR(1024) DEFAULT NULL COMMENT '组织机构层级路径,逗号分隔,父节点在后',

    `sort_num`    INT           DEFAULT NULL COMMENT '排序顺序',
    `status`      TINYINT       DEFAULT 0 COMMENT '状态（0正常 1停用）',
    `ext_json`    TEXT          DEFAULT NULL COMMENT '扩展信息',
    `remark`      TEXT          DEFAULT NULL comment '备注',
    `deleted`     TINYINT       DEFAULT 0 COMMENT '删除标志（0未删除  1已删除）',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `create_by`   VARCHAR(32)   DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME      DEFAULT NULL COMMENT '修改时间',
    `update_by`   VARCHAR(32)   DEFAULT NULL COMMENT '修改人',
    primary key (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  AUTO_INCREMENT = 1000 COMMENT = '分组信息表';

-- 5. 资源权限表
drop table if exists sys_resource;
create table sys_resource
(
    `id`            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `parent_code`   VARCHAR(64)  DEFAULT '0' COMMENT '父编码',
    `name`          VARCHAR(64)  DEFAULT NULL COMMENT '名称',
    `code`          VARCHAR(64)  DEFAULT NULL COMMENT '编码',
    `resource_type` TINYINT      DEFAULT NULL COMMENT '资源类型（字典 1模块 2目录 3菜单 4内链 5外链 6按钮）',
    `path`          VARCHAR(64)  DEFAULT NULL COMMENT '路由地址',
    `component`     VARCHAR(64)  DEFAULT NULL COMMENT '组件地址',
    `permission`    VARCHAR(64)  DEFAULT NULL COMMENT '权限标识',
    `link`          VARCHAR(255) DEFAULT NULL COMMENT '链接地址',
    `icon`          VARCHAR(64)  DEFAULT NULL COMMENT '图标',
    `visible`       TINYINT      DEFAULT 1 COMMENT '是否可见（0不可见 1可见）',
    `module`        VARCHAR(64)  DEFAULT NULL COMMENT '归属模块',

    `sort_num`      INT          DEFAULT NULL COMMENT '排序顺序',
    `status`        TINYINT      DEFAULT 0 COMMENT '使用状态（0正常 1停用）',
    `ext_json`      TEXT         DEFAULT NULL COMMENT '扩展信息',
    `remark`        TEXT         DEFAULT NULL comment '备注',
    `deleted`       TINYINT      DEFAULT 0 COMMENT '删除标志（0未删除  1已删除）',
    `create_time`   DATETIME     DEFAULT NULL COMMENT '创建时间',
    `create_by`     VARCHAR(32)  DEFAULT NULL COMMENT '创建人',
    `update_time`   DATETIME     DEFAULT NULL COMMENT '修改时间',
    `update_by`     VARCHAR(32)  DEFAULT NULL COMMENT '修改人',
    primary key (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  AUTO_INCREMENT = 2000 COMMENT = '资源权限表';

-- 6. 用户角色权限关系表
DROP TABLE IF EXISTS `sys_relation`;
CREATE TABLE `sys_relation`
(
    `id`            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `object_id`     VARCHAR(64) DEFAULT NULL COMMENT '对象ID',
    `target_id`     VARCHAR(64) DEFAULT NULL COMMENT '目标ID',
    `relation_type` TINYINT     DEFAULT NULL COMMENT '关系类型(字典 1:role_has_user,2:role_has_perm,3:group_has_user,4:group_has_role)',
    `deleted`       TINYINT     DEFAULT 0 COMMENT '删除标志（0未删除  1已删除）',
    `create_time`   DATETIME    DEFAULT NULL COMMENT '创建时间',
    `create_by`     VARCHAR(32) DEFAULT NULL COMMENT '创建人',
    `update_time`   DATETIME    DEFAULT NULL COMMENT '修改时间',
    `update_by`     VARCHAR(32) DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  AUTO_INCREMENT = 2000 COMMENT = '用户角色权限关系表';