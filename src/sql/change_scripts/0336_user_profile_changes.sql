alter table application
	drop foreign key application_ibfk_9,
	drop index scope_id,
	drop column scope_id
;

alter table comment
	drop foreign key comment_ibfk_20,
	drop index resume_id,
	drop column resume_id
;

alter table resource_condition
	drop foreign key resource_condition_ibfk_7,
	drop index resume_id,
	drop column resume_id
;

alter table resource_previous_state
	drop foreign key resource_previous_state_ibfk_8,
	drop index resume_id,
	drop column resume_id
;

alter table resource_state
	drop foreign key resource_state_ibfk_8,
	drop index resume_id,
	drop column resume_id
;

alter table user_feedback
	drop foreign key user_feedback_ibfk_9,
	drop index resume_id,
	drop column resume_id
;

alter table user_notification
	drop foreign key user_notification_ibfk_8,
	drop index resume_id,
	drop column resume_id
;

alter table user_role
	drop foreign key user_role_ibfk_9,
	drop index resume_id,
	drop column resume_id
;

alter table state_transition_pending
	drop foreign key state_transition_pending_ibfk_8,
	drop index resume_id,
	drop column resume_id
;

delete 
from role_transition
where state_transition_id in (
	select id
	from state_transition
	where state_action_id in (
		select id
		from state_action
		where action_id in (
			select id
			from action
			where id like "%RESUME%")
			or transition_action_id in (
				select id
				from action
				where id like "%RESUME%")))
;


delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where action_id in (
		select id
		from action
		where id like "%RESUME%")
		or transition_action_id in (
			select id
			from action
			where id like "%RESUME%"))
;

delete
from state_action_assignment
where state_action_id in (
	select id
	from state_action
	where action_id in (
		select id
		from action
		where id like "%RESUME%"))
;

delete
from state_action
where action_id in (
	select id
	from action
	where id like "%RESUME%")
;

delete
from action
where id like "%RESUME%"
;

delete
from role
where scope_id = "RESUME"
;

delete
from state
where scope_id = "RESUME"
;

delete
from state_group
where scope_id = "RESUME"
;

delete
from workflow_property_configuration
where workflow_property_definition_id in (
	select id
	from workflow_property_definition
	where scope_id = "RESUME")
;

delete
from workflow_property_definition
where scope_id = "RESUME"
;


delete
from state_transition_evaluation
where scope_id = "RESUME"
;

delete
from scope
where id = "RESUME"
;
