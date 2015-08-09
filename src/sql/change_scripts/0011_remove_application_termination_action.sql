delete role_transition.* 
from state_action inner join state_transition
	on state_action.id = state_transition.state_action_id
inner join role_transition
	on state_transition.id = role_transition.state_transition_id
where state_action.action_id = "APPLICATION_TERMINATE"
;

delete state_termination.* 
from state_action inner join state_transition
	on state_action.id = state_transition.state_action_id
inner join state_termination
	on state_transition.id = state_termination.state_transition_id
where state_action.action_id = "APPLICATION_TERMINATE"
;

delete state_transition.* 
from state_action inner join state_transition
	on state_action.id = state_transition.state_action_id
where state_action.action_id = "APPLICATION_TERMINATE"
;

delete state_action_notification.* 
from state_action inner join state_action_notification
	on state_action.id = state_action_notification.state_action_id
where state_action.action_id = "APPLICATION_TERMINATE"
;

delete from state_action
where action_id = "APPLICATION_TERMINATE"
;

delete comment_state.*
from comment_state inner join comment
	on comment_state.comment_id = comment.id
where comment.action_id = "APPLICATION_TERMINATE"
;

delete comment_transition_state.*
from comment_transition_state inner join comment
	on comment_transition_state.comment_id = comment.id
where comment.action_id = "APPLICATION_TERMINATE"
;

delete comment_assigned_user.*
from comment_assigned_user inner join comment
	on comment_assigned_user.comment_id = comment.id
where comment.action_id = "APPLICATION_TERMINATE"
;

update application inner join comment
	on application.id = comment.application_id
	and comment.action_id = "APPLICATION_TERMINATE"
set application.state_id = comment.state_id,
	application.previous_state_id = comment.state_id,
	application.due_date = if(
		comment.state_id in ("APPLICATION_APPROVAL", "APPLICATION_APPROVED", "APPLICATION_VALIDATION_PENDING_COMPLETION"),
		current_date() + interval 84 day, if(
			comment.state_id in ("APPLICATION_UNSUBMITTED", "APPLICATION_UNSUBMITTED_PENDING_COMPLETION"),
			current_date() + interval 28 day, if(
				comment.state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK",
				current_date() + interval 7 day,
				null)))
;

update application
set due_date = "2014-04-02"
where id = 12018
;

update resource_state inner join application
	on resource_state.application_id = application.id
inner join comment
	on application.id = comment.application_id
	and comment.action_id = "APPLICATION_TERMINATE"
set resource_state.state_id = comment.state_id
where resource_state.primary_state = 1
;

update resource_previous_state inner join application
	on resource_previous_state.application_id = application.id
inner join comment
	on application.id = comment.application_id
	and comment.action_id = "APPLICATION_TERMINATE"
set resource_previous_state.state_id = comment.state_id
where resource_previous_state.primary_state = 1
;

delete state_termination.* 
from state_transition inner join state_termination
	on state_transition.id = state_termination.state_transition_id
where state_transition.transition_action_id = "APPLICATION_TERMINATE"
;

delete role_transition.*
from role_transition inner join state_transition
	on role_transition.state_transition_id = state_transition.id
where state_transition.transition_action_id = "APPLICATION_TERMINATE"
;

delete from state_transition
where transition_action_id = "APPLICATION_TERMINATE"
;

delete comment_state.*
from comment_state inner join comment
	on comment_state.comment_id = comment.id
inner join (
	select application_id
	from comment
	where action_id = "APPLICATION_TERMINATE") as termination_comment
	on termination_comment.application_id = comment.application_id
	and comment.action_id = "APPLICATION_EXPORT"
;

delete comment_transition_state.*
from comment_transition_state inner join comment
	on comment_transition_state.comment_id = comment.id
inner join (
	select application_id
	from comment
	where action_id = "APPLICATION_TERMINATE") as termination_comment
	on termination_comment.application_id = comment.application_id
	and comment.action_id = "APPLICATION_EXPORT"
;

delete comment.*
from comment inner join (
	select application_id
	from comment
	where action_id = "APPLICATION_TERMINATE") as termination_comment
	on termination_comment.application_id = comment.application_id
	and comment.action_id = "APPLICATION_EXPORT"
;

delete from comment
where action_id = "APPLICATION_TERMINATE"
;

delete from state_transition_propagation
where propagated_action_id = "APPLICATION_TERMINATE"
;

delete from action
where id = "APPLICATION_TERMINATE"
;

delete comment_state.*
from comment inner join comment_state
	on comment.id = comment_state.comment_id
where comment.action_id = "APPLICATION_EXPORT"
and comment.created_timestamp >= "2014-12-07 03:33:38"
;

delete comment_transition_state.*
from comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
where comment.action_id = "APPLICATION_EXPORT"
and comment.created_timestamp >= "2014-12-07 03:33:38"
;

delete 
from comment
where action_id = "APPLICATION_EXPORT"
and created_timestamp >= "2014-12-07 03:33:38"
;

update application
set state_id = "APPLICATION_APPROVED_PENDING_EXPORT",
	previous_state_id = "APPLICATION_APPROVED"
where state_id = "APPLICATION_APPROVED_PENDING_CORRECTION"
;

update application
set state_id = "APPLICATION_REJECTED_PENDING_EXPORT",
	previous_state_id = "APPLICATION_REJECTED"
where state_id = "APPLICATION_REJECTED_PENDING_CORRECTION"
;

update application
set state_id = "APPLICATION_WITHDRAWN_PENDING_EXPORT",
	previous_state_id = "APPLICATION_WITHDRAWN_PENDING_EXPORT"
where state_id = "APPLICATION_WITHDRAWN_PENDING_CORRECTION"
;

update application
set state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION",
	due_date = current_date() + interval 28 day
where id in (
	select application_id from comment
	where action_id = "application_escalate"
	and transition_state_id LIKE "%REJECTED%")
;

update resource_state
set state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
where application_id in (
	select application_id from comment
	where action_id = "application_escalate"
	and transition_state_id LIKE "%REJECTED%")
and primary_state = 1
;

update resource_previous_state
set state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
where application_id in (
	select application_id from comment
	where action_id = "application_escalate"
	and transition_state_id LIKE "%REJECTED%")
and primary_state = 1
;

delete comment_assigned_user.*
from comment inner join comment_assigned_user
	on comment.id = comment_assigned_user.comment_id
where comment.action_id = "application_escalate"
	and comment.transition_state_id LIKE "%REJECTED%"
;

delete comment_state.*
from comment inner join comment_state
	on comment.id = comment_state.comment_id
where comment.action_id = "application_escalate"
	and comment.transition_state_id LIKE "%REJECTED%"
;

delete comment_transition_state.*
from comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
where comment.action_id = "application_escalate"
	and comment.transition_state_id LIKE "%REJECTED%"
;

delete 
from comment
where action_id = "application_escalate"
	and transition_state_id LIKE "%REJECTED%"
;

delete application_processing.*
from application_processing inner join application
	on application_processing.application_id = application.id
where application.program_id = 33
;

delete comment_state.*
from comment_state inner join comment 
	on comment_state.comment_id = comment.id
inner join application
	on comment.application_id = application.id
where application.program_id = 33
;

delete comment_transition_state.*
from comment_transition_state inner join comment 
	on comment_transition_state.comment_id = comment.id
inner join application
	on comment.application_id = application.id
where application.program_id = 33
;

delete comment_appointment_preference.*
from comment_appointment_preference inner join comment 
	on comment_appointment_preference.comment_id = comment.id
inner join application
	on comment.application_id = application.id
where application.program_id = 33
;

delete comment_appointment_timeslot.*
from comment_appointment_timeslot inner join comment 
	on comment_appointment_timeslot.comment_id = comment.id
inner join application
	on comment.application_id = application.id
where application.program_id = 33
;

delete comment_assigned_user.*
from comment_assigned_user inner join comment 
	on comment_assigned_user.comment_id = comment.id
inner join application
	on comment.application_id = application.id
where application.program_id = 33
;

delete application_referee.*
from application_referee inner join application
	on application_referee.application_id = application.id
where application.program_id = 33
;

delete application_employment_position.*
from application_employment_position inner join application
	on application_employment_position.application_id = application.id
where application.program_id = 33
;

delete application_funding.*
from application_funding inner join application
	on application_funding.application_id = application.id
where application.program_id = 33
;

delete application_qualification.*
from application_qualification inner join application
	on application_qualification.application_id = application.id
where application.program_id = 33
;

delete application_supervisor.*
from application_supervisor inner join application
	on application_supervisor.application_id = application.id
where application.program_id = 33
;

delete document.*
from document inner join comment
	on document.comment_id = comment.id
inner join application
	on comment.application_id = application.id
where application.program_id = 33
;

delete comment.*
from comment inner join application
	on comment.application_id = application.id
where application.program_id = 33
;

delete resource_state.*
from resource_state inner join application
	on resource_state.application_id = application.id
where application.program_id = 33
;

delete resource_previous_state.*
from resource_previous_state inner join application
	on resource_previous_state.application_id = application.id
where application.program_id = 33
;

delete user_role.*
from user_role inner join application
	on user_role.application_id = application.id
where application.program_id = 33
;

delete from application
where program_id = 33
;

delete from application_processing_summary
where program_id = 33
;

delete from comment_custom_question
where program_id = 33
;

delete comment_transition_state.* 
from comment_transition_state inner join comment
	on comment_transition_state.comment_id = comment.id
where comment.program_id = 33
;

delete comment_state.* 
from comment_state inner join comment
	on comment_state.comment_id = comment.id
where comment.program_id = 33
;

delete program_study_option_instance.*
from program_study_option_instance inner join program_study_option
	on program_study_option_instance.program_study_option_id = program_study_option.id
where program_study_option.program_id = 33
;

delete 
from program_study_option
where program_id = 33
;

delete 
from comment
where program_id = 33
;

delete comment_state.*
from comment_state inner join comment 
	on comment_state.comment_id = comment.id
inner join project
	on comment.project_id = project.id
where project.program_id = 33
;

delete comment_transition_state.*
from comment_transition_state inner join comment 
	on comment_transition_state.comment_id = comment.id
inner join project
	on comment.project_id = project.id
where project.program_id = 33
;

delete comment.*
from comment inner join project
	on comment.project_id = project.id
where project.program_id = 33
;

delete resource_previous_state.*
from resource_previous_state inner join project
	on resource_previous_state.project_id = project.id
where project.program_id = 33
;

delete resource_state.*
from resource_state inner join project
	on resource_state.project_id = project.id
where project.program_id = 33
;

delete user_role.*
from user_role inner join project
	on user_role.project_id = project.id
where project.program_id = 33
;

delete
from project
where program_id = 33
;

delete
from resource_state
where program_id = 33
;

delete
from resource_previous_state
where program_id = 33
;

delete
from user_role
where program_id = 33
;

delete 
from program
where id = 33
;

