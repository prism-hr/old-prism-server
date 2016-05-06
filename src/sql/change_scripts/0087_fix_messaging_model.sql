alter table message_recipient
	drop index viewed,
	drop column viewed,
	add column send_timestamp datetime,
	add column view_timestamp datetime
;

alter table message_recipient
	add index (send_timestamp),
	add index (view_timestamp)
;

alter table message_recipient
	modify column role_id varchar(50) after user_id,
	modify column user_id int(10) unsigned
;

alter table state_action_recipient
	drop primary key,
	drop column id,
	drop index state_action_assignment_id,
	add unique index (state_action_assignment_id, role_id)
;

set foreign_key_checks = 0
;

update action
set id = replace(id, "EMAIL_CREATOR", "SEND_MESSAGE")
;

update action
set fallback_action_id = replace(fallback_action_id, 'EMAIL_CREATOR', 'SEND_MESSAGE')
;

update action_redaction
set action_id = replace(action_id, 'EMAIL_CREATOR', 'SEND_MESSAGE')
;

update comment
set action_id = replace(action_id, 'EMAIL_CREATOR', 'SEND_MESSAGE')
;

update state_action
set action_id = replace(action_id, 'EMAIL_CREATOR', 'SEND_MESSAGE')
;

update state_transition
set transition_action_id = replace(transition_action_id, 'EMAIL_CREATOR', 'SEND_MESSAGE')
;

update state_transition_pending
set action_id = replace(action_id, 'EMAIL_CREATOR', 'SEND_MESSAGE')
;

update state_transition_propagation
set propagated_action_id = replace(propagated_action_id, 'EMAIL_CREATOR', 'SEND_MESSAGE')
;

update user_feedback
set action_id = replace(action_id, 'EMAIL_CREATOR', 'SEND_MESSAGE')
;

set foreign_key_checks = 1
;
