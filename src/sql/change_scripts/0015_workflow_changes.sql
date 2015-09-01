update comment
set state_id = replace(state_id, "_IMPORT_", "_"),
	transition_state_id = REPLACE(state_id, "_IMPORT_", "_")
where state_id like "%_IMPORT_%"
	or transition_state_id like "%_IMPORT_%" 
;

update comment_transition_state
set state_id = REPLACE(state_id, "_IMPORT_", "_")
where state_id like "%_IMPORT_%"
;

update comment_state
set state_id = REPLACE(state_id, "_IMPORT_", "_")
where state_id like "%_IMPORT_%"
;

update comment
set state_id = replace(state_id, "_PROGRAM_REACTIVATION", "_REACTIVATION"),
	transition_state_id = REPLACE(state_id, "_PROGRAM_REACTIVATION", "_REACTIVATION")
where state_id like "%_PROGRAM_REACTIVATION"
	or transition_state_id like "%_PROGRAM_REACTIVATION"
;

update comment_transition_state
set state_id = replace(state_id, "_PROGRAM_REACTIVATION", "_REACTIVATION")
where state_id like "%_PROGRAM_REACTIVATION"
;

update comment_state
set state_id = replace(state_id, "_PROGRAM_REACTIVATION", "_REACTIVATION")
where state_id like "%_PROGRAM_REACTIVATION"
;

update resource_previous_state
set state_id = REPLACE(state_id, "_IMPORT_", "_")
where state_id like "%_IMPORT_%"
;

update resource_state
set state_id = REPLACE(state_id, "_IMPORT_", "_")
where state_id like "%_IMPORT_%"
;

update resource_previous_state
set state_id = replace(state_id, "_PROGRAM_REACTIVATION", "_REACTIVATION")
where state_id like "%_PROGRAM_REACTIVATION"
;

update resource_state
set state_id = replace(state_id, "_PROGRAM_REACTIVATION", "_REACTIVATION")
where state_id like "%_PROGRAM_REACTIVATION"
;

update program
set state_id = replace(state_id, "_IMPORT_", "_"),
	previous_state_id = REPLACE(state_id, "_IMPORT_", "_")
where state_id like "%_IMPORT_%"
	or previous_state_id like "%_IMPORT_%" 
;

update project
set state_id = replace(state_id, "_PROGRAM_REACTIVATION", "_REACTIVATION"),
	previous_state_id = REPLACE(state_id, "_PROGRAM_REACTIVATION", "_REACTIVATION")
where state_id like "%_PROGRAM_REACTIVATION"
	or previous_state_id like "%_PROGRAM_REACTIVATION"
;

delete from state_transition_propagation
where state_transition_id in (
	select id 
	from state_transition
	where state_action_id in (
		select id 
		from state_action
		where state_id in ("PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION", "PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION")))
;

delete from state_transition
where state_action_id in (
	select id 
	from state_action
	where state_id in ("PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION", "PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION"))
;

delete from state_transition_propagation
where state_transition_id in (
	select id 
	from state_transition
	where state_action_id in (
		select id 
		from state_action
		where transition_state_id in ("PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION", "PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION")))
;

delete from state_transition
where state_action_id in (
	select id 
	from state_action
	where transition_state_id in ("PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION", "PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION"))
;

delete from state_action_assignment
where state_action_id in (
	select id 
	from state_action
	where state_id in ("PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION", "PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION"))
;

delete from state_action_notification
where state_action_id in (
	select id 
	from state_action
	where state_id in ("PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION", "PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION"))
;

delete from state_action
where state_id in ("PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION", "PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION")
;

delete from state
where id in ("PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION", "PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION")
;

delete
from state_transition_propagation
where state_transition_id in (
	select id
	from state_transition
	where state_action_id in (
		select id
		from state_action
		where action_id in ("PROGRAM_RESTORE", "PROJECT_RESTORE")))
;

delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where action_id in ("PROGRAM_RESTORE", "PROJECT_RESTORE"))
;

delete
from state_action_notification
where state_action_id in (
	select id
	from state_action
	where action_id in ("PROGRAM_RESTORE", "PROJECT_RESTORE"))
;

delete
from state_action
where action_id in ("PROGRAM_RESTORE", "PROJECT_RESTORE")
;

delete
from action
where id in ("PROGRAM_RESTORE", "PROJECT_RESTORE")
;

delete 
from state_transition_propagation
where state_transition_id in (
	select id
	from state_transition
	where state_transition_evaluation_id in (
		select id 
		from state_transition_evaluation
		where id in ("PROGRAM_RESTORED_OUTCOME", "PROJECT_RESTORED_OUTCOME", "PROGRAM_EXPIRED_OUTCOME")))
;

delete
from state_transition
where state_transition_evaluation_id in (
	select id 
	from state_transition_evaluation
	where id in ("PROGRAM_RESTORED_OUTCOME", "PROJECT_RESTORED_OUTCOME", "PROGRAM_EXPIRED_OUTCOME"))
;

delete
from state_transition_evaluation
where id in ("PROGRAM_RESTORED_OUTCOME", "PROJECT_RESTORED_OUTCOME", "PROGRAM_EXPIRED_OUTCOME")
;
