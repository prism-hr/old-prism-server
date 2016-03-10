update application inner join comment
	on application.id = comment.application_id
set application.state_id = "APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE",
	application.previous_state_id = "APPLICATION_APPROVED"
where comment.action_id = "APPLICATION_TERMINATE"
	and application.previous_state_id = "APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE"
;

update application inner join comment
	on application.id = comment.application_id
set application.state_id = "APPLICATION_REJECTED",
	application.previous_state_id = "APPLICATION_VALIDATION"
where comment.action_id = "APPLICATION_TERMINATE"
	and application.previous_state_id = "APPLICATION_REJECTED"
;

update application inner join comment
	on application.id = comment.application_id
set application.state_id = "APPLICATION_REFERENCE_PENDING_COMPLETION",
	application.previous_state_id = "APPLICATION_REFERENCE"
where comment.action_id = "APPLICATION_TERMINATE"
	and application.previous_state_id = "APPLICATION_REFERENCE_PENDING_COMPLETION"
;

update application inner join comment
	on application.id = comment.application_id
set application.state_id = "APPLICATION_REVIEW",
	application.previous_state_id = "APPLICATION_REFERENCE_PENDING_COMPLETION"
where comment.action_id = "APPLICATION_TERMINATE"
	and application.previous_state_id = "APPLICATION_REVIEW"
;

update application inner join comment
	on application.id = comment.application_id
set application.state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK",
	application.previous_state_id = "APPLICATION_REVIEW"
where comment.action_id = "APPLICATION_TERMINATE"
	and application.previous_state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
;

update application inner join comment
	on application.id = comment.application_id
set application.state_id = "APPLICATION_REVIEW_PENDING_COMPLETION",
	application.previous_state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
where comment.action_id = "APPLICATION_TERMINATE"
	and application.previous_state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
;

update application inner join comment
	on application.id = comment.application_id
set application.state_id = "APPLICATION_UNSUBMITTED",
	application.previous_state_id = "APPLICATION_UNSUBMITTED"
where comment.action_id = "APPLICATION_TERMINATE"
	and application.previous_state_id = "APPLICATION_UNSUBMITTED"
;

update application inner join resource_state
	on application.id = resource_state.application_id
inner join comment
	on application.id = comment.application_id
set resource_state.state_id = application.state_id
where comment.action_id = "APPLICATION_TERMINATE"
;

update application inner join resource_previous_state
	on application.id = resource_previous_state.application_id
inner join comment
	on application.id = comment.application_id
set resource_previous_state.state_id = application.previous_state_id
where comment.action_id = "APPLICATION_TERMINATE"
;

update user_role inner join (
	select comment.application_id as application_id, 
		comment_assigned_user.user_id as user_id, 
		comment_assigned_user.role_id as role_id
	from comment_assigned_user inner join comment
		on comment_assigned_user.comment_id = comment.id
	where comment.action_id = "APPLICATION_TERMINATE"
		and comment_assigned_user.role_id in ("APPLICATION_VIEWER_REFEREE", "APPLICATION_VIEWER_RECRUITER")
		and comment_assigned_user.role_transition_type = "CREATE") as broken_user_role
	on user_role.application_id = broken_user_role.application_id
	and user_role.user_id = broken_user_role.user_id
	and user_role.role_id = broken_user_role.role_id
set user_role.role_id = if (user_role.role_id = "APPLICATION_VIEWER_REFEREE", "APPLICATION_REFEREE", "APPLICATION_REVIEWER")
;

delete from comment_state
where comment_id in (
	select id
	from comment
	where action_id = "APPLICATION_TERMINATE")
;

delete from comment_transition_state
where comment_id in (
	select id
	from comment
	where action_id = "APPLICATION_TERMINATE")
;

delete from comment_assigned_user
where comment_id in (
	select id
	from comment
	where action_id = "APPLICATION_TERMINATE")
;

delete
from comment
where action_id = "APPLICATION_TERMINATE"
;

delete 
from role_transition
where state_transition_id in (
	select id
	from state_transition
	where state_action_id in (
		select id
		from state_action
		where action_id = "APPLICATION_TERMINATE")
			or transition_action_id = "APPLICATION_TERMINATE")
;

delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where action_id = "APPLICATION_TERMINATE")
		or transition_action_id = "APPLICATION_TERMINATE"
;

delete
from state_action
where action_id = "APPLICATION_TERMINATE"
;

delete 
from state_transition_propagation
where propagated_action_id = "APPLICATION_TERMINATE"
;

delete
from action
where id = "APPLICATION_TERMINATE"
;
