delete
from notification_configuration
where notification_definition_id in (
	select id
	from notification_definition
	where id = "INSTITUTION_STARTUP_NOTIFICATION")
;

delete
from state_action_notification
where notification_definition_id in (
	select id
	from notification_definition
	where id = "INSTITUTION_STARTUP_NOTIFICATION")
;  

delete
from notification_definition
where id = "INSTITUTION_STARTUP_NOTIFICATION"
;

update resource_state
set state_id = "INSTITUTION_APPROVED"
where state_id = "INSTITUTION_APPROVED_COMPLETED"
;

update resource_previous_state
set state_id = "INSTITUTION_APPROVED"
where state_id = "INSTITUTION_APPROVED_COMPLETED"
;

delete
from comment_state
where state_id = "INSTITUTION_APPROVED_COMPLETED"
;

delete
from comment_transition_state
where state_id = "INSTITUTION_APPROVED_COMPLETED"
;

update institution
set state_id = "INSTITUTION_APPROVED",
	previous_state_id = "INSTITUTION_APPROVAL"
where state_id = "INSTITUTION_APPROVED_COMPLETED"
;

delete
from comment_state
where comment_id in (
	select id
	from comment
	where action_id = "INSTITUTION_STARTUP")
;

delete
from comment_transition_state
where comment_id in (
	select id
	from comment
	where action_id = "INSTITUTION_STARTUP")
;

delete 
from comment
where action_id = "INSTITUTION_STARTUP"
;

update comment
set state_id = "INSTITUTION_APPROVED"
where state_id = "INSTITUTION_APPROVED_COMPLETED"
;

update comment
set transition_state_id = "INSTITUTION_APPROVED"
where transition_state_id = "INSTITUTION_APPROVED_COMPLETED"
;

delete
from role_transition
where state_transition_id in (
	select id
	from state_transition
	where state_action_id in (
		select id
		from state_action
		where state_id = "INSTITUTION_APPROVED_COMPLETED"
			or action_id = "INSTITUTION_STARTUP"))
;

delete
from state_transition_propagation
where state_transition_id in (
	select id
	from state_transition
	where state_action_id in (
		select id
		from state_action
		where state_id = "INSTITUTION_APPROVED_COMPLETED"
			or action_id = "INSTITUTION_STARTUP"))
;

delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
		or action_id = "INSTITUTION_STARTUP")
;

delete
from state_action_assignment
where state_action_id in (
	select id
	from state_action
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
		or action_id = "INSTITUTION_STARTUP")
;

delete
from state_action_notification
where state_action_id in (
	select id
	from state_action
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
		or action_id = "INSTITUTION_STARTUP")
;

delete
from state_action
where state_id = "INSTITUTION_APPROVED_COMPLETED"
	or action_id = "INSTITUTION_STARTUP"
;

delete
from state
where id = "INSTITUTION_APPROVED_COMPLETED"
;

delete
from action
where id = "INSTITUTION_STARTUP"
;
