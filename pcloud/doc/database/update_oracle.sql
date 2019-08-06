--会议表添加字段
--hsjiang
--2019-07-05
alter table biz_meeting add extern_configs VARCHAR2(2048);
comment on column biz_meeting.extern_configs is '会议扩展字段';

--各表创建时间添加默认值
--hsjiang
--2019-07-06
ALTER TABLE acd_calls MODIFY (created_time DEFAULT sysdate );
ALTER TABLE acd_evaluation MODIFY (created_time DEFAULT sysdate );
ALTER TABLE acd_evaluation_score MODIFY (created_time DEFAULT sysdate );
ALTER TABLE acd_queue MODIFY (created_time DEFAULT sysdate );
ALTER TABLE acd_terminal MODIFY (created_time DEFAULT sysdate );
ALTER TABLE biz_meeting MODIFY (created_time DEFAULT sysdate );

ALTER TABLE sys_dept MODIFY (create_time DEFAULT sysdate );
ALTER TABLE sys_menu MODIFY (create_time DEFAULT sysdate );
ALTER TABLE sys_dict MODIFY (create_time DEFAULT sysdate );
ALTER TABLE sys_role MODIFY (create_time DEFAULT sysdate );
ALTER TABLE sys_user MODIFY (create_time DEFAULT sysdate );

--通话记录表字段有误
--hsjiang
--2019-07-08
ALTER TABLE ACD_CALLS RENAME COLUMN "HANUP_TIME" TO "HANGUP_TIME";
alter table ACD_CALLS add call_status VARCHAR2(64);
comment on column ACD_CALLS.call_status is '通话状态,CALL_LINE:呼叫中,CALL_RING:响铃中,CALL_CONNECT:已接通,CALL_HANGUP:正常挂断,CALL_TIMEOUT:呼叫超时';
comment on column ACD_CALLS.HANGUP_TIME is '坐席或客户挂断通话时间';