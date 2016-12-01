delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where state_id in (
		select id
		from state 
		where id = "APPLICATION_UNSUBMITTED_PENDING_COMPLETION"))
	or transition_state_id = "APPLICATION_UNSUBMITTED_PENDING_COMPLETION"
;

delete
from state_action_assignment
where state_action_id in (
	select id
	from state_action
	where state_id in (
		select id
		from state 
		where id = "APPLICATION_UNSUBMITTED_PENDING_COMPLETION"))
;

delete
from state_action_notification
where state_action_id in (
	select id
	from state_action
	where state_id in (
		select id
		from state 
		where id = "APPLICATION_UNSUBMITTED_PENDING_COMPLETION"))
;

delete
from state_action
where state_id in (
	select id
	from state 
	where id = "APPLICATION_UNSUBMITTED_PENDING_COMPLETION")
;

delete
from state 
where id = "APPLICATION_UNSUBMITTED_PENDING_COMPLETION"
;

set session foreign_key_checks = 0
;

update action_redaction
set action_id = "APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL"
where action_id = "APPLICATION_CONFIRM_MANAGEMENT"
;

update comment
set action_id = "APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL"
where action_id = "APPLICATION_CONFIRM_MANAGEMENT"
;

update state_action
set action_id = "APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL"
where action_id = "APPLICATION_CONFIRM_MANAGEMENT"
;

update state_transition
set transition_action_id = "APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL"
where transition_action_id = "APPLICATION_CONFIRM_MANAGEMENT"
;

update state_transition_pending
set action_id = "APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL"
where action_id = "APPLICATION_CONFIRM_MANAGEMENT"
;

update state_transition_propagation
set propagated_action_id = "APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL"
where propagated_action_id = "APPLICATION_CONFIRM_MANAGEMENT"
;

update user_feedback
set action_id = "APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL"
where action_id = "APPLICATION_CONFIRM_MANAGEMENT"
;

update action
set id = "APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL"
where id = "APPLICATION_CONFIRM_MANAGEMENT"
;

update action
set fallback_action_id = "APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL"
where fallback_action_id = "APPLICATION_CONFIRM_MANAGEMENT"
;

set session foreign_key_checks = 1
;
