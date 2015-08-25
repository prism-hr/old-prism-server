update application inner join comment
	on application.id = comment.application_id
set application.state_id = comment.state_id,
	application.previous_state_id = comment.state_id
where application.id in (14868, 15695, 15973, 16100, 16299, 16309)
	and comment.action_id = "APPLICATION_ESCALATE"
;

update resource_state inner join application
	on resource_state.application_id = application.id
set resource_state.state_id = application.state_id
where resource_state.primary_state = 1
	and application.id in (14868, 15695, 15973, 16100, 16299, 16309)
;

delete 
from comment_state
where comment_id in (
	select id
	from comment 
	where application_id in (14868, 15695, 15973, 16100, 16299, 16309)
		and action_id = "APPLICATION_ESCALATE")
;

delete 
from comment_transition_state
where comment_id in (
	select id
	from comment 
	where application_id in (14868, 15695, 15973, 16100, 16299, 16309)
		and action_id = "APPLICATION_ESCALATE")
;

delete 
from comment_assigned_user
where comment_id in (
	select id
	from comment 
	where application_id in (14868, 15695, 15973, 16100, 16299, 16309)
		and action_id = "APPLICATION_ESCALATE")
;

delete
from comment
where application_id in (14868, 15695, 15973, 16100, 16299, 16309)
	and action_id = "APPLICATION_ESCALATE"
;

update application inner join resource_state
	on application.id = resource_state.application_id
set application.state_id = resource_state.state_id
where application.state_id = "application_unsubmitted"
	and resource_state.primary_state = 1
;

update application
set due_date = date(created_timestamp) + interval 84 day
where state_id = "APPLICATION_UNSUBMITTED"
;

update application
set due_date = date(created_timestamp) + interval 168 day
where state_id = "APPLICATION_UNSUBMITTED_PENDING_COMPLETION"
;

update application
set state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION",
	previous_state_id = "APPLICATION_UNSUBMITTED",
	due_date = (date(current_timestamp()) + interval 84 day)
where closing_date is null
	and state_id = "APPLICATION_VALIDATION"
;

update application inner join resource_state
	on application.id = resource_state.application_id
set resource_state.state_id = application.state_id
where resource_state.primary_state = 1
	and resource_state.state_id != application.state_id
;

update application inner join (
	select application_id as application_id,
		max(comment.created_timestamp) as created_timestamp
	from comment inner join action
		on comment.action_id = action.id
	where action.action_type = "USER_INVOCATION"
	group by comment.application_id) latest_user_comment
	on application.id = latest_user_comment.application_id
set application.updated_timestamp = latest_user_comment.created_timestamp,
	application.sequence_identifier = concat(unix_timestamp(latest_user_comment.created_timestamp), lpad(application.id, 10, "0"))
;

update application
set due_date = current_date() + interval 7 day
where state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
;

update application
set state_id = "APPLICATION_APPROVAL_PENDING_COMPLETION"
where id = 14767
;

update resource_state
set state_id = "APPLICATION_APPROVAL_PENDING_COMPLETION"
where application_id = 14767
;

delete comment_state.*
from comment_state
where comment_id in (
	select id
	from comment
	where action_id = "APPLICATION_EXPORT"
		and created_timestamp > "2014-12-10 00:00:00")
;

delete comment_transition_state.*
from comment_transition_state
where comment_id in (
	select id
	from comment
	where action_id = "APPLICATION_EXPORT"
		and created_timestamp > "2014-12-10 00:00:00")
;

delete
from comment
where action_id = "APPLICATION_EXPORT"
	and created_timestamp > "2014-12-10 00:00:00"
;
