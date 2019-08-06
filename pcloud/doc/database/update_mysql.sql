--修改表字段
--hsjiang
--2019-07-04
alter table acd_evaluation change  `desc` description  varchar(2048)  COMMENT '评价说明';
alter table acd_evaluation_score change  `desc` description  varchar(2048)  COMMENT '评价说明';
alter table acd_terminal change  `desc` description  varchar(2048)  COMMENT '设备说明';
alter table sys_user change  `call` telephone varchar(64)  COMMENT '电话';
alter table biz_customer change  `call` telephone varchar(64)  COMMENT '电话';
alter table sys_role change  level role_level char(1)  COMMENT '角色等级 ‘0’-超级管理员； ‘1’-用户管理员；‘2’-其它';
alter table sys_userconnection change  rank degree int(11);

--会议表添加字段
--hsjiang
--2019-07-05
alter table biz_meeting add extern_configs VARCHAR(2048) COMMENT '会议扩展字段';

