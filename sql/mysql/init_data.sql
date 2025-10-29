-- 组织机构
insert into moyu.sys_org (id, parent_code, name, code, org_type, org_level, org_path, sort_num, status, ext_json, remark, deleted, create_time, create_by, update_time, update_by)
values  (2001, '0', 'MY集团', '10000000', 1, 1, '0', 1, 0, null, null, 0, null, null, null, null),
        (2002, '10000000', '集团总部', '11000000', 1, 2, '10000000,0', 2, 0, null, null, 0, null, null, null, null),
        (2003, '10000000', '北京公司', '12000000', 1, 2, '10000000,0', 4, 0, null, null, 0, null, null, null, null),
        (2004, '10000000', '华东公司', '14000000', 1, 2, '10000000,0', 6, 0, null, null, 0, null, null, null, null),
        (2005, '10000000', '华南公司', '16000000', 1, 2, '10000000,0', 8, 0, null, null, 0, null, null, null, null),
        (2006, '11000000', '总部财务部', '11001000', 2, 2, '11000000,10000000,0', 22, 0, null, null, 0, null, null, null, null),
        (2007, '11000000', '总部科技部', '11002000', 2, 2, '11000000,10000000,0', 24, 0, null, null, 0, null, null, null, null),
        (2008, '12000000', '北京运营部', '12001000', 2, 2, '12000000,10000000,0', 42, 0, null, null, 0, null, null, null, null),
        (2009, '12000000', '北京技术部', '12002000', 2, 2, '12000000,10000000,0', 44, 0, null, null, 0, null, null, null, null),
        (2010, '14000000', '华东技术部', '14001000', 2, 2, '14000000,10000000,0', 62, 0, null, null, 0, null, null, null, null),
        (2011, '16000000', '华南行政部', '16001000', 2, 2, '16000000,10000000,0', 84, 0, null, null, 0, null, null, null, null);

-- 用户
insert into moyu.sys_user (id, account, password, nick_name, avatar, name, gender, birthday, email, phone, id_no, address, staff_code, entry_date, org_code, org_name, org_path, login_ip, login_time, last_login_ip, last_login_time, pwd_update_time, status, remark, deleted, create_time, create_by, update_time, update_by)
values  (101, 'superAdmin', '$2a$10$ZxsW23u3p2wdnEpPTkT5zuOU.rs.TqyWAAa5eFTgxbQfbQggZ2Y3C', null, null, '超管', 1, null, null, null, null, null, null, null, '11000000', '集团总部', '11000000,10000000,0', null, null, null, null, null, 0, null, 0, null, null, null, null),
        (102, 'bjAdmin', '$2a$10$ZxsW23u3p2wdnEpPTkT5zuOU.rs.TqyWAAa5eFTgxbQfbQggZ2Y3C', null, null, 'bjAdmin', 1, null, null, null, null, null, null, null, '12000000', '北京公司', '12000000,10000000,0', null, null, null, null, null, 0, null, 0, null, null, null, null),
        (103, 'auditor', '$2a$10$ZxsW23u3p2wdnEpPTkT5zuOU.rs.TqyWAAa5eFTgxbQfbQggZ2Y3C', null, null, '审计员小王', 1, null, null, null, null, null, null, null, '11000000', '集团总部', '11000000,10000000,0', null, null, null, null, null, 0, null, 0, null, null, null, null);

-- 角色
insert into moyu.sys_role (id, name, code, sort_num, status, ext_json, remark, deleted, create_time, create_by, update_time, update_by)
values  (100, '默认角色', 'r_default', 1, 0, null, '', 0, null, null, null, null),
        (110, 'ROOT管理员', 'ROOT', 1, 0, null, '', 0, null, null, null, null),
        (120, '超级管理员', 'r_superAdmin', 2, 0, null, null, 0, null, null, null, null),
        (130, '审计员', 'r_auditor', 4, 0, null, '', 0, null, null, null, null);

-- 功能权限组
insert into moyu.sys_group (id, name, code, org_code, org_name, data_scope, scope_set, org_path, sort_num, status, ext_json, remark, deleted, create_time, create_by, update_time, update_by)
values  (1894925631903645700, '总部兼职岗', 'g_zongbu', '11000000', '集团总部', 3, '', '11000000,10000000,0', 2, 0, null, null, 0, null, null, null, null),
        (1894925631903645701, '北京技术部岗', 'g_bjtech', '12002000', '北京技术部', 3, '', '12002000,12000000,10000000,0', 9, 0, null, null, 0, null, null, null, null),
        (1894925631903645702, '全局审计岗', 'g_auditor', '10000000', 'MY集团', 3, '', '10000000,0', 5, 0, null, null, 0, null, null, null, null);

-- 菜单数据
insert into moyu.sys_resource (id, parent_code, name, code, resource_type, path, component, icon, permission, visible, link, module, sort_num, ext_json, remark, deleted, create_time, create_by, update_time, update_by)
values  (2001, '0', '系统模块', 'sys_module', 1, '/sysModule', 'Layout', 'appstore-add-outlined', '', 1, null, 'sys_module', 1, null, '', 0, null, null, null, null),
        (2002, '0', '业务模块', 'biz_module', 1, '/bizModule', 'Layout', 'profile-outlined', '', 1, null, null, 2, null, '', 0, null, null, null, null),

        (2003, 'sys_module', '组织架构', 'dir_sys_org', 2, '/org', null, 'apartment-outlined', '', 1, null, 'sys_module', 10, null, '', 0, null, null, null, null),
        (2004, 'sys_module', '权限控制', 'dir_sys_perm', 2, '/perm', null, 'user-switch-outlined', '', 1, null, 'sys_module', 20, null, '', 0, null, null, null, null),
        (2005, 'sys_module', '资源管理', 'dir_sys_resource', 2, '/sys/resource', null, 'trademark-circle-outlined', '', 1, null, 'sys_module', 30, null, null, 0, null, null, null, null),
        (2006, 'sys_module', '系统工具', 'dir_sys_dev', 2, '/dev', null, 'tool-outlined', '', 1, null, 'sys_module', 40, null, '', 0, null, null, null, null),
        (2007, 'sys_module', '系统运维', 'dir_sys_ops', 2, '/ops', null, 'hdd-outlined', '', 1, null, 'sys_module', 50, null, '', 0, null, null, null, null),
        (2008, 'sys_module', '移动端管理', 'dir_sys_mobile', 2, '/mobile', null, 'mobile-outlined', '', 1, null, 'sys_module', 60, null, '', 0, null, null, null, null),
        (2009, 'biz_module', '公司架构', 'dir_biz_company', 2, '/1nlpdpnief', null, 'cluster-outlined', '', 1, null, 'biz_module', 20, null, '', 0, null, null, null, null),
        (2010, 'biz_module', '通知公告', 'menu_biz_notice', 3, '/biz/notice', 'biz/notice/index', 'appstore-outlined', null, 1, null, 'biz_module', 30, null, '', 0, null, null, null, null),

        (2031, 'dir_sys_org', '组织管理', 'menu_sys_org', 3, '/sys/org', 'sys/org/index', 'cluster-outlined', null, 1, null, 'sys_module', 1010, null, '', 0, null, null, null, null),
        (2032, 'dir_sys_org', '用户管理', 'menu_sys_user', 3, '/sys/user', 'sys/user/index', 'user-outlined', '', 1, null, 'sys_module', 1020, null, '', 0, null, null, null, null),
        (2033, 'dir_sys_perm', '岗位权限', 'menu_sys_group', 3, '/sys/group', 'sys/group/index', 'team-outlined', '', 1, null, 'sys_module', 2010, null, '', 0, null, null, null, null),
        (2034, 'dir_sys_perm', '角色管理', 'menu_sys_role', 3, '/sys/role', 'sys/role/index', 'deployment-unit-outlined', '', 1, null, 'sys_module', 2020, null, '', 0, null, null, null, null),
        (2035, 'dir_sys_resource', '模块管理', 'menu_sys_module', 3, '/sys/module', 'sys/resource/module/index', 'appstore-add-outlined', '', 1, null, 'sys_module', 3010, null, '', 0, null, null, null, null),
        (2036, 'dir_sys_resource', '菜单管理', 'menu_sys_menu', 3, '/sys/menu', 'sys/resource/menu/index', 'pic-left-outlined', '', 1, null, 'sys_module', 3020, null, '', 0, null, null, null, null),
        (2037, 'dir_sys_resource', '接口管理', 'menu_sys_button', 3, '/sys/button', 'sys/resource/button/index', 'api-outlined', '', 1, null, 'sys_module', 3030, null, null, 0, null, null, null, null),

        (2038, 'dir_sys_dev', '代码生成', 'menu_sys_gen', 3, '/dev/gen', 'dev/gen/index', 'code-outlined', '', 1, null, 'sys_module', 4010, null, '', 0, null, null, null, null),
        (2039, 'dir_sys_dev', '文件管理', 'menu_sys_file', 3, '/dev/file/index', 'dev/file/index', 'copy-outlined', null, 1, null, 'sys_module', 4020, null, '', 0, null, null, null, null),
        (2040, 'dir_sys_dev', '邮件推送', 'menu_sys_email', 3, '/dev/email/index', 'dev/email/index', 'send-outlined', null, 1, null, 'sys_module', 4030, null, '', 0, null, null, null, null),
        (2041, 'dir_sys_dev', '短信发送', 'menu_sys_sms', 3, '/dev/sms/index', 'dev/sms/index', 'mail-outlined', null, 1, null, 'sys_module', 4040, null, '', 0, null, null, null, null),
        (2042, 'dir_sys_dev', '站内信息', 'menu_sys_message', 3, '/dev/message/index', 'dev/message/index', 'message-outlined', null, 1, null, 'sys_module', 4050, null, '', 0, null, null, null, null),
        (2043, 'dir_sys_ops', '系统日志', 'menu_sys_log', 3, '/ops/sys/log', 'sys/log/index', 'bars-outlined', null, 1, null, 'sys_module', 5002, null, '', 0, null, null, null, null),
        (2044, 'dir_sys_ops', '三方用户', 'menu_sys_third', 3, '/auth/third', 'auth/third/index', 'team-outlined', null, 1, null, 'sys_module', 5010, null, '', 0, null, null, null, null),
        (2045, 'dir_sys_ops', '任务调度', 'menu_sys_job', 3, '/dev/job', 'dev/job/index', 'field-time-outlined', null, 1, null, 'sys_module', 5020, null, '', 0, null, null, null, null),
        (2046, 'dir_sys_ops', '系统监控', 'menu_sys_monitor', 3, '/dev/monitor', 'dev/monitor/index', 'database-outlined', null, 1, null, 'sys_module', 5030, null, '', 0, null, null, null, null),
        (2047, 'dir_sys_ops', '内部链接', 'iframe_sys_link', 4, 'https://www.baidu.com', '', 'disconnect-outlined', '', 1, null, 'sys_module', 5040, null, '', 0, null, null, null, null),
        (2048, 'dir_sys_ops', '外部链接', 'link_sys_doc', 5, 'https://www.baidu.com', '', 'link-outlined', '', 1, null, 'sys_module', 5050, null, '', 0, null, null, null, null),
        (2049, 'dir_sys_ops', '日志审计', 'dir_sys_log', 2, '/ops/log', null, 'robot-outlined', null, 1, null, 'sys_module', 5060, null, '', 0, null, null, null, null),
        (2050, 'dir_sys_log', '访问日志', 'menu_sys_log_visit', 3, '/dev/vislog', 'dev/log/vislog/index', 'bars-outlined', null, 1, null, 'sys_module', 5061, null, '', 0, null, null, null, null),
        (2051, 'dir_sys_log', '操作日志', 'menu_sys_log_op', 3, '/dev/oplog', 'dev/log/oplog/index', 'bars-outlined', null, 1, null, 'sys_module', 5062, null, '', 0, null, null, null, null),
        (2052, 'dir_sys_mobile', '模块管理', 'menu_sys_mobile_module', 3, '/mobile/module/index', 'mobile/resource/module/index', 'build-outlined', null, 1, null, 'sys_module', 51, null, '', 0, null, null, null, null),
        (2053, 'dir_sys_mobile', '菜单管理', 'menu_sys_mobile_menu', 3, '/mobile/menu/index', 'mobile/resource/menu/index', 'appstore-add-outlined', null, 1, null, 'sys_module', 52, null, '', 0, null, null, null, null),
        (2054, 'dir_biz_company', '机构管理', 'menu_biz_org', 3, '/biz/org', 'biz/org/index', 'cluster-outlined', null, 1, null, 'biz_module', 52, null, '', 0, null, null, null, null),
        (2055, 'dir_biz_company', '人员管理', 'menu_biz_user', 3, '/biz/user', 'biz/user/index', 'user-outlined', null, 1, null, 'biz_module', 53, null, '', 0, null, null, null, null),
        (2056, 'dir_biz_company', '岗位管理', 'menu_biz_pos', 3, '/biz/position', 'biz/position/index', 'apartment-outlined', null, 1, null, 'biz_module', 54, null, '', 0, null, null, null, null),

        (2101, 'menu_sys_org', '新增组织', 'btn_sys_org_add', 6, '/api/sys/org/add', '', '', 'sys:org:add', 1, null, 'sys_module', 101001, null, '', 0, null, null, null, null),
        (2102, 'menu_sys_org', '删除组织', 'btn_sys_org_delete', 6, '/api/sys/org/delete', '', '', 'sys:org:delete', 1, null, 'sys_module', 101002, null, '', 0, null, null, null, null),
        (2103, 'menu_sys_org', '修改组织', 'btn_sys_org_edit', 6, '/api/sys/org/edit', '', '', 'sys:org:edit', 1, null, 'sys_module', 101003, null, '', 0, null, null, null, null),
        (2104, 'menu_sys_org', '组织列表', 'btn_sys_org_list', 6, '/api/sys/org/page', '', '', 'sys:org:page', 1, null, 'sys_module', 101004, null, '', 0, null, null, null, null),
        (2105, 'menu_sys_org', '组织详情', 'btn_sys_org_detail', 6, '/api/sys/org/detail', '', null, 'sys:org:detail', 1, null, 'sys_module', 101005, null, null, 0, null, null, null, null),
        (2106, 'menu_sys_org', '删除组织树', 'btn_sys_org_deleteTree', 6, '/api/sys/org/deleteTree', '', null, 'sys:org:deleteTree', 1, null, 'sys_module', 101006, null, null, 0, null, null, null, null),

        (2107, 'menu_sys_user', '新增用户', 'btn_sys_user_add', 6, '/api/sys/user/add', '', null, 'sys:user:add', 1, null, 'sys_module', 102001, null, null, 0, null, null, null, null),
        (2108, 'menu_sys_user', '删除用户', 'btn_sys_user_delete', 6, '/api/sys/user/delete', '', null, 'sys:user:delete', 1, null, 'sys_module', 102002, null, null, 0, null, null, null, null),
        (2109, 'menu_sys_user', '修改用户', 'btn_sys_user_edit', 6, '/api/sys/user/add', '', null, 'sys:user:edit', 1, null, 'sys_module', 102003, null, null, 0, null, null, null, null),
        (2110, 'menu_sys_user', '用户列表', 'btn_sys_user_list', 6, '/api/sys/user/page', '', null, 'sys:user:page', 1, null, 'sys_module', 102004, null, null, 0, null, null, null, null),
        (2111, 'menu_sys_user', '用户详情', 'btn_sys_user_detail', 6, '/api/sys/user/detail', '', null, 'sys:user:detail', 1, null, 'sys_module', 102005, null, null, 0, null, null, null, null),

        (2112, 'menu_sys_group', '新增岗位', 'btn_sys_group_add', 6, '/api/sys/group/add', '', null, 'sys:group:add', 1, null, 'sys_module', 201001, null, null, 0, null, null, null, null),
        (2113, 'menu_sys_group', '删除岗位', 'btn_sys_group_delete', 6, '/api/sys/group/delete', '', null, 'sys:group:delete', 1, null, 'sys_module', 201002, null, null, 0, null, null, null, null),
        (2114, 'menu_sys_group', '修改岗位', 'btn_sys_group_edit', 6, '/api/sys/group/add', '', null, 'sys:group:edit', 1, null, 'sys_module', 201003, null, null, 0, null, null, null, null),
        (2115, 'menu_sys_group', '岗位列表', 'btn_sys_group_list', 6, '/api/sys/group/page', '', null, 'sys:group:page', 1, null, 'sys_module', 201004, null, null, 0, null, null, null, null),
        (2116, 'menu_sys_group', '岗位详情', 'btn_sys_group_detail', 6, '/api/sys/group/detail', '', null, 'sys:group:detail', 1, null, 'sys_module', 201005, null, null, 0, null, null, null, null),
        (2117, 'menu_sys_group', '岗位用户列表', 'btn_sys_group_userList', 6, '/api/sys/group/userList', '', null, 'sys:group:userList', 1, null, 'sys_module', 201006, null, null, 0, null, null, null, null),
        (2118, 'menu_sys_group', '岗位新增用户', 'btn_sys_group_addUser', 6, '/api/sys/group/addUser', null, null, 'sys:group:addUser', 1, null, 'sys_module', 201007, null, null, 0, null, null, null, null),
        (2119, 'menu_sys_group', '岗位移除用户', 'btn_sys_group_deleteUser', 6, '/api/sys/group/deleteUser', null, null, 'sys:group:deleteUser', 1, null, 'sys_module', 201008, null, null, 0, null, null, null, null),
        (2120, 'menu_sys_group', '岗位角色列表', 'btn_sys_group_roleList', 6, '/api/sys/group/roleList', '', null, 'sys:group:roleList', 1, null, 'sys_module', 201009, null, null, 0, null, null, null, null),
        (2121, 'menu_sys_group', '岗位新增角色', 'btn_sys_group_addRole', 6, '/api/sys/group/addRole', null, null, 'sys:group:addRole', 1, null, 'sys_module', 201010, null, null, 0, null, null, null, null),
        (2122, 'menu_sys_group', '岗位移除角色', 'btn_sys_group_deleteRole', 6, '/api/sys/group/deleteRole', null, null, 'sys:group:deleteRole', 1, null, 'sys_module', 201011, null, null, 0, null, null, null, null),

        (2123, 'menu_sys_role', '新增角色', 'btn_sys_role_add', 6, '/api/sys/role/add', '', null, 'sys:role:add', 1, null, 'sys_module', 202001, null, null, 0, null, null, null, null),
        (2124, 'menu_sys_role', '删除角色', 'btn_sys_role_delete', 6, '/api/sys/role/delete', '', null, 'sys:role:delete', 1, null, 'sys_module', 202002, null, null, 0, null, null, null, null),
        (2125, 'menu_sys_role', '修改角色', 'btn_sys_role_edit', 6, '/api/sys/role/add', '', null, 'sys:role:edit', 1, null, 'sys_module', 202003, null, null, 0, null, null, null, null),
        (2126, 'menu_sys_role', '角色列表', 'btn_sys_role_list', 6, '/api/sys/role/page', '', '', 'sys:role:page', 1, null, 'sys_module', 202004, null, '', 0, null, null, null, null),
        (2127, 'menu_sys_role', '角色详情', 'btn_sys_role_detail', 6, '/api/sys/role/detail', '', null, 'sys:role:detail', 1, null, 'sys_module', 202005, null, null, 0, null, null, null, null),
        (2128, 'menu_sys_role', '角色用户列表', 'btn_sys_role_userList', 6, '/api/sys/role/userList', '', null, 'sys:role:userList', 1, null, 'sys_module', 202006, null, null, 0, null, null, null, null),
        (2129, 'menu_sys_role', '角色新增用户', 'btn_sys_role_addUser', 6, '/api/sys/role/addUser', null, null, 'sys:role:addUser', 1, null, 'sys_module', 202007, null, null, 0, null, null, null, null),
        (2130, 'menu_sys_role', '角色移除用户', 'btn_sys_role_deleteUser', 6, '/api/sys/role/deleteUser', null, null, 'sys:role:deleteUser', 1, null, 'sys_module', 202008, null, null, 0, null, null, null, null),
        (2131, 'menu_sys_role', '角色授权', 'btn_sys_role_grantMenu', 6, '/api/sys/role/grantMenu', null, null, 'sys:role:grantMenu', 1, null, 'sys_module', 202009, null, null, 0, null, null, null, null),

        (2132, 'menu_sys_module', '新增模块', 'btn_sys_module_add', 6, '/api/sys/resource/add', '', null, 'sys:resource:add', 1, null, 'sys_module', 301001, null, null, 0, null, null, null, null),
        (2133, 'menu_sys_module', '删除模块', 'btn_sys_module_delete', 6, '/api/sys/resource/delete', '', null, 'sys:resource:delete', 1, null, 'sys_module', 301002, null, null, 0, null, null, null, null),
        (2134, 'menu_sys_module', '修改模块', 'btn_sys_module_edit', 6, '/api/sys/resource/add', '', null, 'sys:resource:edit', 1, null, 'sys_module', 301003, null, null, 0, null, null, null, null),
        (2135, 'menu_sys_module', '模块列表', 'btn_sys_module_list', 6, '/api/sys/resource/page', '', null, 'sys:resource:page', 1, null, 'sys_module', 301004, null, null, 0, null, null, null, null),
        (2136, 'menu_sys_module', '模块详情', 'btn_sys_module_detail', 6, '/api/sys/resource/detail', '', null, 'sys:resource:detail', 1, null, 'sys_module', 301005, null, null, 0, null, null, null, null),

        (2137, 'menu_sys_menu', '新增菜单', 'btn_sys_menu_add', 6, '/api/sys/resource/add', '', '', 'sys:resource:add', 1, null, 'sys_module', 302001, null, '', 0, null, null, null, null),
        (2138, 'menu_sys_menu', '删除菜单树', 'btn_sys_menu_deleteTree', 6, '/api/sys/resource/deleteTree', '', null, 'sys:resource:deleteTree', 1, null, 'sys_module', 302002, null, null, 0, null, null, null, null),
        (2139, 'menu_sys_menu', '修改菜单', 'btn_sys_menu_edit', 6, '/api/sys/resource/edit', '', null, 'sys:resource:edit', 1, null, 'sys_module', 302003, null, null, 0, null, null, null, null),
        (2140, 'menu_sys_menu', '菜单树', 'btn_sys_menu_tree', 6, '/api/sys/resource/tree', '', null, 'sys:resource:detail', 1, null, 'sys_module', 302004, null, null, 0, null, null, null, null),
        (2141, 'menu_sys_menu', '菜单详情', 'btn_sys_menu_detail', 6, '/api/sys/resource/detail', '', null, 'sys:resource:detail', 1, null, 'sys_module', 302005, null, null, 0, null, null, null, null),

        (2142, 'menu_sys_button', '新增按钮', 'btn_sys_button_add', 6, '/api/sys/resource/add', '', null, 'sys:resource:add', 1, null, 'sys_module', 303001, null, null, 0, null, null, null, null),
        (2143, 'menu_sys_button', '删除按钮', 'btn_sys_button_delete', 6, '/api/sys/resource/delete', '', null, 'sys:resource:delete', 1, null, 'sys_module', 303002, null, null, 0, null, null, null, null),
        (2144, 'menu_sys_button', '修改按钮', 'btn_sys_button_edit', 6, '/api/sys/resource/add', '', null, 'sys:resource:edit', 1, null, 'sys_module', 303003, null, null, 0, null, null, null, null),
        (2145, 'menu_sys_button', '按钮列表', 'btn_sys_button_list', 6, '/api/sys/resource/page', '', null, 'sys:resource:page', 1, null, 'sys_module', 303004, null, null, 0, null, null, null, null),
        (2146, 'menu_sys_button', '按钮详情', 'btn_sys_button_detail', 6, '/api/sys/resource/detail', '', null, 'sys:resource:detail', 1, null, 'sys_module', 303005, null, null, 0, null, null, null, null),

        (2147, 'menu_biz_org', '新增机构', 'btn_biz_org_add', 6, null, null, null, null, 1, null, 'biz_module', 511001, null, '', 0, null, null, null, null),
        (2148, 'menu_biz_org', '删除机构', 'btn_biz_org_del', 6, null, null, null, null, 1, null, 'biz_module', 511002, null, '', 0, null, null, null, null),
        (2149, 'menu_biz_org', '编辑机构', 'btn_biz_org_edit', 6, null, null, null, null, 1, null, 'biz_module', 511003, null, '', 0, null, null, null, null);

-- 关系
insert into moyu.sys_relation (id, object_id, target_id, relation_type, deleted, create_time, create_by, update_time, update_by)
values  (1, 'ROOT', 'superAdmin', 1, 0, null, null, null, null),
        (2, 'r_superAdmin', 'superAdmin', 1, 0, null, null, null, null),

        (3, 'r_superAdmin', 'menu_sys_org', 2, 0, null, null, null, null),
        (4, 'r_superAdmin', 'menu_sys_user', 2, 0, null, null, null, null),
        (5, 'r_superAdmin', 'menu_sys_group', 2, 0, null, null, null, null),
        (6, 'r_superAdmin', 'menu_sys_role', 2, 0, null, null, null, null),
        (7, 'r_superAdmin', 'menu_sys_module', 2, 0, null, null, null, null),
        (8, 'r_superAdmin', 'menu_sys_menu', 2, 0, null, null, null, null),
        (9, 'r_superAdmin', 'menu_sys_button', 2, 0, null, null, null, null),
        (10, 'r_superAdmin', 'menu_sys_gen', 2, 0, null, null, null, null),
        (11, 'r_superAdmin', 'iframe_sys_link', 2, 0, null, null, null, null),
        (12, 'r_superAdmin', 'link_sys_doc', 2, 0, null, null, null, null),
        (13, 'r_superAdmin', 'menu_sys_log', 2, 0, null, null, null, null),
        (14, 'r_superAdmin', 'menu_sys_log_visit', 2, 0, null, null, null, null),
        (15, 'r_superAdmin', 'menu_sys_log_op', 2, 0, null, null, null, null),
        (16, 'r_superAdmin', 'menu_biz_pos', 2, 0, null, null, null, null),

        (31, 'r_auditor', 'menu_sys_org', 2, 0, null, null, null, null),
        (32, 'r_auditor', 'menu_sys_user', 2, 0, null, null, null, null),
        (33, 'r_auditor', 'menu_sys_group', 2, 0, null, null, null, null),
        (34, 'r_auditor', 'menu_sys_role', 2, 0, null, null, null, null),
        (35, 'r_auditor', 'menu_sys_module', 2, 0, null, null, null, null),
        (36, 'r_auditor', 'menu_sys_menu', 2, 0, null, null, null, null),
        (37, 'r_auditor', 'menu_sys_button', 2, 0, null, null, null, null),
        (38, 'r_auditor', 'iframe_sys_link', 2, 0, null, null, null, null),
        (39, 'r_auditor', 'link_sys_doc', 2, 0, null, null, null, null),
        (40, 'r_auditor', 'menu_sys_log', 2, 0, null, null, null, null),
        (41, 'r_auditor', 'menu_sys_log_visit', 2, 0, null, null, null, null),
        (42, 'r_auditor', 'menu_sys_log_op', 2, 0, null, null, null, null),
        (43, 'r_auditor', 'menu_biz_pos', 2, 0, null, null, null, null),
        (44, 'r_auditor', 'menu_biz_notice', 2, 0, null, null, null, null),
        (45, 'r_auditor', 'btn_sys_user_list', 2, 0, null, null, null, null),
        (46, 'r_auditor', 'btn_sys_user_detail', 2, 0, null, null, null, null),
        (47, 'r_auditor', 'btn_sys_role_userList', 2, 0, null, null, null, null),
        (48, 'r_auditor', 'btn_sys_role_list', 2, 0, null, null, null, null),
        (49, 'r_auditor', 'btn_sys_role_detail', 2, 0, null, null, null, null),
        (50, 'r_auditor', 'btn_sys_org_list', 2, 0, null, null, null, null),
        (51, 'r_auditor', 'btn_sys_org_detail', 2, 0, null, null, null, null),
        (52, 'r_auditor', 'btn_sys_module_list', 2, 0, null, null, null, null),
        (53, 'r_auditor', 'btn_sys_module_detail', 2, 0, null, null, null, null),
        (54, 'r_auditor', 'btn_sys_menu_tree', 2, 0, null, null, null, null),
        (55, 'r_auditor', 'btn_sys_menu_detail', 2, 0, null, null, null, null),
        (56, 'r_auditor', 'btn_sys_group_userList', 2, 0, null, null, null, null),
        (57, 'r_auditor', 'btn_sys_group_roleList', 2, 0, null, null, null, null),
        (58, 'r_auditor', 'btn_sys_group_list', 2, 0, null, null, null, null),
        (59, 'r_auditor', 'btn_sys_group_detail', 2, 0, null, null, null, null),
        (60, 'r_auditor', 'btn_sys_button_list', 2, 0, null, null, null, null),
        (61, 'r_auditor', 'btn_sys_button_detail', 2, 0, null, null, null, null),

        (121, 'g_zongbu', 'superAdmin', 3, 0, null, null, null, null),
        (122, 'g_zongbu', 'bjAdmin', 3, 0, null, null, null, null),
        (123, 'g_zongbu', 'r_superAdmin', 4, 0, null, null, null, null),
        (124, 'g_bjtech', 'bjAdmin', 3, 0, null, null, null, null),
        (125, 'g_bjtech', 'r_superAdmin', 4, 0, null, null, null, null),
        (127, 'g_auditor', 'auditor', 3, 0, null, null, null, null),
        (128, 'g_auditor', 'r_auditor', 4,  0, null, null, null, null);

