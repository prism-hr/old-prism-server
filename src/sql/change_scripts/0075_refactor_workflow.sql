delete
from user_role
where role_id = "APPLICATION_SUGGESTED_SUPERVISOR"
;

delete
from role_transition
where "APPLICATION_SUGGESTED_SUPERVISOR" in (role_id, transition_role_id)
;

delete
from comment_assigned_user
where role_id = "APPLICATION_SUGGESTED_SUPERVISOR"
;

delete
from role
where id = "APPLICATION_SUGGESTED_SUPERVISOR"
;

alter table state_group
	drop index scope_id,
	add index (scope_id)
;

update state_group
set sequence_order = sequence_order - 1
;

alter table state_group
	drop index scope_id,
	change column sequence_order ordinal int(2) unsigned not null,
	add unique index (scope_id, ordinal)
;

alter table state_action
	drop column is_default_action
;

update comment
set application_export_exception = replace(application_export_exception, 
	"No export program instance for application", "SYSTEM_NO_PROGRAM_INSTANCE")
where application_export_exception is not null
;

alter table action
	modify column transition_action int(1) unsigned
;

alter table state_group
	modify column repeatable int(1) unsigned
;

alter table state
	modify column hidden int(1) unsigned
;

alter table state
	modify column parallelizable int(1) unsigned
;

alter table action
	drop column emphasized_action
;

set foreign_key_checks = 0
;

update comment
set action_id = "APPLICATION_COMPLETE_STAGE"
where action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
;

update state_action
set action_id = "APPLICATION_COMPLETE_STAGE"
where action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
;

update state_action_assignment
set delegated_action_id = "APPLICATION_COMPLETE_STAGE"
where delegated_action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
;

update state_transition
set transition_action_id = "APPLICATION_COMPLETE_STAGE"
where transition_action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
;

update state_transition_propagation
set propagated_action_id = "APPLICATION_COMPLETE_STAGE"
where propagated_action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
;

update action_redaction
set action_id = "APPLICATION_COMPLETE_STAGE"
where action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
;

update user_feedback
set action_id = "APPLICATION_COMPLETE_STAGE"
where action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
;

update action
set fallback_action_id = "APPLICATION_COMPLETE_STAGE"
where fallback_action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
;

update action
set id = "APPLICATION_COMPLETE_STAGE"
where id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
;

set foreign_key_checks = 1
;

set foreign_key_checks = 0
;

update comment
set action_id = "APPLICATION_COMPLETE_STAGE"
where action_id like "APPLICATION_COMPLETE_%"
;

update state_action
set action_id = "APPLICATION_COMPLETE_STAGE"
where action_id like "APPLICATION_COMPLETE_%"
;

update state_action_assignment
set delegated_action_id = "APPLICATION_COMPLETE_STAGE"
where delegated_action_id like "APPLICATION_COMPLETE_%"
;

update state_transition
set transition_action_id = "APPLICATION_COMPLETE_STAGE"
where transition_action_id like "APPLICATION_COMPLETE_%"
;

update state_transition_propagation
set propagated_action_id = "APPLICATION_COMPLETE_STAGE"
where propagated_action_id like "APPLICATION_COMPLETE_%"
;

delete
from action_redaction
where action_id like "APPLICATION_COMPLETE_%"
	and action_id != "APPLICATION_COMPLETE_STAGE"
;

update user_feedback
set action_id = "APPLICATION_COMPLETE_STAGE"
where action_id like "APPLICATION_COMPLETE_%"
;

update action
set fallback_action_id = "APPLICATION_COMPLETE_STAGE"
where fallback_action_id like "APPLICATION_COMPLETE_%"
;

delete
from action
where id like "APPLICATION_COMPLETE_%"
	and id != "APPLICATION_COMPLETE_STAGE"
;

set foreign_key_checks = 1
;

