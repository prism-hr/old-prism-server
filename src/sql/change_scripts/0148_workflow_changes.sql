set foreign_key_checks = 0
;

update comment_assigned_user
set role_id = replace(role_id, "ADMITTER", "APPROVER")
;

update role_transition
set role_id = replace(role_id, "ADMITTER", "APPROVER")
;

update role_transition
set transition_role_id = replace(role_id, "ADMITTER", "APPROVER")
;

update user_role
set role_id = replace(role_id, "ADMITTER", "APPROVER")
;

update state_action_assignment
set role_id = replace(role_id, "ADMITTER", "APPROVER")
;

update state_action_notification
set role_id = replace(role_id, "ADMITTER", "APPROVER")
;

set foreign_key_checks = 1
;

delete
from action_redaction
where role_id like "%ADMITTER"
;

delete
from role
where id like "%ADMITTER"
;

delete
from comment_transition_state
where comment_id in (
	select id
	from comment
	where action_id like "%SUSPEND")
;


delete
from comment_state
where comment_id in (
	select id
	from comment
	where action_id like "%SUSPEND")
;

delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where action_id like "%SUSPEND")
;

delete
from state_transition_propagation
where state_transition_id in (
	select id
	from state_transition
	where state_action_id in (
		select id
		from state_action
		where action_id like "%SUSPEND"))
	or propagated_action_id like "%SUSPEND"
;

delete
from state_action
where action_id like "%SUSPEND"
;

delete
from comment
where action_id like "%SUSPEND"
;

delete
from action
where id like "%SUSPEND"
;

delete
from comment_state
where comment_id in (
  select id
  from comment
  where state_id like "%REACTIVATION"
        or transition_state_id like "%REACTIVATION")
      or state_id like "%REACTIVATION"
;

delete
from comment_transition_state
where comment_id in (
  select id
  from comment
  where state_id like "%REACTIVATION"
        or transition_state_id like "%REACTIVATION")
      or state_id like "%REACTIVATION"
;

delete
from comment
where state_id like "%REACTIVATION"
	or transition_state_id like "%REACTIVATION"
;

update program inner join resource_state
    on program.id = resource_state.program_id
set program.state_id = 'PROGRAM_DISABLED_COMPLETED',
  resource_state.state_id = 'PROGRAM_DISABLED_COMPLETED'
where program.state_id = 'PROGRAM_DISABLED_PENDING_REACTIVATION'
      and resource_state.state_id = 'PROGRAM_DISABLED_PENDING_REACTIVATION'
;

update program inner join resource_previous_state
	on program.id = resource_previous_state.program_id
set program.previous_state_id = program.state_id,
	resource_previous_state.state_id = program.state_id
where program.previous_state_id like "%REACTIVATION"
	or resource_previous_state.state_id like "%REACTIVATION"
;

update project inner join resource_previous_state
	on project.id = resource_previous_state.project_id
set project.previous_state_id = project.state_id,
	resource_previous_state.state_id = project.state_id
where project.previous_state_id like "%REACTIVATION"
	or resource_previous_state.state_id like "%REACTIVATION"
;

delete
from state_transition_propagation
where state_transition_id in (
	select id
	from state_transition
	where state_action_id in (
		select id
		from state_action
		where state_id like "%REACTIVATION")
		or transition_state_id like "%REACTIVATION")
;

delete
from role_transition
where state_transition_id in (
	select id
	from state_transition
	where  state_action_id in (
	select id
	from state_action
	where state_id like "%REACTIVATION")
	or transition_state_id like "%REACTIVATION")
;

delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where state_id like "%REACTIVATION")
	or transition_state_id like "%REACTIVATION"
;

delete
from state_action_assignment
where state_action_id in (
	select id
	from state_action
	where state_id like "%REACTIVATION")
;

delete
from state_action_notification
where state_action_id in (
	select id
	from state_action
	where state_id like "%REACTIVATION")
;


delete
from state_action
where state_id like "%REACTIVATION"
;

delete
from state
where id like "%REACTIVATION"
;

set foreign_key_checks = 0
;

update state_transition
set transition_state_id = replace(transition_state_id, "APPROVAL_INSTITUTION", "APPROVAL_PARENT")
;

update state_action
set state_id = replace(state_id, "APPROVAL_INSTITUTION", "APPROVAL_PARENT")
;

set foreign_key_checks = 1
;

update state
set id = replace(id, "APPROVAL_INSTITUTION", "APPROVAL_PARENT")
;

set foreign_key_checks = 0
;

update state
set state_group_id = replace(state_group_id, "APPROVAL_INSTITUTION", "APPROVAL_PARENT")
;

set foreign_key_checks = 1
;

update state_group
set id = replace(id, "APPROVAL_INSTITUTION", "APPROVAL_PARENT")
;
