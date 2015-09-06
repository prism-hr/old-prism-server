update comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
set comment.transition_state_id = comment_transition_state.state_id
;

update comment
set state_id = "INSTITUTION_APPROVED",
	transition_state_id = "INSTITUTION_APPROVED"
where id = 75377
;

update comment_state
set state_id = "INSTITUTION_APPROVED"
where comment_id = 75377
;

update comment_transition_state
set state_id = "INSTITUTION_APPROVED"
where comment_id = 75377
;

delete
from comment_assigned_user
where comment_id in (
	select id
	from comment
	where institution_id = 5243
		and action_id = "SYSTEM_CREATE_INSTITUTION")
;

delete
from comment_state
where comment_id in (
	select id
	from comment
	where institution_id = 5243
		and action_id = "SYSTEM_CREATE_INSTITUTION")
;

delete
from comment_transition_state
where comment_id in (
	select id
	from comment
	where institution_id = 5243
		and action_id = "SYSTEM_CREATE_INSTITUTION")
;


delete
from comment
where institution_id = 5243
	and action_id = "SYSTEM_CREATE_INSTITUTION"
;

update resource_list_filter_constraint
set filter_property = replace(filter_property, "_TITLE", "_NAME")
;
